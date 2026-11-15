
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.dao.CursoDAO;
import com.mycompany.reto1_erandio_academy.modelo.Curso;
import com.mycompany.reto1_erandio_academy.modelo.Actividad;
import com.mycompany.reto1_erandio_academy.servicio.ActividadServicio;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;

public class VentanaTemarioUsuario {

    private static final String TINTA = "#12283A";
    private static final String AZUL = "#1F6F8B";
    private static final String FONDO = "#F2F5F7";
    private static final String GRIS = "#64748B";

    private final CursoDAO cursoDAO;
    private final ActividadServicio actividadServicio;

    private ComboBox<Curso> comboCursos;
    private TableView<Actividad> tablaActividades;

    public VentanaTemarioUsuario() {
        cursoDAO = new CursoDAO();
        actividadServicio = new ActividadServicio();
    }

    public void mostrar() {

        Stage ventana = new Stage();

        // =========================
        // PANEL PRINCIPAL
        // =========================

        BorderPane raiz = new BorderPane();
        raiz.setStyle("-fx-background-color: " + FONDO + ";");

        // =========================
        // CABECERA
        // =========================

        Label titulo = new Label("Consultar temario");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 25));
        titulo.setTextFill(Color.web(TINTA));

        Label subtitulo = new Label(
                "Consulta las actividades y contenidos de tus cursos."
        );
        subtitulo.setFont(Font.font("Segoe UI", 13));
        subtitulo.setTextFill(Color.web(GRIS));

        VBox textosCabecera = new VBox(5, titulo, subtitulo);

        Label etiqueta = new Label("TEMARIO");
        etiqueta.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        etiqueta.setTextFill(Color.web(AZUL));
        etiqueta.setPadding(new Insets(8, 14, 8, 14));
        etiqueta.setStyle(
                "-fx-background-color: #DCEEF3;"
                + "-fx-background-radius: 20;"
        );

        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);

        HBox cabecera = new HBox(
                textosCabecera,
                espacio,
                etiqueta
        );

        cabecera.setAlignment(Pos.CENTER_LEFT);
        cabecera.setPadding(new Insets(28, 35, 22, 35));

        // =========================
        // TARJETA DE SELECCIÓN
        // =========================

        Label tituloSeleccion = new Label("Seleccionar curso");
        tituloSeleccion.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 17)
        );
        tituloSeleccion.setTextFill(Color.web(TINTA));

        Label descripcionSeleccion = new Label(
                "Elige un curso para consultar su temario."
        );
        descripcionSeleccion.setFont(Font.font("Segoe UI", 12));
        descripcionSeleccion.setTextFill(Color.web(GRIS));

        comboCursos = new ComboBox<>();
        comboCursos.setPromptText("Selecciona un curso...");
        comboCursos.setPrefWidth(350);
        comboCursos.setMaxWidth(Double.MAX_VALUE);
        comboCursos.setStyle(
                "-fx-font-family: 'Segoe UI';"
                + "-fx-font-size: 13px;"
        );

        comboCursos.setConverter(new StringConverter<Curso>() {

            @Override
            public String toString(Curso curso) {
                return curso == null ? "" : curso.getNombre();
            }

            @Override
            public Curso fromString(String texto) {
                return null;
            }
        });

        comboCursos.setCellFactory(lista -> new javafx.scene.control.ListCell<Curso>() {

            @Override
            protected void updateItem(Curso curso, boolean vacio) {
                super.updateItem(curso, vacio);

                setText(
                        vacio || curso == null
                                ? null
                                : curso.getNombre()
                );
            }
        });

        Button botonActualizar = new Button("Actualizar temario");
        botonActualizar.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 12)
        );
        botonActualizar.setTextFill(Color.WHITE);
        botonActualizar.setPadding(new Insets(11, 20, 11, 20));
        botonActualizar.setCursor(Cursor.HAND);
        botonActualizar.setStyle(
                "-fx-background-color: " + AZUL + ";"
                + "-fx-background-radius: 7;"
        );

        botonActualizar.setOnMouseEntered(e ->
                botonActualizar.setStyle(
                        "-fx-background-color: #18566D;"
                        + "-fx-background-radius: 7;"
                )
        );

        botonActualizar.setOnMouseExited(e ->
                botonActualizar.setStyle(
                        "-fx-background-color: " + AZUL + ";"
                        + "-fx-background-radius: 7;"
                )
        );

        botonActualizar.setOnAction(e -> cargarTemario());

        comboCursos.setOnAction(e -> cargarTemario());

        HBox filaSeleccion = new HBox(
                15,
                comboCursos,
                botonActualizar
        );

        filaSeleccion.setAlignment(Pos.CENTER_LEFT);

        VBox tarjetaSeleccion = new VBox(
                12,
                tituloSeleccion,
                descripcionSeleccion,
                filaSeleccion
        );

        tarjetaSeleccion.setPadding(new Insets(22));
        tarjetaSeleccion.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #E1E8ED;"
                + "-fx-border-radius: 12;"
        );

        // =========================
        // TABLA DE ACTIVIDADES
        // =========================

        Label tituloTabla = new Label("Contenido del temario");
        tituloTabla.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 18)
        );
        tituloTabla.setTextFill(Color.web(TINTA));

        Label descripcionTabla = new Label(
                "Aquí aparecerán las actividades del curso seleccionado."
        );
        descripcionTabla.setFont(Font.font("Segoe UI", 12));
        descripcionTabla.setTextFill(Color.web(GRIS));

        tablaActividades = new TableView<>();
        tablaActividades.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
        tablaActividades.setPlaceholder(
                new Label("Selecciona un curso para consultar su temario.")
        );

        TableColumn<Actividad, String> columnaTitulo =
                new TableColumn<>("Título");

        columnaTitulo.setCellValueFactory(
                new PropertyValueFactory<>("titulo")
        );
        columnaTitulo.setMinWidth(180);
        columnaTitulo.setPrefWidth(280);

        TableColumn<Actividad, String> columnaDescripcion =
                new TableColumn<>("Descripción");

        columnaDescripcion.setCellValueFactory(
                new PropertyValueFactory<>("descripcion")
        );
        columnaDescripcion.setMinWidth(250);
        columnaDescripcion.setPrefWidth(550);

        tablaActividades.getColumns().addAll(
                columnaTitulo,
                columnaDescripcion
        );

        tablaActividades.setStyle(
                "-fx-font-family: 'Segoe UI';"
                + "-fx-font-size: 13px;"
        );

        VBox tarjetaTabla = new VBox(
                12,
                tituloTabla,
                descripcionTabla,
                tablaActividades
        );

        tarjetaTabla.setPadding(new Insets(22));
        tarjetaTabla.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #E1E8ED;"
                + "-fx-border-radius: 12;"
        );

        VBox.setVgrow(tablaActividades, Priority.ALWAYS);
        VBox.setVgrow(tarjetaTabla, Priority.ALWAYS);

        // =========================
        // CONTENIDO CENTRAL
        // =========================

        VBox contenido = new VBox(
                20,
                tarjetaSeleccion,
                tarjetaTabla
        );

        contenido.setPadding(
                new Insets(10, 35, 30, 35)
        );

        VBox zonaCentral = new VBox(
                cabecera,
                contenido
        );

        VBox.setVgrow(contenido, Priority.ALWAYS);
        zonaCentral.setStyle(
                "-fx-background-color: " + FONDO + ";"
        );

        raiz.setCenter(zonaCentral);

        // =========================
        // CARGAR CURSOS
        // =========================

        cargarCursos();

        // =========================
        // MOSTRAR VENTANA
        // =========================

        Scene escena = new Scene(raiz, 1050, 700);

        ventana.setTitle("Erandio Academy - Consultar temario");
        ventana.setMinWidth(800);
        ventana.setMinHeight(550);
        ventana.setScene(escena);

        ventana.setOnHidden(e -> {
            actividadServicio.cerrar();
            cursoDAO.cerrar();
        });

        ventana.show();
    }

    // =========================
    // CARGAR CURSOS
    // =========================

    private void cargarCursos() {

        List<Curso> cursos = cursoDAO.listar();

        comboCursos.setItems(
                FXCollections.observableArrayList(cursos)
        );
    }

    // =========================
    // CARGAR TEMARIO
    // =========================

    private void cargarTemario() {

        Curso curso = comboCursos.getValue();

        if (curso == null) {
            tablaActividades.getItems().clear();
            return;
        }

        List<Actividad> actividades =
                actividadServicio.listarPorCurso(curso.getId());

        tablaActividades.setItems(
                FXCollections.observableArrayList(actividades)
        );
    }
}
