package com.mycompany.reto1_erandio_academy.vista;
import com.mycompany.reto1_erandio_academy.controlador.Gestor;

import com.mycompany.reto1_erandio_academy.controlador.DAOImpl;

import com.mycompany.reto1_erandio_academy.modelo.Curso;
import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.modelo.Matricula;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;

public class BajaMatriculaController implements Initializable {

    private final DAOImpl usuarioDAO = new DAOImpl();
    private final DAOImpl cursoDAO = new DAOImpl();
    private final Gestor matriculaServicio = new Gestor();

    private final Map<String, String> nombresCursos = new HashMap<>();

    @FXML private VBox contenido;
    @FXML private Label titulo;
    @FXML private ComboBox<Usuario> comboUsuarios;
    @FXML private TableView<Matricula> tablaMatriculas;
    @FXML private TableColumn<Matricula, String> columnaCurso;
    @FXML private TableColumn<Matricula, String> columnaFecha;
    @FXML private TableColumn<Matricula, String> columnaEstado;
    @FXML private Button botonBaja;
    @FXML private Button botonActualizar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        comboUsuarios.setConverter(new StringConverter<Usuario>() {
            @Override
            public String toString(Usuario usuario) {
                return usuario == null ? "" : usuario.getNombre();
            }

            @Override
            public Usuario fromString(String texto) {
                return null;
            }
        });

        comboUsuarios.setOnAction(e -> cargarMatriculas());

        columnaCurso.setCellValueFactory(dato ->
                new javafx.beans.property.SimpleStringProperty(
                        nombresCursos.getOrDefault(
                                dato.getValue().getCursoId().toString(), "Curso desconocido")));
        columnaFecha.setCellValueFactory(dato ->
                new javafx.beans.property.SimpleStringProperty(dato.getValue().getFecha()));
        columnaEstado.setCellValueFactory(dato ->
                new javafx.beans.property.SimpleStringProperty(dato.getValue().getEstado()));

        cargarUsuarios();
        cargarNombresCursos();

        contenido.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((o2, oldW, newW) -> {
                    if (newW != null) {
                        newW.addEventHandler(WindowEvent.WINDOW_HIDDEN, e -> {
                            matriculaServicio.cerrar();
                            usuarioDAO.cerrar();
                            cursoDAO.cerrar();
                        });
                    }
                });
            }
        });
    }

    private void cargarUsuarios() {
        List<Usuario> usuarios = usuarioDAO.listarUsuarios();
        comboUsuarios.setItems(FXCollections.observableArrayList(usuarios));
    }

    private void cargarNombresCursos() {
        nombresCursos.clear();
        for (Curso curso : cursoDAO.listarCursos()) {
            nombresCursos.put(curso.getId().toString(), curso.getNombre());
        }
    }

    private void cargarMatriculas() {

        Usuario usuario = comboUsuarios.getValue();

        if (usuario == null) {
            tablaMatriculas.getItems().clear();
            return;
        }

        List<Matricula> matriculas = matriculaServicio.listarMatriculasPorUsuario(usuario.getId());
        tablaMatriculas.setItems(FXCollections.observableArrayList(matriculas));
    }

    @FXML
    private void onActualizar() {
        cargarNombresCursos();
        cargarMatriculas();
    }

    @FXML
    private void onBaja() {

        Matricula matricula = tablaMatriculas.getSelectionModel().getSelectedItem();

        if (matricula == null) {
            mostrarMensaje(Alert.AlertType.WARNING, "Selecciona una matrícula.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar baja");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Quieres darte de baja de este curso?");

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {

            boolean resultado = matriculaServicio.eliminarMatricula(matricula.getId());

            if (resultado) {
                mostrarMensaje(Alert.AlertType.INFORMATION, "Matrícula eliminada correctamente.");
                cargarMatriculas();
            } else {
                mostrarMensaje(Alert.AlertType.ERROR, "No se ha podido eliminar la matrícula.");
            }
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
