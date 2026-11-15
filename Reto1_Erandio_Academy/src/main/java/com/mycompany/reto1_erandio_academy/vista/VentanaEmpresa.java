
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.modelo.Empresa;
import com.mycompany.reto1_erandio_academy.servicio.EmpresaServicio;
import com.mycompany.reto1_erandio_academy.servicio.SesionUsuario;
import com.mycompany.reto1_erandio_academy.servicio.LogServicio;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class VentanaEmpresa {

    private final EmpresaServicio empresaServicio;

    private Empresa empresaActual;

    private final String AZUL_OSCURO = "#12283A";
    private final String AZUL = "#1F6F8B";
    private final String FONDO = "#F2F5F7";
    private final String GRIS = "#64748B";

    public VentanaEmpresa() {
        empresaServicio = new EmpresaServicio();
    }

    // ==========================================
    // REGISTRAR LOG
    // ==========================================

    private void registrarLog(
            String operacion,
            String resultado,
            String error) {

        LogServicio logServicio = new LogServicio();

        try {
            logServicio.registrar(
                    operacion,
                    "empresa",
                    resultado,
                    error);
        } finally {
            logServicio.cerrar();
        }
    }

    // ==========================================
    // MOSTRAR VENTANA
    // ==========================================

    public void mostrar() {

        // SOLO ADMINISTRADORES
        if (!SesionUsuario.tieneRol("ADMIN")) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Acceso denegado",
                    "Solo el administrador puede gestionar la empresa.");

            empresaServicio.cerrar();
            return;
        }

        Stage ventana = new Stage();

        ventana.setTitle(
                "Erandio Academy - Datos de la empresa");

        // ==========================================
        // CAMPOS
        // ==========================================

        TextField nombre = crearCampo(
                "Nombre de la empresa");

        TextField sector = crearCampo(
                "Sector empresarial");

        TextField actividad = crearCampo(
                "Actividad de la empresa");

        TextField direccion = crearCampo(
                "Dirección");

        TextField estado = crearCampo(
                "Estado de la empresa");

        TextArea descripcion = new TextArea();

        descripcion.setPromptText(
                "Describe la actividad y características de la empresa...");

        descripcion.setPrefRowCount(5);
        descripcion.setWrapText(true);

        descripcion.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #DCE3E8;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 8;");

        // ==========================================
        // CABECERA
        // ==========================================

        Label etiqueta = new Label("ADMINISTRACIÓN");

        etiqueta.setStyle(
                "-fx-text-fill: " + AZUL + ";" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;");

        Label titulo = new Label(
                "Datos de la empresa");

        titulo.setStyle(
                "-fx-text-fill: " + AZUL_OSCURO + ";" +
                "-fx-font-size: 27px;" +
                "-fx-font-weight: bold;");

        Label subtitulo = new Label(
                "Consulta y actualiza la información de Erandio Academy.");

        subtitulo.setStyle(
                "-fx-text-fill: " + GRIS + ";" +
                "-fx-font-size: 13px;");

        VBox cabecera = new VBox(
                6,
                etiqueta,
                titulo,
                subtitulo);

        // ==========================================
        // FORMULARIO
        // ==========================================

        GridPane formulario = new GridPane();

        formulario.setPadding(new Insets(22));
        formulario.setHgap(18);
        formulario.setVgap(16);

        ColumnConstraints columnaEtiquetas =
                new ColumnConstraints();

        columnaEtiquetas.setMinWidth(120);

        ColumnConstraints columnaCampos =
                new ColumnConstraints();

        columnaCampos.setHgrow(Priority.ALWAYS);
        columnaCampos.setFillWidth(true);

        formulario.getColumnConstraints().addAll(
                columnaEtiquetas,
                columnaCampos);

        agregarCampo(
                formulario,
                "Nombre:",
                nombre,
                0);

        agregarCampo(
                formulario,
                "Sector:",
                sector,
                1);

        agregarCampo(
                formulario,
                "Actividad:",
                actividad,
                2);

        agregarCampo(
                formulario,
                "Dirección:",
                direccion,
                3);

        agregarCampo(
                formulario,
                "Descripción:",
                descripcion,
                4);

        agregarCampo(
                formulario,
                "Estado:",
                estado,
                5);

        // ==========================================
        // BOTONES
        // ==========================================

        Button guardar = crearBoton(
                "Guardar cambios",
                AZUL);

        Button cerrar = crearBoton(
                "Cerrar",
                AZUL_OSCURO);

        HBox botones = new HBox(
                12,
                guardar,
                cerrar);

        botones.setAlignment(Pos.CENTER_RIGHT);

        // ==========================================
        // TARJETA
        // ==========================================

        Label tituloFormulario = new Label(
                "Información general");

        tituloFormulario.setStyle(
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + AZUL_OSCURO + ";");

        VBox tarjeta = new VBox(
                18,
                tituloFormulario,
                formulario,
                botones);

        tarjeta.setPadding(new Insets(22));

        tarjeta.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #E1E7EC;" +
                "-fx-border-radius: 14;");

        // ==========================================
        // CONTENIDO PRINCIPAL
        // ==========================================

        VBox contenido = new VBox(
                22,
                cabecera,
                tarjeta);

        contenido.setPadding(new Insets(30));

        contenido.setStyle(
                "-fx-background-color: " + FONDO + ";");

        // ==========================================
        // CARGAR DATOS DE MONGODB
        // ==========================================

        try {

            empresaActual =
                    empresaServicio.obtenerEmpresa();

            if (empresaActual == null) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Empresa no encontrada",
                        "No existen datos de empresa en MongoDB.");

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
                    "Error al cargar los datos",
                    ex.getMessage());

            empresaServicio.cerrar();
            return;
        }

        // ==========================================
        // GUARDAR CAMBIOS
        // ==========================================

        guardar.setOnAction(e -> {

            try {

                if (nombre.getText().trim().isEmpty()) {

                    mostrarAlerta(
                            Alert.AlertType.WARNING,
                            "Campo obligatorio",
                            "El nombre de la empresa es obligatorio.");

                    return;
                }

                empresaActual.setNombre(
                        nombre.getText().trim());

                empresaActual.setSector(
                        sector.getText().trim());

                empresaActual.setActividad(
                        actividad.getText().trim());

                empresaActual.setDireccion(
                        direccion.getText().trim());

                empresaActual.setDescripcion(
                        descripcion.getText().trim());

                empresaActual.setEstado(
                        estado.getText().trim());

                empresaServicio.actualizar(empresaActual);

                registrarLog(
                        "UPDATE",
                        "OK",
                        null);

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Guardado correctamente",
                        "Los datos de Erandio Academy se han actualizado.");

            } catch (Exception ex) {

                registrarLog(
                        "UPDATE",
                        "ERROR",
                        ex.getMessage());

                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Error al guardar",
                        ex.getMessage());
            }
        });

        // ==========================================
        // CERRAR
        // ==========================================

        cerrar.setOnAction(e -> ventana.close());

        ventana.setScene(
                new Scene(contenido, 950, 720));

        ventana.setMinWidth(750);
        ventana.setMinHeight(600);

        ventana.setOnHidden(e ->
                empresaServicio.cerrar());

        ventana.show();
    }

    // ==========================================
    // CREAR CAMPOS
    // ==========================================

    private TextField crearCampo(String texto) {

        TextField campo = new TextField();

        campo.setPromptText(texto);
        campo.setPrefHeight(40);

        campo.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #DCE3E8;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10;");

        return campo;
    }

    // ==========================================
    // AGREGAR CAMPO AL FORMULARIO
    // ==========================================

    private void agregarCampo(
            GridPane formulario,
            String etiqueta,
            Control campo,
            int fila) {

        Label label = new Label(etiqueta);

        label.setStyle(
                "-fx-text-fill: " + AZUL_OSCURO + ";" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;");

        GridPane.setHgrow(
                campo,
                Priority.ALWAYS);

        formulario.add(label, 0, fila);
        formulario.add(campo, 1, fila);
    }

    // ==========================================
    // CREAR BOTONES
    // ==========================================

    private Button crearBoton(
            String texto,
            String color) {

        Button boton = new Button(texto);

        boton.setPrefHeight(42);

        boton.setPadding(
                new Insets(0, 20, 0, 20));

        boton.setCursor(Cursor.HAND);

        boton.setStyle(
                "-fx-background-color: " + color + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;");

        return boton;
    }

    // ==========================================
    // MOSTRAR ALERTAS
    // ==========================================

    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alerta = new Alert(tipo);

        alerta.setTitle("Erandio Academy");
        alerta.setHeaderText(titulo);

        alerta.setContentText(
                mensaje == null
                ? "Error desconocido"
                : mensaje);

        alerta.showAndWait();
    }
}
