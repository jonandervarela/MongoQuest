
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.modelo.Empleado;
import com.mycompany.reto1_erandio_academy.servicio.EmpleadoServicio;
import com.mycompany.reto1_erandio_academy.servicio.LogServicio;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Optional;

public class VentanaListaEmpleados {

    private final EmpleadoServicio servicio = new EmpleadoServicio();

    private static final String TINTA = "#12283A";
    private static final String AZUL = "#1F6F8B";
    private static final String FONDO = "#F2F5F7";
    private static final String GRIS = "#64748B";

    private FilteredList<Empleado> empleadosFiltrados;

    // REGISTRAR OPERACIONES EN MONGODB
    private void registrarLog(
            String operacion,
            String resultado,
            String error) {

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
        ventana.setTitle("Erandio Academy - Gestión de empleados");

        // CABECERA
        Label titulo = new Label("Gestión de empleados");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 27));
        titulo.setTextFill(Color.web(TINTA));

        Label descripcion = new Label(
                "Administra los profesores y empleados de la academia."
        );
        descripcion.setFont(Font.font("Segoe UI", 14));
        descripcion.setTextFill(Color.web(GRIS));

        VBox cabecera = new VBox(7, titulo, descripcion);
        cabecera.setPadding(new Insets(28, 30, 20, 30));

        // BUSCADOR
        TextField buscador = new TextField();
        buscador.setPromptText(
                "Buscar por nombre, puesto o departamento..."
        );
        buscador.setPrefHeight(40);
        buscador.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 8;"
                + "-fx-border-color: #DCE4EA;"
                + "-fx-border-radius: 8;"
                + "-fx-font-size: 13px;"
        );

        // TABLA
        TableView<Empleado> tabla = new TableView<>();

        tabla.setPlaceholder(
                new Label("No hay empleados para mostrar.")
        );

        tabla.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        tabla.setStyle(
                "-fx-background-color: white;"
                + "-fx-border-color: #DCE4EA;"
                + "-fx-border-radius: 8;"
        );

        TableColumn<Empleado, String> nombre =
                new TableColumn<>("Nombre");

        nombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );
        nombre.setMinWidth(150);

        TableColumn<Empleado, String> puesto =
                new TableColumn<>("Puesto");

        puesto.setCellValueFactory(
                new PropertyValueFactory<>("puesto")
        );
        puesto.setMinWidth(130);

        TableColumn<Empleado, String> departamento =
                new TableColumn<>("Departamento");

        departamento.setCellValueFactory(
                new PropertyValueFactory<>("departamento")
        );
        departamento.setMinWidth(150);

        TableColumn<Empleado, Boolean> activo =
                new TableColumn<>("Estado");

        activo.setCellValueFactory(
                new PropertyValueFactory<>("activo")
        );
        activo.setMinWidth(100);

        activo.setCellFactory(columna ->
                new TableCell<Empleado, Boolean>() {

            @Override
            protected void updateItem(Boolean valor, boolean vacio) {
                super.updateItem(valor, vacio);

                if (vacio || valor == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(valor ? "Activo" : "Inactivo");

                    setStyle(valor
                            ? "-fx-text-fill: #16834A; -fx-font-weight: bold;"
                            : "-fx-text-fill: #B3382C; -fx-font-weight: bold;"
                    );
                }
            }
        });

        tabla.getColumns().addAll(
                nombre,
                puesto,
                departamento,
                activo
        );

        // BOTONES
        Button nuevoEmpleado = crearBoton(
                "+ Registrar empleado", AZUL
        );

        Button modificar = crearBoton(
                "Modificar", "#3976A8"
        );

        Button eliminar = crearBoton(
                "Eliminar", "#B3382C"
        );

        Button actualizar = crearBoton(
                "Actualizar lista", "#64748B"
        );

        // REGISTRAR EMPLEADO
        nuevoEmpleado.setOnAction(e -> {
            new VentanaNuevoEmpleado(servicio, tabla).mostrar();
            cargarEmpleados(tabla, buscador);
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

            formulario.setPadding(new Insets(20));
            formulario.setHgap(15);
            formulario.setVgap(15);

            formulario.add(new Label("Nombre:"), 0, 0);
            formulario.add(nombreCampo, 1, 0);

            formulario.add(new Label("Puesto:"), 0, 1);
            formulario.add(puestoCampo, 1, 1);

            formulario.add(new Label("Departamento:"), 0, 2);
            formulario.add(departamentoCampo, 1, 2);

            formulario.add(activoCampo, 1, 3);

            nombreCampo.setPrefWidth(300);
            puestoCampo.setPrefWidth(300);
            departamentoCampo.setPrefWidth(300);

            Dialog<ButtonType> dialogo = new Dialog<>();

            dialogo.setTitle("Modificar empleado");
            dialogo.setHeaderText("Actualizar información del empleado");
            dialogo.getDialogPane().setContent(formulario);

            dialogo.getDialogPane().getButtonTypes().addAll(
                    ButtonType.OK,
                    ButtonType.CANCEL
            );

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

                        cargarEmpleados(tabla, buscador);

                        mostrarInformacion(
                                "Empleado modificado correctamente."
                        );

                    } else {

                        mostrarAviso(
                                "No se encontraron cambios para guardar."
                        );
                    }

                } catch (Exception ex) {

                    registrarLog(
                            "UPDATE",
                            "ERROR",
                            ex.getMessage()
                    );

                    mostrarError(ex.getMessage());
                }
            }
        });

        // ELIMINAR EMPLEADO
        eliminar.setOnAction(e -> {

            Empleado seleccionado =
                    tabla.getSelectionModel().getSelectedItem();

            if (seleccionado == null) {
                mostrarAviso(
                        "Selecciona un empleado para eliminar."
                );
                return;
            }

            Alert confirmacion =
                    new Alert(Alert.AlertType.CONFIRMATION);

            confirmacion.setTitle("Eliminar empleado");
            confirmacion.setHeaderText("Confirmar eliminación");

            confirmacion.setContentText(
                    "¿Estás seguro de que quieres eliminar al empleado "
                    + seleccionado.getNombre() + "?"
            );

            Optional<ButtonType> resultado =
                    confirmacion.showAndWait();

            if (resultado.isPresent()
                    && resultado.get() == ButtonType.OK) {

                try {

                    boolean eliminado =
                            servicio.eliminar(seleccionado.getId());

                    if (eliminado) {

                        registrarLog("DELETE", "OK", null);

                        cargarEmpleados(tabla, buscador);

                        mostrarInformacion(
                                "Empleado eliminado correctamente."
                        );

                    } else {

                        mostrarAviso(
                                "No se ha encontrado el empleado."
                        );
                    }

                } catch (Exception ex) {

                    registrarLog(
                            "DELETE",
                            "ERROR",
                            ex.getMessage()
                    );

                    mostrarError(ex.getMessage());
                }
            }
        });

        // ACTUALIZAR TABLA
        actualizar.setOnAction(e ->
                cargarEmpleados(tabla, buscador)
        );

        // CARGAR DATOS
        cargarEmpleados(tabla, buscador);

        // BÚSQUEDA EN TIEMPO REAL
        buscador.textProperty().addListener(
                (observable, anterior, nuevo) -> {

                    if (empleadosFiltrados == null) {
                        return;
                    }

                    String filtro = nuevo == null
                            ? ""
                            : nuevo.trim().toLowerCase();

                    empleadosFiltrados.setPredicate(empleado -> {

                        if (filtro.isEmpty()) {
                            return true;
                        }

                        return contiene(empleado.getNombre(), filtro)
                                || contiene(empleado.getPuesto(), filtro)
                                || contiene(
                                        empleado.getDepartamento(),
                                        filtro
                                );
                    });
                }
        );

        // BARRA DE BOTONES
        HBox acciones = new HBox(
                10,
                nuevoEmpleado,
                modificar,
                eliminar,
                actualizar
        );

        acciones.setAlignment(Pos.CENTER_LEFT);

        // CONTENIDO CENTRAL
        VBox contenido = new VBox(
                16,
                buscador,
                tabla,
                acciones
        );

        contenido.setPadding(
                new Insets(15, 30, 30, 30)
        );

        VBox.setVgrow(tabla, Priority.ALWAYS);

        // PANEL PRINCIPAL
        VBox principal = new VBox(
                cabecera,
                contenido
        );

        principal.setStyle(
                "-fx-background-color: " + FONDO + ";"
        );

        Scene escena = new Scene(principal, 1050, 680);

        ventana.setMinWidth(800);
        ventana.setMinHeight(550);
        ventana.setScene(escena);

        ventana.setOnHidden(e -> servicio.cerrar());

        ventana.centerOnScreen();
        ventana.show();
    }

    // CARGAR EMPLEADOS
    private void cargarEmpleados(
            TableView<Empleado> tabla,
            TextField buscador) {

        try {

            empleadosFiltrados = new FilteredList<>(
                    FXCollections.observableArrayList(
                            servicio.listar()
                    ),
                    empleado -> true
            );

            tabla.setItems(empleadosFiltrados);

            // Mantener el filtro que ya esté escrito
            if (buscador != null
                    && !buscador.getText().isBlank()) {

                String filtro = buscador.getText()
                        .trim()
                        .toLowerCase();

                empleadosFiltrados.setPredicate(empleado ->
                        contiene(empleado.getNombre(), filtro)
                        || contiene(empleado.getPuesto(), filtro)
                        || contiene(empleado.getDepartamento(), filtro)
                );
            }

        } catch (Exception ex) {

            mostrarError(ex.getMessage());
        }
    }

    // COMPROBAR TEXTO
    private boolean contiene(String texto, String filtro) {

        return texto != null
                && texto.toLowerCase().contains(filtro);
    }

    // ESTILO DE BOTONES
    private Button crearBoton(String texto, String color) {

        Button boton = new Button(texto);

        boton.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                12
        ));

        boton.setTextFill(Color.WHITE);
        boton.setCursor(javafx.scene.Cursor.HAND);
        boton.setPadding(new Insets(11, 17, 11, 17));

        String normal =
                "-fx-background-color: " + color + ";"
                + "-fx-background-radius: 7;";

        String encima =
                "-fx-background-color: derive(" + color + ", -15%);"
                + "-fx-background-radius: 7;";

        boton.setStyle(normal);

        boton.setOnMouseEntered(e ->
                boton.setStyle(encima)
        );

        boton.setOnMouseExited(e ->
                boton.setStyle(normal)
        );

        return boton;
    }

    // MENSAJE DE AVISO
    private void mostrarAviso(String mensaje) {

        mostrarMensaje(
                Alert.AlertType.WARNING,
                "Atención",
                mensaje
        );
    }

    // MENSAJE DE INFORMACIÓN
    private void mostrarInformacion(String mensaje) {

        mostrarMensaje(
                Alert.AlertType.INFORMATION,
                "Operación correcta",
                mensaje
        );
    }

    // MENSAJE DE ERROR
    private void mostrarError(String mensaje) {

        mostrarMensaje(
                Alert.AlertType.ERROR,
                "Ha ocurrido un error",
                mensaje
        );
    }

    // MOSTRAR MENSAJE
    private void mostrarMensaje(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alerta = new Alert(tipo);
        alerta.setTitle("Erandio Academy");
        alerta.setHeaderText(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
