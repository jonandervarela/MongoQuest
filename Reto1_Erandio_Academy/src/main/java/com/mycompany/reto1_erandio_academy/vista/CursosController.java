package com.mycompany.reto1_erandio_academy.vista;
import com.mycompany.reto1_erandio_academy.controlador.Gestor;

import com.mycompany.reto1_erandio_academy.controlador.SesionUsuario;
import com.mycompany.reto1_erandio_academy.modelo.Curso;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;

public class CursosController implements Initializable {

    private final Gestor servicio = new Gestor();

    @FXML private VBox contenido;
    @FXML private TableView<Curso> tabla;
    @FXML private TableColumn<Curso, String> colNombre;
    @FXML private TableColumn<Curso, String> colDescripcion;
    @FXML private TableColumn<Curso, String> colDuracion;
    @FXML private TableColumn<Curso, Boolean> colActivo;
    @FXML private Button nuevo;
    @FXML private Button modificar;
    @FXML private Button eliminar;
    @FXML private Button actualizar;

    private void registrarLog(String operacion, String resultado, String error) {

        System.out.println("===== SESION EN CURSOS =====");
        System.out.println("Hay sesión: " + SesionUsuario.haySesion());

        if (SesionUsuario.getUsuarioActual() != null) {
            System.out.println("Usuario: " + SesionUsuario.getUsuarioActual().getNombre());
            System.out.println("Email: " + SesionUsuario.getUsuarioActual().getEmail());
        } else {
            System.out.println("USUARIO ACTUAL ES NULL");
        }

        System.out.println("============================");

        Gestor logServicio = new Gestor();
        try {
            logServicio.registrarLog(operacion, "cursos", resultado, error);
        } finally {
            logServicio.cerrar();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colNombre.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("descripcion"));
        colDuracion.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("duracion"));
        colActivo.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("activo"));

        cargarCursos();

        contenido.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((o2, oldW, newW) -> {
                    if (newW != null) {
                        newW.addEventHandler(WindowEvent.WINDOW_HIDDEN, e -> servicio.cerrar());
                    }
                });
            }
        });
    }

    @FXML
    private void onNuevo() {

        TextField nombreCampo = new TextField();
        TextField descripcionCampo = new TextField();
        TextField duracionCampo = new TextField();

        CheckBox activoCampo = new CheckBox("Curso activo");
        activoCampo.setSelected(true);

        GridPane formulario = crearFormulario(nombreCampo, descripcionCampo, duracionCampo, activoCampo);
        Dialog<ButtonType> dialogo = crearDialogo("Registrar curso", formulario);

        Optional<ButtonType> resultado = dialogo.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {

            try {
                Curso curso = new Curso(
                        null,
                        nombreCampo.getText().trim(),
                        descripcionCampo.getText().trim(),
                        duracionCampo.getText().trim(),
                        activoCampo.isSelected()
                );

                servicio.registrarCurso(curso);
                registrarLog("INSERT", "OK", null);
                cargarCursos();
                mostrarInformacion("Curso registrado correctamente.");

            } catch (Exception ex) {
                registrarLog("INSERT", "ERROR", ex.getMessage());
                mostrarError(ex.getMessage());
            }
        }
    }

    @FXML
    private void onModificar() {

        Curso seleccionado = tabla.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAviso("Selecciona un curso.");
            return;
        }

        TextField nombreCampo = new TextField(seleccionado.getNombre());
        TextField descripcionCampo = new TextField(seleccionado.getDescripcion());
        TextField duracionCampo = new TextField(seleccionado.getDuracion());

        CheckBox activoCampo = new CheckBox("Curso activo");
        activoCampo.setSelected(seleccionado.isActivo());

        GridPane formulario = crearFormulario(nombreCampo, descripcionCampo, duracionCampo, activoCampo);
        Dialog<ButtonType> dialogo = crearDialogo("Modificar curso", formulario);

        Optional<ButtonType> resultado = dialogo.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {

            try {
                Curso actualizado = new Curso(
                        seleccionado.getId(),
                        nombreCampo.getText().trim(),
                        descripcionCampo.getText().trim(),
                        duracionCampo.getText().trim(),
                        activoCampo.isSelected()
                );

                if (servicio.modificarCurso(actualizado)) {
                    registrarLog("UPDATE", "OK", null);
                    cargarCursos();
                    mostrarInformacion("Curso modificado correctamente.");
                } else {
                    mostrarAviso("No se encontraron cambios para guardar.");
                }

            } catch (Exception ex) {
                registrarLog("UPDATE", "ERROR", ex.getMessage());
                mostrarError(ex.getMessage());
            }
        }
    }

    @FXML
    private void onEliminar() {

        Curso seleccionado = tabla.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAviso("Selecciona un curso.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar curso");
        confirmacion.setHeaderText("¿Estás seguro?");
        confirmacion.setContentText("Vas a eliminar el curso: " + seleccionado.getNombre());

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {

            try {
                if (servicio.eliminarCurso(seleccionado.getId())) {
                    registrarLog("DELETE", "OK", null);
                    cargarCursos();
                    mostrarInformacion("Curso eliminado correctamente.");
                } else {
                    mostrarAviso("No se ha encontrado el curso.");
                }

            } catch (Exception ex) {
                registrarLog("DELETE", "ERROR", ex.getMessage());
                mostrarError(ex.getMessage());
            }
        }
    }

    @FXML
    private void onActualizar() {
        cargarCursos();
    }

    private GridPane crearFormulario(TextField nombre, TextField descripcion, TextField duracion, CheckBox activo) {

        GridPane formulario = new GridPane();
        formulario.setPadding(new Insets(15));
        formulario.setHgap(10);
        formulario.setVgap(10);

        formulario.add(new Label("Nombre:"), 0, 0);
        formulario.add(nombre, 1, 0);
        formulario.add(new Label("Descripción:"), 0, 1);
        formulario.add(descripcion, 1, 1);
        formulario.add(new Label("Duración:"), 0, 2);
        formulario.add(duracion, 1, 2);
        formulario.add(activo, 1, 3);

        return formulario;
    }

    private Dialog<ButtonType> crearDialogo(String titulo, GridPane formulario) {

        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle(titulo);
        dialogo.getDialogPane().setContent(formulario);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        return dialogo;
    }

    private void cargarCursos() {
        try {
            tabla.setItems(FXCollections.observableArrayList(servicio.listarCursos()));
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void mostrarAviso(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Academy");
        alerta.setHeaderText("Atención");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarInformacion(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Academy");
        alerta.setHeaderText("Operación correcta");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Academy");
        alerta.setHeaderText("Ha ocurrido un error");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
