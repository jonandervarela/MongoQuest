
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.modelo.Empleado;
import com.mycompany.reto1_erandio_academy.servicio.EmpleadoServicio;
import com.mycompany.reto1_erandio_academy.servicio.LogServicio;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public class VentanaListaEmpleados {

    private final EmpleadoServicio servicio = new EmpleadoServicio();

    // Registrar operaciones en MongoDB
    private void registrarLog(String operacion, String resultado, String error) {

        LogServicio logServicio = new LogServicio();

        try {
            logServicio.registrar(
                    operacion,
                    "empleados",
                    resultado,
                    error
            );
        } finally {
            logServicio.cerrar();
        }
    }

    public void mostrar() {

        Stage ventana = new Stage();
        ventana.setTitle("Academy - Gestión de empleados");

        TableView<Empleado> tabla = new TableView<>();

        TableColumn<Empleado, String> nombre =
                new TableColumn<>("Nombre");

        nombre.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("nombre"));

        TableColumn<Empleado, String> puesto =
                new TableColumn<>("Puesto");

        puesto.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("puesto"));

        TableColumn<Empleado, String> departamento =
                new TableColumn<>("Departamento");

        departamento.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("departamento"));

        TableColumn<Empleado, Boolean> activo =
                new TableColumn<>("Activo");

        activo.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("activo"));

        tabla.getColumns().addAll(
                nombre, puesto, departamento, activo);

        Button nuevoEmpleado = new Button("Registrar empleado");
        Button modificar = new Button("Modificar empleado");
        Button eliminar = new Button("Eliminar empleado");
        Button actualizar = new Button("Actualizar lista");

        // REGISTRAR EMPLEADO
        nuevoEmpleado.setOnAction(e -> {
            new VentanaNuevoEmpleado(servicio, tabla).mostrar();
        });

        // MODIFICAR EMPLEADO
        modificar.setOnAction(e -> {

            Empleado seleccionado =
                    tabla.getSelectionModel().getSelectedItem();

            if (seleccionado == null) {
                mostrarAviso("Selecciona un empleado.");
                return;
            }

            TextField nombreCampo =
                    new TextField(seleccionado.getNombre());

            TextField puestoCampo =
                    new TextField(seleccionado.getPuesto());

            TextField departamentoCampo =
                    new TextField(seleccionado.getDepartamento());

            CheckBox activoCampo =
                    new CheckBox("Empleado activo");

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

            dialogo.getDialogPane().getButtonTypes().addAll(
                    ButtonType.OK, ButtonType.CANCEL);

            Optional<ButtonType> resultado =
                    dialogo.showAndWait();

            if (resultado.isPresent()
                    && resultado.get() == ButtonType.OK) {

                try {

                    Empleado actualizado = new Empleado(
                            seleccionado.getId(),
                            nombreCampo.getText().trim(),
                            puestoCampo.getText().trim(),
                            departamentoCampo.getText().trim(),
                            activoCampo.isSelected()
                    );

                    if (servicio.modificar(actualizado)) {

                        registrarLog("UPDATE", "OK", null);

                        cargarEmpleados(tabla);

                        mostrarInformacion(
                                "Empleado modificado correctamente.");

                    } else {

                        mostrarAviso(
                                "No se encontraron cambios para guardar.");
                    }

                } catch (Exception ex) {

                    registrarLog("UPDATE", "ERROR", ex.getMessage());

                    mostrarError(ex.getMessage());
                }
            }
        });

        // ELIMINAR EMPLEADO
        eliminar.setOnAction(e -> {

            Empleado seleccionado =
                    tabla.getSelectionModel().getSelectedItem();

            if (seleccionado == null) {
                mostrarAviso("Selecciona un empleado para eliminar.");
                return;
            }

            Alert confirmacion =
                    new Alert(Alert.AlertType.CONFIRMATION);

            confirmacion.setTitle("Eliminar empleado");
            confirmacion.setHeaderText("¿Estás seguro?");

            confirmacion.setContentText(
                    "Vas a eliminar al empleado: "
                    + seleccionado.getNombre());

            Optional<ButtonType> resultado =
                    confirmacion.showAndWait();

            if (resultado.isPresent()
                    && resultado.get() == ButtonType.OK) {

                try {

                    boolean eliminado =
                            servicio.eliminar(seleccionado.getId());

                    if (eliminado) {

                        registrarLog("DELETE", "OK", null);

                        cargarEmpleados(tabla);

                        mostrarInformacion(
                                "Empleado eliminado correctamente.");

                    } else {

                        mostrarAviso(
                                "No se ha encontrado el empleado.");
                    }

                } catch (Exception ex) {

                    registrarLog("DELETE", "ERROR", ex.getMessage());

                    mostrarError(ex.getMessage());
                }
            }
        });

        // ACTUALIZAR TABLA
        actualizar.setOnAction(e -> cargarEmpleados(tabla));

        cargarEmpleados(tabla);

        VBox contenido = new VBox(
                12,
                tabla,
                nuevoEmpleado,
                modificar,
                eliminar,
                actualizar
        );

        contenido.setPadding(new Insets(15));

        ventana.setScene(new Scene(contenido, 750, 500));

        ventana.setOnHidden(e -> servicio.cerrar());

        ventana.show();
    }

    // CARGAR EMPLEADOS
    private void cargarEmpleados(TableView<Empleado> tabla) {

        try {

            tabla.setItems(
                    FXCollections.observableArrayList(
                            servicio.listar()));

        } catch (Exception ex) {

            mostrarError(ex.getMessage());
        }
    }

    // MENSAJE DE AVISO
    private void mostrarAviso(String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Academy");
        alerta.setHeaderText("Atención");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // MENSAJE DE INFORMACIÓN
    private void mostrarInformacion(String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Academy");
        alerta.setHeaderText("Operación correcta");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // MENSAJE DE ERROR
    private void mostrarError(String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Academy");
        alerta.setHeaderText("Ha ocurrido un error");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
