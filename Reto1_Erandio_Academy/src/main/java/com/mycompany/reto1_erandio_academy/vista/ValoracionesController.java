package com.mycompany.reto1_erandio_academy.vista;
import com.mycompany.reto1_erandio_academy.controlador.Gestor;

import com.mycompany.reto1_erandio_academy.controlador.DAOImpl;

import com.mycompany.reto1_erandio_academy.controlador.SesionUsuario;
import com.mycompany.reto1_erandio_academy.modelo.Curso;
import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.modelo.Valoracion;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;

public class ValoracionesController implements Initializable {

    private DAOImpl cursoDAO;
    private DAOImpl usuarioDAO;
    private Gestor valoracionServicio;

    private Usuario usuarioActual;
    private boolean esAdministrador;

    private final Map<String, String> nombresUsuarios = new HashMap<>();
    private final Map<String, String> nombresCursos = new HashMap<>();

    @FXML private VBox contenido;
    @FXML private Label titulo;
    @FXML private VBox bloqueAdminUsuario;
    @FXML private ComboBox<Usuario> comboUsuarios;
    @FXML private Label etiquetaUsuarioPropio;
    @FXML private ComboBox<Curso> comboCursos;
    @FXML private ComboBox<Integer> comboPuntuacion;
    @FXML private TextArea campoComentario;
    @FXML private Button botonGuardar;
    @FXML private TableView<Valoracion> tabla;
    @FXML private TableColumn<Valoracion, String> usuarioColumna;
    @FXML private TableColumn<Valoracion, String> cursoColumna;
    @FXML private TableColumn<Valoracion, String> puntuacionColumna;
    @FXML private TableColumn<Valoracion, String> comentarioColumna;
    @FXML private Button botonActualizar;

    private void registrarLog(String operacion, String resultado, String error) {
        Gestor logServicio = new Gestor();
        try {
            logServicio.registrarLog(operacion, "valoraciones", resultado, error);
        } finally {
            logServicio.cerrar();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        usuarioActual = SesionUsuario.getUsuarioActual();

        if (usuarioActual == null) {
            mostrarMensaje(Alert.AlertType.WARNING, "No hay ningún usuario conectado.");
            botonGuardar.setDisable(true);
            botonActualizar.setDisable(true);
            return;
        }

        esAdministrador = SesionUsuario.tieneRol("ADMIN");

        cursoDAO = new DAOImpl();
        usuarioDAO = new DAOImpl();
        valoracionServicio = new Gestor();

        if (esAdministrador) {

            bloqueAdminUsuario.setVisible(true);
            bloqueAdminUsuario.setManaged(true);

            comboUsuarios.setItems(FXCollections.observableArrayList(usuarioDAO.listarUsuarios()));
            comboUsuarios.setConverter(new javafx.util.StringConverter<Usuario>() {
                @Override
                public String toString(Usuario usuario) {
                    return usuario == null ? "" : usuario.getNombre();
                }

                @Override
                public Usuario fromString(String texto) {
                    return null;
                }
            });

        } else {
            etiquetaUsuarioPropio.setVisible(true);
            etiquetaUsuarioPropio.setManaged(true);
            etiquetaUsuarioPropio.setText("Usuario: " + usuarioActual.getNombre());
        }

        comboCursos.setItems(FXCollections.observableArrayList(cursoDAO.listarCursos()));
        comboCursos.setConverter(new javafx.util.StringConverter<Curso>() {
            @Override
            public String toString(Curso curso) {
                return curso == null ? "" : curso.getNombre();
            }

            @Override
            public Curso fromString(String texto) {
                return null;
            }
        });

        comboPuntuacion.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));

        crearColumnas();
        cargarNombres();
        cargarValoraciones();

        contenido.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((o2, oldW, newW) -> {
                    if (newW != null) {
                        newW.addEventHandler(WindowEvent.WINDOW_HIDDEN, e -> {
                            valoracionServicio.cerrar();
                            usuarioDAO.cerrar();
                            cursoDAO.cerrar();
                        });
                    }
                });
            }
        });
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

        puntuacionColumna.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(d.getValue().getPuntuacion())));

        comentarioColumna.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getComentario()));
    }

    @FXML
    private void onGuardar() {

        Usuario usuario = esAdministrador ? comboUsuarios.getValue() : usuarioActual;
        Curso curso = comboCursos.getValue();
        Integer puntuacion = comboPuntuacion.getValue();
        String comentario = campoComentario.getText().trim();

        if (usuario == null || curso == null || puntuacion == null) {
            mostrarMensaje(Alert.AlertType.WARNING, "Debes seleccionar usuario, curso y puntuación.");
            return;
        }

        try {
            Valoracion valoracion = new Valoracion(usuario.getId(), curso.getId(), puntuacion, comentario);

            boolean resultado = valoracionServicio.registrarValoracion(valoracion);

            if (resultado) {
                registrarLog("INSERT", "OK", null);
                mostrarMensaje(Alert.AlertType.INFORMATION, "Valoración guardada correctamente.");
                campoComentario.clear();
                comboPuntuacion.setValue(null);
                cargarValoraciones();
            } else {
                registrarLog("INSERT", "ERROR", "No se ha podido guardar la valoración.");
                mostrarMensaje(Alert.AlertType.ERROR, "No se ha podido guardar la valoración.");
            }

        } catch (Exception ex) {
            registrarLog("INSERT", "ERROR", ex.getMessage());
            mostrarMensaje(Alert.AlertType.ERROR, ex.getMessage());
        }
    }

    @FXML
    private void onActualizar() {
        cargarNombres();
        cargarValoraciones();
        tabla.refresh();
    }

    private void cargarValoraciones() {

        if (esAdministrador) {
            tabla.setItems(FXCollections.observableArrayList(valoracionServicio.listarValoraciones()));
        } else {
            tabla.setItems(FXCollections.observableArrayList(valoracionServicio.listarValoracionesPorUsuario(usuarioActual.getId())));
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
