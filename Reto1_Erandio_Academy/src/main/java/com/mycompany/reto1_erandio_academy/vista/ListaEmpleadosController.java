package com.mycompany.reto1_erandio_academy.vista;
import com.mycompany.reto1_erandio_academy.controlador.Gestor;

import com.mycompany.reto1_erandio_academy.controlador.Navegador;
import com.mycompany.reto1_erandio_academy.modelo.Empleado;

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

public class ListaEmpleadosController implements Initializable {

    private final Gestor servicio = new Gestor();

    @FXML private VBox contenido;
    @FXML private TableView<Empleado> tabla;
    @FXML private TableColumn<Empleado, String> colNombre;
    @FXML private TableColumn<Empleado, String> colPuesto;
    @FXML private TableColumn<Empleado, String> colDepartamento;
    @FXML private TableColumn<Empleado, Boolean> colActivo;
    @FXML private Button nuevoEmpleado;
    @FXML private Button modificar;
    @FXML private Button eliminar;
    @FXML private Button actualizar;

    private void registrarLog(String operacion, String resultado, String error) {
        Gestor logServicio = new Gestor();
        try {
            logServicio.registrarLog(operacion, "empleados", resultado, error);
        } finally {
            logServicio.cerrar();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colNombre.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nombre"));
        colPuesto.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("puesto"));
        colDepartamento.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("departamento"));
        colActivo.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("activo"));

        cargarEmpleados();

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
    private void onNuevoEmpleado() {
        NuevoEmpleadoController controlador = Navegador.abrirYDevolverControlador(
                "/com/mycompany/reto1_erandio_academy/vista/NuevoEmpleado.fxml");
        if (controlador != null) {
            controlador.setAlGuardar(this::cargarEmpleados);
        }
    }

    @FXML
    private void onModificar() {

        Empleado seleccionado = tabla.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAviso("Selecciona un empleado.");
            return;
        }

        TextField nombreCampo = new TextField(seleccionado.getNombre());
        TextField puestoCampo = new TextField(seleccionado.getPuesto());
        TextField departamentoCampo = new TextField(seleccionado.getDepartamento());
        CheckBox activoCampo = new CheckBox("Empleado activo");
        activoCampo.setSelected(seleccionado.isActivo());

        GridPane formulario = new GridPane();
        formulario.setPadding(new Insets(15));
        formulario.setHgap(10);
        formulario.setVgap(10);

        formulario.add(new Label("Nombre:"), 0, 0);
        formulario.add(nombreCampo, 1, 0);
        formulario.add(new Label("Puesto:"), 0, 1);
        formulario.add(puestoCampo, 1, 1);
        formulario.add(new Label("Departamento:"), 0, 2);
        formulario.add(departamentoCampo, 1, 2);
        formulario.add(activoCampo, 1, 3);

        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Modificar empleado");
        dialogo.getDialogPane().setContent(formulario);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> resultado = dialogo.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {

            try {
                Empleado actualizado = new Empleado(
                        seleccionado.getId(),
                        nombreCampo.getText().trim(),
                        puestoCampo.getText().trim(),
                        departamentoCampo.getText().trim(),
                        activoCampo.isSelected()
                );

                if (servicio.modificarEmpleado(actualizado)) {
                    registrarLog("UPDATE", "OK", null);
                    cargarEmpleados();
                    mostrarInformacion("Empleado modificado correctamente.");
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

        Empleado seleccionado = tabla.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAviso("Selecciona un empleado para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar empleado");
        confirmacion.setHeaderText("¿Estás seguro?");
        confirmacion.setContentText("Vas a eliminar al empleado: " + seleccionado.getNombre());

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {

            try {
                boolean eliminado = servicio.eliminarEmpleado(seleccionado.getId());

                if (eliminado) {
                    registrarLog("DELETE", "OK", null);
                    cargarEmpleados();
                    mostrarInformacion("Empleado eliminado correctamente.");
                } else {
                    mostrarAviso("No se ha encontrado el empleado.");
                }

            } catch (Exception ex) {
                registrarLog("DELETE", "ERROR", ex.getMessage());
                mostrarError(ex.getMessage());
            }
        }
    }

    @FXML
    private void onActualizar() {
        cargarEmpleados();
    }

    private void cargarEmpleados() {
        try {
            tabla.setItems(FXCollections.observableArrayList(servicio.listarEmpleados()));
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
