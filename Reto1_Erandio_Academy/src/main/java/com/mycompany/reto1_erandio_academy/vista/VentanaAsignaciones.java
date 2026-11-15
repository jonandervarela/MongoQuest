
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.modelo.Curso;
import com.mycompany.reto1_erandio_academy.modelo.Empleado;
import com.mycompany.reto1_erandio_academy.servicio.AsignacionServicio;
import com.mycompany.reto1_erandio_academy.servicio.CursoServicio;
import com.mycompany.reto1_erandio_academy.servicio.EmpleadoServicio;
import com.mycompany.reto1_erandio_academy.servicio.LogServicio;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VentanaAsignaciones {

    private final AsignacionServicio asignacionServicio =
            new AsignacionServicio();

    private final CursoServicio cursoServicio =
            new CursoServicio();

    private final EmpleadoServicio empleadoServicio =
            new EmpleadoServicio();

    private static final String TINTA = "#12283A";
    private static final String AZUL = "#1F6F8B";
    private static final String FONDO = "#F2F5F7";
    private static final String GRIS = "#64748B";

    // REGISTRAR LOG

    private void registrarLog(
            String operacion,
            String resultado,
            String error) {

        LogServicio logServicio = new LogServicio();

        try {
            logServicio.registrar(
                    operacion,
                    "asignaciones",
                    resultado,
                    error
            );
        } finally {
            logServicio.cerrar();
        }
    }

    // MOSTRAR VENTANA

    public void mostrar() {

        Stage ventana = new Stage();

        ventana.setTitle(
                "Erandio Academy - Asignación de profesores"
        );

        // CABECERA

        Label etiqueta = new Label("PANEL DE ADMINISTRACIÓN");

        etiqueta.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                11
        ));

        etiqueta.setTextFill(Color.web(AZUL));

        Label titulo = new Label(
                "Asignación de profesores"
        );

        titulo.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                27
        ));

        titulo.setTextFill(Color.web(TINTA));

        Label descripcion = new Label(
                "Asigna profesores a los cursos y consulta sus asignaciones."
        );

        descripcion.setFont(Font.font("Segoe UI", 14));
        descripcion.setTextFill(Color.web(GRIS));

        VBox cabecera = new VBox(
                7,
                etiqueta,
                titulo,
                descripcion
        );

        cabecera.setPadding(
                new Insets(28, 30, 20, 30)
        );

        // COMBOBOX CURSOS

        ComboBox<Curso> comboCursos = new ComboBox<>();

        comboCursos.setPromptText("Selecciona un curso");
        comboCursos.setMaxWidth(Double.MAX_VALUE);
        comboCursos.setPrefHeight(42);

        comboCursos.setConverter(
                new StringConverter<Curso>() {

            @Override
            public String toString(Curso curso) {
                return curso == null
                        ? ""
                        : curso.getNombre();
            }

            @Override
            public Curso fromString(String texto) {
                return null;
            }
        });

        // COMBOBOX PROFESORES

        ComboBox<Empleado> comboProfesores =
                new ComboBox<>();

        comboProfesores.setPromptText(
                "Selecciona un profesor"
        );

        comboProfesores.setMaxWidth(Double.MAX_VALUE);
        comboProfesores.setPrefHeight(42);

        comboProfesores.setConverter(
                new StringConverter<Empleado>() {

            @Override
            public String toString(Empleado empleado) {
                return empleado == null
                        ? ""
                        : empleado.getNombre();
            }

            @Override
            public Empleado fromString(String texto) {
                return null;
            }
        });

        // CARGAR CURSOS

        try {

            comboCursos.setItems(
                    FXCollections.observableArrayList(
                            cursoServicio.listar()
                    )
            );

        } catch (Exception ex) {

            mostrarError(ex.getMessage());
        }

        // CARGAR PROFESORES ACTIVOS

        List<Empleado> profesores = new ArrayList<>();

        try {

            for (Empleado empleado : empleadoServicio.listar()) {

                if (empleado.getPuesto() != null
                        && empleado.getPuesto()
                                .equalsIgnoreCase("Profesor")
                        && empleado.isActivo()) {

                    profesores.add(empleado);
                }
            }

            comboProfesores.setItems(
                    FXCollections.observableArrayList(
                            profesores
                    )
            );

        } catch (Exception ex) {

            mostrarError(ex.getMessage());
        }

        // LISTA DE PROFESORES ASIGNADOS

        ListView<Empleado> listaAsignaciones =
                new ListView<>();

        listaAsignaciones.setPlaceholder(
                new Label(
                        "Selecciona un curso para ver sus profesores."
                )
        );

        listaAsignaciones.setPrefHeight(300);

        listaAsignaciones.setCellFactory(param ->
                new ListCell<Empleado>() {

            @Override
            protected void updateItem(
                    Empleado empleado,
                    boolean vacio) {

                super.updateItem(empleado, vacio);

                if (vacio || empleado == null) {

                    setText(null);
                    setStyle("");

                } else {

                    setText("  " + empleado.getNombre());

                    setFont(Font.font(
                            "Segoe UI",
                            FontWeight.SEMI_BOLD,
                            13
                    ));

                    setStyle(
                            "-fx-padding: 12;"
                            + "-fx-text-fill: #12283A;"
                    );
                }
            }
        });

        // BOTONES

        Button asignar = crearBoton(
                "+ Asignar profesor",
                AZUL
        );

        Button quitar = crearBoton(
                "Quitar profesor",
                "#B3382C"
        );

        Button actualizar = crearBoton(
                "Actualizar datos",
                "#64748B"
        );

        // SELECCIONAR CURSO

        comboCursos.setOnAction(e -> {

            Curso curso = comboCursos.getValue();

            if (curso != null) {

                cargarAsignaciones(
                        curso,
                        listaAsignaciones
                );
            }
        });

        // ASIGNAR PROFESOR

        asignar.setOnAction(e -> {

            Curso curso = comboCursos.getValue();

            Empleado profesor =
                    comboProfesores.getValue();

            try {

                if (curso == null || profesor == null) {

                    mostrarAviso(
                            "Selecciona un curso y un profesor."
                    );

                    return;
                }

                asignacionServicio.asignar(
                        profesor.getId(),
                        curso.getId()
                );

                registrarLog(
                        "INSERT",
                        "OK",
                        null
                );

                cargarAsignaciones(
                        curso,
                        listaAsignaciones
                );

                comboProfesores.setValue(null);

                mostrarInformacion(
                        "Profesor asignado correctamente."
                );

            } catch (Exception ex) {

                registrarLog(
                        "INSERT",
                        "ERROR",
                        ex.getMessage()
                );

                mostrarError(ex.getMessage());
            }
        });

        // QUITAR PROFESOR

        quitar.setOnAction(e -> {

            Curso curso = comboCursos.getValue();

            Empleado seleccionado =
                    listaAsignaciones
                            .getSelectionModel()
                            .getSelectedItem();

            if (curso == null || seleccionado == null) {

                mostrarAviso(
                        "Selecciona un curso y un profesor asignado."
                );

                return;
            }

            Alert confirmacion =
                    new Alert(
                            Alert.AlertType.CONFIRMATION
                    );

            confirmacion.setTitle(
                    "Quitar asignación"
            );

            confirmacion.setHeaderText(
                    "Confirmar eliminación"
            );

            confirmacion.setContentText(
                    "¿Quieres quitar a "
                    + seleccionado.getNombre()
                    + " del curso "
                    + curso.getNombre()
                    + "?"
            );

            Optional<ButtonType> resultado =
                    confirmacion.showAndWait();

            if (resultado.isPresent()
                    && resultado.get() == ButtonType.OK) {

                try {

                    ObjectId empleadoId =
                            seleccionado.getId();

                    if (asignacionServicio.quitar(
                            empleadoId,
                            curso.getId())) {

                        registrarLog(
                                "DELETE",
                                "OK",
                                null
                        );

                        cargarAsignaciones(
                                curso,
                                listaAsignaciones
                        );

                        mostrarInformacion(
                                "Asignación eliminada correctamente."
                        );

                    } else {

                        mostrarAviso(
                                "No se ha encontrado la asignación."
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

        // ACTUALIZAR DATOS

        actualizar.setOnAction(e -> {

            Curso cursoSeleccionado =
                    comboCursos.getValue();

            try {

                comboCursos.setItems(
                        FXCollections.observableArrayList(
                                cursoServicio.listar()
                        )
                );

                List<Empleado> profesoresActualizados =
                        new ArrayList<>();

                for (Empleado empleado : empleadoServicio.listar()) {

                    if (empleado.getPuesto() != null
                            && empleado.getPuesto()
                                    .equalsIgnoreCase("Profesor")
                            && empleado.isActivo()) {

                        profesoresActualizados.add(empleado);
                    }
                }

                comboProfesores.setItems(
                        FXCollections.observableArrayList(
                                profesoresActualizados
                        )
                );

                if (cursoSeleccionado != null) {

                    comboCursos.getItems().stream()
                            .filter(c ->
                                    c.getId().equals(
                                            cursoSeleccionado.getId()
                                    )
                            )
                            .findFirst()
                            .ifPresent(comboCursos::setValue);
                }

                if (comboCursos.getValue() != null) {

                    cargarAsignaciones(
                            comboCursos.getValue(),
                            listaAsignaciones
                    );

                } else {

                    listaAsignaciones.getItems().clear();
                }

                mostrarInformacion(
                        "Datos actualizados correctamente."
                );

            } catch (Exception ex) {

                mostrarError(ex.getMessage());
            }
        });

        // PANEL DE NUEVA ASIGNACIÓN

        Label tituloAsignacion = new Label(
                "Nueva asignación"
        );

        tituloAsignacion.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                17
        ));

        tituloAsignacion.setTextFill(
                Color.web(TINTA)
        );

        Label etiquetaCurso = new Label(
                "Seleccionar curso"
        );

        Label etiquetaProfesor = new Label(
                "Seleccionar profesor"
        );

        VBox panelAsignacion = new VBox(
                13,
                tituloAsignacion,
                etiquetaCurso,
                comboCursos,
                etiquetaProfesor,
                comboProfesores,
                asignar
        );

        panelAsignacion.setPadding(
                new Insets(22)
        );

        panelAsignacion.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 10;"
                + "-fx-border-color: #DCE4EA;"
                + "-fx-border-radius: 10;"
        );

        // PANEL DE PROFESORES ASIGNADOS

        Label tituloLista = new Label(
                "Profesores asignados"
        );

        tituloLista.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                17
        ));

        tituloLista.setTextFill(
                Color.web(TINTA)
        );

        Label ayuda = new Label(
                "Selecciona un profesor para quitar su asignación."
        );

        ayuda.setFont(Font.font("Segoe UI", 12));
        ayuda.setTextFill(Color.web(GRIS));
        ayuda.setWrapText(true);

        VBox panelLista = new VBox(
                12,
                tituloLista,
                ayuda,
                listaAsignaciones,
                quitar
        );

        panelLista.setPadding(
                new Insets(22)
        );

        panelLista.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 10;"
                + "-fx-border-color: #DCE4EA;"
                + "-fx-border-radius: 10;"
        );

        VBox.setVgrow(listaAsignaciones, Priority.ALWAYS);

        // DISTRIBUCIÓN

        HBox paneles = new HBox(
                18,
                panelAsignacion,
                panelLista
        );

        paneles.setAlignment(Pos.TOP_CENTER);

        HBox.setHgrow(panelAsignacion, Priority.ALWAYS);
        HBox.setHgrow(panelLista, Priority.ALWAYS);

        panelAsignacion.setMaxWidth(Double.MAX_VALUE);
        panelLista.setMaxWidth(Double.MAX_VALUE);

        // BARRA INFERIOR

        HBox acciones = new HBox(
                actualizar
        );

        acciones.setAlignment(Pos.CENTER_RIGHT);

        // CONTENIDO PRINCIPAL

        VBox contenido = new VBox(
                18,
                paneles,
                acciones
        );

        contenido.setPadding(
                new Insets(0, 30, 30, 30)
        );

        VBox raiz = new VBox(
                cabecera,
                contenido
        );

        raiz.setStyle(
                "-fx-background-color: " + FONDO + ";"
        );

        // CONFIGURAR VENTANA

        Scene escena = new Scene(
                raiz,
                1050,
                680
        );

        ventana.setScene(escena);

        ventana.setMinWidth(850);
        ventana.setMinHeight(600);

        // CERRAR CONEXIONES

        ventana.setOnHidden(e -> {

            asignacionServicio.cerrar();
            cursoServicio.cerrar();
            empleadoServicio.cerrar();
        });

        ventana.centerOnScreen();
        ventana.show();
    }

    // CARGAR PROFESORES ASIGNADOS

    private void cargarAsignaciones(
            Curso curso,
            ListView<Empleado> lista) {

        List<Empleado> profesoresAsignados =
                new ArrayList<>();

        List<Document> asignaciones =
                asignacionServicio.listarPorCurso(
                        curso.getId()
                );

        for (Document documento : asignaciones) {

            ObjectId empleadoId =
                    documento.getObjectId("empleadoId");

            Empleado empleado =
                    empleadoServicio.buscarPorId(
                            empleadoId
                    );

            if (empleado != null) {
                profesoresAsignados.add(empleado);
            }
        }

        lista.setItems(
                FXCollections.observableArrayList(
                        profesoresAsignados
                )
        );
    }

    // CREAR BOTONES

    private Button crearBoton(
            String texto,
            String color) {

        Button boton = new Button(texto);

        boton.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                12
        ));

        boton.setTextFill(Color.WHITE);
        boton.setCursor(Cursor.HAND);

        boton.setPadding(
                new Insets(11, 18, 11, 18)
        );

        String normal =
                "-fx-background-color: " + color + ";"
                + "-fx-background-radius: 7;";

        String encima =
                "-fx-background-color: derive("
                + color
                + ", -15%);"
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

    // MENSAJES

    private void mostrarAviso(String mensaje) {

        mostrarMensaje(
                Alert.AlertType.WARNING,
                "Atención",
                mensaje
        );
    }

    private void mostrarInformacion(String mensaje) {

        mostrarMensaje(
                Alert.AlertType.INFORMATION,
                "Operación correcta",
                mensaje
        );
    }

    private void mostrarError(String mensaje) {

        mostrarMensaje(
                Alert.AlertType.ERROR,
                "Ha ocurrido un error",
                mensaje
        );
    }

    private void mostrarMensaje(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alerta = new Alert(tipo);

        alerta.setTitle("Erandio Academy");
        alerta.setHeaderText(titulo);

        alerta.setContentText(
                mensaje == null
                        ? "Ha ocurrido un error."
                        : mensaje
        );

        alerta.showAndWait();
    }
}
