
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.modelo.Curso;
import com.mycompany.reto1_erandio_academy.servicio.CursoServicio;
import com.mycompany.reto1_erandio_academy.servicio.LogServicio;
import com.mycompany.reto1_erandio_academy.servicio.SesionUsuario;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Optional;

public class VentanaCursos {

    private final CursoServicio servicio = new CursoServicio();

    private FilteredList<Curso> cursosFiltrados;

    private static final String TINTA = "#12283A";
    private static final String AZUL = "#1F6F8B";
    private static final String FONDO = "#F2F5F7";
    private static final String GRIS = "#64748B";

    // REGISTRAR LOG EN MONGODB

    private void registrarLog(
            String operacion,
            String resultado,
            String error) {

        LogServicio logServicio = new LogServicio();

        try {
            logServicio.registrar(
                    operacion,
                    "cursos",
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

        ventana.setTitle("Erandio Academy - Gestión de cursos");

        // CABECERA

        Label etiqueta = new Label("PANEL DE ADMINISTRACIÓN");
        etiqueta.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                11
        ));
        etiqueta.setTextFill(Color.web(AZUL));

        Label titulo = new Label("Gestión de cursos");
        titulo.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                27
        ));
        titulo.setTextFill(Color.web(TINTA));

        Label descripcion = new Label(
                "Administra los cursos y la formación académica."
        );
        descripcion.setFont(Font.font("Segoe UI", 14));
        descripcion.setTextFill(Color.web(GRIS));

        VBox cabecera = new VBox(
                7,
                etiqueta,
                titulo,
                descripcion
        );

        cabecera.setPadding(new Insets(28, 30, 20, 30));

        // BUSCADOR

        TextField buscador = new TextField();

        buscador.setPromptText(
                "Buscar por nombre, descripción o duración..."
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

        TableView<Curso> tabla = new TableView<>();

        tabla.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        tabla.setPlaceholder(
                new Label("No hay cursos para mostrar.")
        );

        tabla.setStyle(
                "-fx-background-color: white;"
                + "-fx-border-color: #DCE4EA;"
                + "-fx-border-radius: 8;"
        );

        TableColumn<Curso, String> nombre =
                new TableColumn<>("Nombre del curso");

        nombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );

        nombre.setMinWidth(180);

        TableColumn<Curso, String> descripcionCol =
                new TableColumn<>("Descripción");

        descripcionCol.setCellValueFactory(
                new PropertyValueFactory<>("descripcion")
        );

        descripcionCol.setMinWidth(250);

        TableColumn<Curso, String> duracion =
                new TableColumn<>("Duración");

        duracion.setCellValueFactory(
                new PropertyValueFactory<>("duracion")
        );

        duracion.setMinWidth(120);

        TableColumn<Curso, Boolean> activo =
                new TableColumn<>("Estado");

        activo.setCellValueFactory(
                new PropertyValueFactory<>("activo")
        );

        activo.setMinWidth(110);

        activo.setCellFactory(columna ->
                new TableCell<Curso, Boolean>() {

            @Override
            protected void updateItem(Boolean valor, boolean vacio) {

                super.updateItem(valor, vacio);

                if (vacio || valor == null) {
                    setText(null);
                    setStyle("");
                } else {

                    setText(valor ? "Activo" : "Inactivo");

                    setStyle(valor
                            ? "-fx-text-fill: #16834A;"
                              + "-fx-font-weight: bold;"
                            : "-fx-text-fill: #B3382C;"
                              + "-fx-font-weight: bold;"
                    );
                }
            }
        });

        tabla.getColumns().addAll(
                nombre,
                descripcionCol,
                duracion,
                activo
        );

        // BOTONES

        Button nuevo = crearBoton(
                "+ Registrar curso", AZUL
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

        // REGISTRAR CURSO

        nuevo.setOnAction(e -> {

            TextField nombreCampo = new TextField();
            nombreCampo.setPromptText("Nombre del curso");

            TextField descripcionCampo = new TextField();
            descripcionCampo.setPromptText("Descripción del curso");

            TextField duracionCampo = new TextField();
            duracionCampo.setPromptText("Ejemplo: 6 meses");

            CheckBox activoCampo =
                    new CheckBox("Curso activo");

            activoCampo.setSelected(true);

            GridPane formulario = crearFormulario(
                    nombreCampo,
                    descripcionCampo,
                    duracionCampo,
                    activoCampo
            );

            Dialog<ButtonType> dialogo = crearDialogo(
                    "Registrar curso",
                    formulario
            );

            Optional<ButtonType> resultado =
                    dialogo.showAndWait();

            if (resultado.isPresent()
                    && resultado.get() == ButtonType.OK) {

                try {

                    Curso curso = new Curso(
                            null,
                            nombreCampo.getText().trim(),
                            descripcionCampo.getText().trim(),
                            duracionCampo.getText().trim(),
                            activoCampo.isSelected()
                    );

                    servicio.registrar(curso);

                    registrarLog("INSERT", "OK", null);

                    cargarCursos(tabla, buscador);

                    mostrarInformacion(
                            "Curso registrado correctamente."
                    );

                } catch (Exception ex) {

                    registrarLog(
                            "INSERT",
                            "ERROR",
                            ex.getMessage()
                    );

                    mostrarError(ex.getMessage());
                }
            }
        });

        // MODIFICAR CURSO

        modificar.setOnAction(e -> {

            Curso seleccionado =
                    tabla.getSelectionModel().getSelectedItem();

            if (seleccionado == null) {

                mostrarAviso("Selecciona un curso.");

                return;
            }

            TextField nombreCampo =
                    new TextField(seleccionado.getNombre());

            TextField descripcionCampo =
                    new TextField(seleccionado.getDescripcion());

            TextField duracionCampo =
                    new TextField(seleccionado.getDuracion());

            CheckBox activoCampo =
                    new CheckBox("Curso activo");

            activoCampo.setSelected(seleccionado.isActivo());

            GridPane formulario = crearFormulario(
                    nombreCampo,
                    descripcionCampo,
                    duracionCampo,
                    activoCampo
            );

            Dialog<ButtonType> dialogo = crearDialogo(
                    "Modificar curso",
                    formulario
            );

            Optional<ButtonType> resultado =
                    dialogo.showAndWait();

            if (resultado.isPresent()
                    && resultado.get() == ButtonType.OK) {

                try {

                    Curso actualizado = new Curso(
                            seleccionado.getId(),
                            nombreCampo.getText().trim(),
                            descripcionCampo.getText().trim(),
                            duracionCampo.getText().trim(),
                            activoCampo.isSelected()
                    );

                    if (servicio.modificar(actualizado)) {

                        registrarLog("UPDATE", "OK", null);

                        cargarCursos(tabla, buscador);

                        mostrarInformacion(
                                "Curso modificado correctamente."
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

        // ELIMINAR CURSO

        eliminar.setOnAction(e -> {

            Curso seleccionado =
                    tabla.getSelectionModel().getSelectedItem();

            if (seleccionado == null) {

                mostrarAviso("Selecciona un curso.");

                return;
            }

            Alert confirmacion =
                    new Alert(Alert.AlertType.CONFIRMATION);

            confirmacion.setTitle("Eliminar curso");
            confirmacion.setHeaderText(
                    "¿Estás seguro de eliminar este curso?"
            );

            confirmacion.setContentText(
                    "Curso: " + seleccionado.getNombre()
            );

            Optional<ButtonType> resultado =
                    confirmacion.showAndWait();

            if (resultado.isPresent()
                    && resultado.get() == ButtonType.OK) {

                try {

                    if (servicio.eliminar(seleccionado.getId())) {

                        registrarLog("DELETE", "OK", null);

                        cargarCursos(tabla, buscador);

                        mostrarInformacion(
                                "Curso eliminado correctamente."
                        );

                    } else {

                        mostrarAviso(
                                "No se ha encontrado el curso."
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

        // ACTUALIZAR LISTA

        actualizar.setOnAction(e ->
                cargarCursos(tabla, buscador)
        );

        // BÚSQUEDA EN TIEMPO REAL

        buscador.textProperty().addListener(
                (observable, anterior, nuevoTexto) -> {

                    if (cursosFiltrados == null) {
                        return;
                    }

                    String filtro = nuevoTexto == null
                            ? ""
                            : nuevoTexto.trim().toLowerCase();

                    cursosFiltrados.setPredicate(curso -> {

                        if (filtro.isEmpty()) {
                            return true;
                        }

                        return contiene(curso.getNombre(), filtro)
                                || contiene(
                                        curso.getDescripcion(),
                                        filtro
                                )
                                || contiene(
                                        curso.getDuracion(),
                                        filtro
                                );
                    });
                }
        );

        // BARRA DE ACCIONES

        HBox acciones = new HBox(
                10,
                nuevo,
                modificar,
                eliminar,
                actualizar
        );

        acciones.setAlignment(Pos.CENTER_LEFT);
        acciones.setPadding(new Insets(8, 0, 0, 0));

        // PANEL DE TABLA

        Label tituloTabla = new Label("Listado de cursos");

        tituloTabla.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                17
        ));

        tituloTabla.setTextFill(Color.web(TINTA));

        VBox panelTabla = new VBox(
                14,
                tituloTabla,
                buscador,
                tabla
        );

        panelTabla.setPadding(new Insets(18));

        panelTabla.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 10;"
                + "-fx-border-color: #DCE4EA;"
                + "-fx-border-radius: 10;"
        );

        VBox.setVgrow(tabla, Priority.ALWAYS);
        VBox.setVgrow(panelTabla, Priority.ALWAYS);

        // CONTENIDO PRINCIPAL

        VBox contenido = new VBox(
                16,
                panelTabla,
                acciones
        );

        contenido.setPadding(
                new Insets(0, 30, 30, 30)
        );

        VBox.setVgrow(panelTabla, Priority.ALWAYS);

        VBox raiz = new VBox(
                cabecera,
                contenido
        );

        raiz.setStyle(
                "-fx-background-color: " + FONDO + ";"
        );

        // CARGAR CURSOS

        cargarCursos(tabla, buscador);

        // CONFIGURAR VENTANA

        Scene escena = new Scene(raiz, 1050, 680);

        ventana.setScene(escena);

        ventana.setMinWidth(800);
        ventana.setMinHeight(550);

        ventana.setOnHidden(e -> servicio.cerrar());

        ventana.centerOnScreen();
        ventana.show();
    }

    // CREAR FORMULARIO

    private GridPane crearFormulario(
            TextField nombre,
            TextField descripcion,
            TextField duracion,
            CheckBox activo) {

        GridPane formulario = new GridPane();

        formulario.setPadding(new Insets(20));
        formulario.setHgap(15);
        formulario.setVgap(15);

        nombre.setPrefWidth(350);
        descripcion.setPrefWidth(350);
        duracion.setPrefWidth(350);

        formulario.add(new Label("Nombre del curso:"), 0, 0);
        formulario.add(nombre, 1, 0);

        formulario.add(new Label("Descripción:"), 0, 1);
        formulario.add(descripcion, 1, 1);

        formulario.add(new Label("Duración:"), 0, 2);
        formulario.add(duracion, 1, 2);

        formulario.add(activo, 1, 3);

        return formulario;
    }

    // CREAR DIÁLOGO

    private Dialog<ButtonType> crearDialogo(
            String titulo,
            GridPane formulario) {

        Dialog<ButtonType> dialogo = new Dialog<>();

        dialogo.setTitle(titulo);
        dialogo.setHeaderText("Introduce los datos del curso.");

        dialogo.getDialogPane().setContent(formulario);

        dialogo.getDialogPane().getButtonTypes().addAll(
                ButtonType.OK,
                ButtonType.CANCEL
        );

        dialogo.getDialogPane().setPrefWidth(580);

        return dialogo;
    }

    // CARGAR CURSOS

    private void cargarCursos(
            TableView<Curso> tabla,
            TextField buscador) {

        try {

            cursosFiltrados = new FilteredList<>(
                    FXCollections.observableArrayList(
                            servicio.listar()
                    ),
                    curso -> true
            );

            tabla.setItems(cursosFiltrados);

            if (buscador != null
                    && !buscador.getText().isBlank()) {

                String filtro = buscador.getText()
                        .trim().toLowerCase();

                cursosFiltrados.setPredicate(curso ->
                        contiene(curso.getNombre(), filtro)
                        || contiene(curso.getDescripcion(), filtro)
                        || contiene(curso.getDuracion(), filtro)
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

    // CREAR BOTONES

    private Button crearBoton(String texto, String color) {

        Button boton = new Button(texto);

        boton.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                12
        ));

        boton.setTextFill(Color.WHITE);
        boton.setCursor(Cursor.HAND);
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
