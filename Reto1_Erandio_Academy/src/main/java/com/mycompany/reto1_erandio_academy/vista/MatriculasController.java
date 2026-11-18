package com.mycompany.reto1_erandio_academy.vista;
import com.mycompany.reto1_erandio_academy.controlador.Gestor;

import com.mycompany.reto1_erandio_academy.controlador.DAOImpl;

import com.mycompany.reto1_erandio_academy.controlador.SesionUsuario;
import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.modelo.Curso;
import com.mycompany.reto1_erandio_academy.modelo.Matricula;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;

public class MatriculasController implements Initializable {

    private DAOImpl usuarioDAO;
    private DAOImpl cursoDAO;
    private Gestor servicio;

    private Usuario usuarioActual;
    private boolean esAdministrador;

    private final Map<String, String> nombresUsuarios = new HashMap<>();
    private final Map<String, String> nombresCursos = new HashMap<>();

    @FXML private VBox contenido;
    @FXML private Label titulo;
    @FXML private VBox bloqueAdmin;
    @FXML private ComboBox<Usuario> usuarios;
    @FXML private ComboBox<Curso> cursos;
    @FXML private Button matricular;
    @FXML private TableView<Matricula> tabla;
    @FXML private TableColumn<Matricula, String> usuarioColumna;
    @FXML private TableColumn<Matricula, String> cursoColumna;
    @FXML private TableColumn<Matricula, String> fechaColumna;
    @FXML private TableColumn<Matricula, String> estadoColumna;
    @FXML private Button actualizar;
    @FXML private Button eliminar;

    private void registrarLog(String operacion, String resultado, String error) {
        Gestor logServicio = new Gestor();
        try {
            logServicio.registrarLog(operacion, "matriculas", resultado, error);
        } finally {
            logServicio.cerrar();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        usuarioActual = SesionUsuario.getUsuarioActual();

        if (usuarioActual == null) {
            mostrarMensaje(Alert.AlertType.WARNING, "No hay ningún usuario conectado.");
            bloqueAdmin.setDisable(true);
            tabla.setDisable(true);
            actualizar.setDisable(true);
            return;
        }

        esAdministrador = SesionUsuario.tieneRol("ADMIN");

        usuarioDAO = new DAOImpl();
        cursoDAO = new DAOImpl();
        servicio = new Gestor();

        crearColumnas();

        if (esAdministrador) {

            bloqueAdmin.setVisible(true);
            bloqueAdmin.setManaged(true);
            eliminar.setVisible(true);
            eliminar.setManaged(true);

            usuarios.setItems(FXCollections.observableArrayList(usuarioDAO.listarUsuarios()));
            cursos.setItems(FXCollections.observableArrayList(cursoDAO.listarCursos()));

            usuarios.setCellFactory(lista -> new ListCell<Usuario>() {
                @Override
                protected void updateItem(Usuario usuario, boolean vacio) {
                    super.updateItem(usuario, vacio);
                    setText(vacio || usuario == null ? null : usuario.getNombre());
                }
            });
            usuarios.setButtonCell(new ListCell<Usuario>() {
                @Override
                protected void updateItem(Usuario usuario, boolean vacio) {
                    super.updateItem(usuario, vacio);
                    setText(vacio || usuario == null ? null : usuario.getNombre());
                }
            });

            cursos.setCellFactory(lista -> new ListCell<Curso>() {
                @Override
                protected void updateItem(Curso curso, boolean vacio) {
                    super.updateItem(curso, vacio);
                    setText(vacio || curso == null ? null : curso.getNombre());
                }
            });
            cursos.setButtonCell(new ListCell<Curso>() {
                @Override
                protected void updateItem(Curso curso, boolean vacio) {
                    super.updateItem(curso, vacio);
                    setText(vacio || curso == null ? null : curso.getNombre());
                }
            });
        }

        cargarNombres();
        cargarMatriculas();

        contenido.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((o2, oldW, newW) -> {
                    if (newW != null) {
                        newW.addEventHandler(WindowEvent.WINDOW_HIDDEN, e -> {
                            usuarioDAO.cerrar();
                            cursoDAO.cerrar();
                            servicio.cerrar();
                        });
                    }
                });
            }
        });
    }

    @FXML
    private void onMatricular() {

        Usuario usuario = usuarios.getValue();
        Curso curso = cursos.getValue();

        try {
            if (usuario == null || curso == null) {
                mostrarMensaje(Alert.AlertType.WARNING, "Selecciona un usuario y un curso.");
                return;
            }

            servicio.matricular(usuario.getId(), curso.getId());
            registrarLog("INSERT", "OK", null);
            cargarMatriculas();
            mostrarMensaje(Alert.AlertType.INFORMATION, "Usuario matriculado correctamente.");

        } catch (Exception ex) {
            registrarLog("INSERT", "ERROR", ex.getMessage());
            mostrarMensaje(Alert.AlertType.ERROR, ex.getMessage());
        }
    }

    @FXML
    private void onActualizar() {
        cargarNombres();
        cargarMatriculas();
        tabla.refresh();
    }

    @FXML
    private void onEliminar() {

        Matricula seleccionada = tabla.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarMensaje(Alert.AlertType.WARNING, "Selecciona una matrícula.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Dar de baja");
        confirmacion.setHeaderText("¿Estás seguro?");
        confirmacion.setContentText("¿Quieres eliminar esta matrícula?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {

            try {
                servicio.eliminarMatricula(seleccionada.getId());
                registrarLog("DELETE", "OK", null);
                cargarMatriculas();
                mostrarMensaje(Alert.AlertType.INFORMATION, "Matrícula eliminada correctamente.");

            } catch (Exception ex) {
                registrarLog("DELETE", "ERROR", ex.getMessage());
                mostrarMensaje(Alert.AlertType.ERROR, ex.getMessage());
            }
        }
    }

    private void cargarNombres() {

        nombresUsuarios.clear();
        nombresCursos.clear();

        for (Usuario usuario : usuarioDAO.listarUsuarios()) {
            nombresUsuarios.put(usuario.getId().toString(), usuario.getNombre());
        }

        for (Curso curso : cursoDAO.listarCursos()) {
            nombresCursos.put(curso.getId().toString(), curso.getNombre());
        }
    }

    private void crearColumnas() {

        usuarioColumna.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        nombresUsuarios.getOrDefault(d.getValue().getUsuarioId().toString(), "Usuario desconocido")));

        cursoColumna.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        nombresCursos.getOrDefault(d.getValue().getCursoId().toString(), "Curso desconocido")));

        fechaColumna.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getFecha()));

        estadoColumna.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getEstado()));
    }

    private void cargarMatriculas() {

        if (esAdministrador) {
            tabla.setItems(FXCollections.observableArrayList(servicio.listarMatriculas()));
        } else {
            tabla.setItems(FXCollections.observableArrayList(servicio.listarMatriculasPorUsuario(usuarioActual.getId())));
        }
    }

    private void mostrarMensaje(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Academy");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
