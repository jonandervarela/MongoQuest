
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.modelo.Empresa;
import com.mycompany.reto1_erandio_academy.servicio.EmpresaServicio;
import com.mycompany.reto1_erandio_academy.servicio.SesionUsuario;
import com.mycompany.reto1_erandio_academy.servicio.LogServicio;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VentanaEmpresa {

    private final EmpresaServicio empresaServicio;

    private Empresa empresaActual;

    public VentanaEmpresa() {
        empresaServicio = new EmpresaServicio();
    }

    public void mostrar() {

        // SOLO ADMINISTRADORES
        if (!SesionUsuario.tieneRol("ADMIN")) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Acceso denegado",
                    "Solo el administrador puede gestionar la empresa."
            );
            empresaServicio.cerrar();
            return;
        }

        Stage ventana = new Stage();
        ventana.setTitle("Academy - Datos de la empresa");

        // CAMPOS
        TextField nombre = new TextField();
        TextField sector = new TextField();
        TextField actividad = new TextField();
        TextField direccion = new TextField();
        TextField estado = new TextField();

        TextArea descripcion = new TextArea();
        descripcion.setPrefRowCount(4);
        descripcion.setWrapText(true);

        // FORMULARIO
        GridPane formulario = new GridPane();

        formulario.setPadding(new Insets(15));
        formulario.setHgap(12);
        formulario.setVgap(12);

        formulario.add(new Label("Nombre:"), 0, 0);
        formulario.add(nombre, 1, 0);

        formulario.add(new Label("Sector:"), 0, 1);
        formulario.add(sector, 1, 1);

        formulario.add(new Label("Actividad:"), 0, 2);
        formulario.add(actividad, 1, 2);

        formulario.add(new Label("Dirección:"), 0, 3);
        formulario.add(direccion, 1, 3);

        formulario.add(new Label("Descripción:"), 0, 4);
        formulario.add(descripcion, 1, 4);

        formulario.add(new Label("Estado:"), 0, 5);
        formulario.add(estado, 1, 5);

        // BOTONES
        Button guardar = new Button("Guardar cambios");
        Button cerrar = new Button("Cerrar");

        HBox botones = new HBox(10, guardar, cerrar);

        VBox contenido = new VBox(
                15,
                new Label("CONFIGURACIÓN DE ERANDIO ACADEMY"),
                formulario,
                botones
        );

        contenido.setPadding(new Insets(20));

        // CARGAR DATOS DE MONGODB
        try {

            empresaActual = empresaServicio.obtenerEmpresa();

            if (empresaActual == null) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Empresa no encontrada",
                        "No existen datos de empresa en MongoDB."
                );
                empresaServicio.cerrar();
                return;
            }

            nombre.setText(empresaActual.getNombre());
            sector.setText(empresaActual.getSector());
            actividad.setText(empresaActual.getActividad());
            direccion.setText(empresaActual.getDireccion());
            descripcion.setText(empresaActual.getDescripcion());
            estado.setText(empresaActual.getEstado());

        } catch (Exception ex) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    ex.getMessage()
            );

            empresaServicio.cerrar();
            return;
        }

        // GUARDAR CAMBIOS
        guardar.setOnAction(e -> {

            try {

                if (nombre.getText().trim().isEmpty()) {
                    mostrarAlerta(
                            Alert.AlertType.WARNING,
                            "Campo obligatorio",
                            "El nombre de la empresa es obligatorio."
                    );
                    return;
                }

                empresaActual.setNombre(nombre.getText().trim());
                empresaActual.setSector(sector.getText().trim());
                empresaActual.setActividad(actividad.getText().trim());
                empresaActual.setDireccion(direccion.getText().trim());
                empresaActual.setDescripcion(
                        descripcion.getText().trim()
                );
                empresaActual.setEstado(estado.getText().trim());

                empresaServicio.actualizar(empresaActual);

                // REGISTRAR LOG DE ÉXITO
                LogServicio logServicio = new LogServicio();

                try {
                    logServicio.registrar(
                            "UPDATE",
                            "empresa",
                            "OK",
                            null
                    );
                } finally {
                    logServicio.cerrar();
                }

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Guardado correctamente",
                        "Los datos de Erandio Academy se han actualizado."
                );

            } catch (Exception ex) {

                // REGISTRAR LOG DE ERROR
                LogServicio logServicio = new LogServicio();

                try {
                    logServicio.registrar(
                            "UPDATE",
                            "empresa",
                            "ERROR",
                            ex.getMessage()
                    );
                } finally {
                    logServicio.cerrar();
                }

                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Error al guardar",
                        ex.getMessage()
                );
            }
        });

        // CERRAR VENTANA
        cerrar.setOnAction(e -> ventana.close());

        ventana.setScene(new Scene(contenido, 650, 500));

        ventana.setOnCloseRequest(e -> empresaServicio.cerrar());

        ventana.show();
    }

    // MOSTRAR ALERTAS
    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alerta = new Alert(tipo);

        alerta.setTitle("Academy");
        alerta.setHeaderText(titulo);

        alerta.setContentText(
                mensaje == null ? "Error desconocido" : mensaje
        );

        alerta.showAndWait();
    }
}
