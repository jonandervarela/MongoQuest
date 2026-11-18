package com.mycompany.reto1_erandio_academy.vista;
import com.mycompany.reto1_erandio_academy.controlador.Gestor;

import com.mycompany.reto1_erandio_academy.controlador.DAOImpl;

import com.mycompany.reto1_erandio_academy.modelo.Actividad;
import com.mycompany.reto1_erandio_academy.modelo.Curso;
import com.mycompany.reto1_erandio_academy.modelo.Ejercicio;
import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.controlador.SesionUsuario;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * "Gestionar actividades" (Temas) del actor Empleado: CRUD de actividades
 * filtrado por los cursos que imparte el profesor, con ejercicios embebidos.
 */
public class ActividadesController implements Initializable {

    private static final String AZUL = "#1F6F8B";
    private static final String ROJO = "#B3382C";

    private final Gestor servicio = new Gestor();
    private final DAOImpl cursoDAO = new DAOImpl();
    private final Gestor asignacionServicio = new Gestor();

    @FXML private VBox contenido;
    @FXML private Label avisoSinCursos;
    @FXML private ComboBox<Curso> cursos;
    @FXML private TableView<Actividad> tabla;
    @FXML private TableColumn<Actividad, String> colTitulo;
    @FXML private TableColumn<Actividad, String> colDescripcion;
    @FXML private TableColumn<Actividad, Number> colNumEjercicios;
    @FXML private Button nuevo;
    @FXML private Button modificar;
    @FXML private Button eliminar;
    @FXML private Button actualizar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        contenido.setStyle("-fx-background-color: #F2F5F7;");

        List<Curso> cursosPermitidos = cursosDelUsuarioActual();

        cursos.setItems(FXCollections.observableArrayList(cursosPermitidos));

        cursos.setCellFactory(lista -> new ListCell<Curso>() {
            @Override
            protected void updateItem(Curso curso, boolean vacio) {
                super.updateItem(curso, vacio);
                setText(vacio || curso == null ? null : curso.getNombre());
            }
        });

        cursos.setButtonCell(new ListCell<Curso>() {
            @Override
            protected void updateItem(Curso curso, boolean vacio) {
                super.updateItem(curso, vacio);
                setText(vacio || curso == null ? null : curso.getNombre());
            }
        });

        colTitulo.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("titulo"));
        colDescripcion.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("descripcion"));
        colNumEjercicios.setCellValueFactory(datos ->
                new javafx.beans.property.SimpleIntegerProperty(
                        datos.getValue().getEjercicios() == null
                                ? 0 : datos.getValue().getEjercicios().size()));

        estiloBoton(nuevo, AZUL);
        estiloBoton(modificar, AZUL);
        estiloBoton(eliminar, ROJO);
        estiloBoton(actualizar, AZUL);

        if (cursosPermitidos.isEmpty()) {
            nuevo.setDisable(true);
        }

        if (esEmpleado() && cursosPermitidos.isEmpty()) {
            avisoSinCursos.setVisible(true);
            avisoSinCursos.setManaged(true);
        }

        cursos.setOnAction(e -> cargarActividades());

        contenido.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((o2, oldW, newW) -> {
                    if (newW != null) {
                        newW.addEventHandler(WindowEvent.WINDOW_HIDDEN, e -> cerrarDaos());
                    }
                });
            }
        });

        cargarActividades();
    }

    private void cerrarDaos() {
        servicio.cerrar();
        cursoDAO.cerrar();
        asignacionServicio.cerrar();
    }

    private List<Curso> cursosDelUsuarioActual() {

        List<Curso> todos = cursoDAO.listarCursos();

        Usuario usuario = SesionUsuario.getUsuarioActual();

        if (usuario == null || !esEmpleado()) {
            return todos;
        }

        if (usuario.getEmpleadoId() == null) {
            return new ArrayList<>();
        }

        Set<ObjectId> cursosAsignados = new HashSet<>();

        for (Document asignacion : asignacionServicio.listarAsignacionesPorEmpleado(usuario.getEmpleadoId())) {
            cursosAsignados.add(asignacion.getObjectId("cursoId"));
        }

        List<Curso> permitidos = new ArrayList<>();

        for (Curso curso : todos) {
            if (cursosAsignados.contains(curso.getId())) {
                permitidos.add(curso);
            }
        }

        return permitidos;
    }

    private boolean esEmpleado() {
        return SesionUsuario.tieneRol("EMPLEADO");
    }

    @FXML
    private void onNueva() {

        Curso curso = cursos.getValue();

        if (curso == null) {
            mostrarMensaje("Selecciona un curso.");
            return;
        }

        TextField tituloCampo = new TextField();
        TextField descripcionCampo = new TextField();
        PanelEjercicios panelEjercicios = crearPanelEjercicios(new ArrayList<>());

        GridPane formulario = crearFormulario(tituloCampo, descripcionCampo, panelEjercicios.nodo);

        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Nueva actividad");
        dialogo.getDialogPane().setContent(formulario);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> resultado = dialogo.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {

            try {
                Actividad actividad = new Actividad(
                        null,
                        tituloCampo.getText().trim(),
                        descripcionCampo.getText().trim(),
                        curso.getId(),
                        new ArrayList<>(panelEjercicios.lista)
                );

                servicio.registrarActividad(actividad);
                registrarLog("INSERT", "OK", null);
                cargarActividades();
                mostrarMensaje("Actividad registrada correctamente.");

            } catch (Exception ex) {
                registrarLog("INSERT", "ERROR", ex.getMessage());
                mostrarMensaje(ex.getMessage());
            }
        }
    }

    @FXML
    private void onModificar() {

        Actividad seleccionada = tabla.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarMensaje("Selecciona una actividad.");
            return;
        }

        TextField tituloCampo = new TextField(seleccionada.getTitulo());
        TextField descripcionCampo = new TextField(seleccionada.getDescripcion());

        PanelEjercicios panelEjercicios = crearPanelEjercicios(
                seleccionada.getEjercicios() == null
                        ? new ArrayList<>()
                        : seleccionada.getEjercicios());

        GridPane formulario = crearFormulario(tituloCampo, descripcionCampo, panelEjercicios.nodo);

        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Modificar actividad");
        dialogo.getDialogPane().setContent(formulario);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> resultado = dialogo.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {

            try {
                Actividad actualizada = new Actividad(
                        seleccionada.getId(),
                        tituloCampo.getText().trim(),
                        descripcionCampo.getText().trim(),
                        seleccionada.getCursoId(),
                        new ArrayList<>(panelEjercicios.lista)
                );

                servicio.modificarActividad(actualizada);
                registrarLog("UPDATE", "OK", null);
                cargarActividades();
                mostrarMensaje("Actividad modificada correctamente.");

            } catch (Exception ex) {
                registrarLog("UPDATE", "ERROR", ex.getMessage());
                mostrarMensaje(ex.getMessage());
            }
        }
    }

    @FXML
    private void onEliminar() {

        Actividad seleccionada = tabla.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarMensaje("Selecciona una actividad.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar actividad");
        confirmacion.setHeaderText("¿Estás seguro?");
        confirmacion.setContentText(seleccionada.getTitulo());

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {

            try {
                servicio.eliminarActividad(seleccionada.getId());
                registrarLog("DELETE", "OK", null);
                cargarActividades();
                mostrarMensaje("Actividad eliminada correctamente.");

            } catch (Exception ex) {
                registrarLog("DELETE", "ERROR", ex.getMessage());
                mostrarMensaje(ex.getMessage());
            }
        }
    }

    @FXML
    private void onActualizar() {
        cargarActividades();
    }

    private GridPane crearFormulario(TextField titulo, TextField descripcion, javafx.scene.Node panelEjercicios) {

        GridPane formulario = new GridPane();
        formulario.setPadding(new Insets(15));
        formulario.setHgap(10);
        formulario.setVgap(10);

        formulario.add(new Label("Título:"), 0, 0);
        formulario.add(titulo, 1, 0);

        formulario.add(new Label("Descripción:"), 0, 1);
        formulario.add(descripcion, 1, 1);

        formulario.add(new Label("Ejercicios:"), 0, 2);
        formulario.add(panelEjercicios, 1, 2);

        return formulario;
    }

    private PanelEjercicios crearPanelEjercicios(List<Ejercicio> iniciales) {

        ObservableList<Ejercicio> lista = FXCollections.observableArrayList(iniciales);

        ListView<Ejercicio> listaVisual = new ListView<>(lista);
        listaVisual.setPrefHeight(100);
        listaVisual.setPrefWidth(260);

        TextField nuevaPregunta = new TextField();
        nuevaPregunta.setPromptText("Pregunta del ejercicio");

        Button anadir = new Button("Añadir");
        estiloBoton(anadir, AZUL);
        anadir.setOnAction(e -> {
            String texto = nuevaPregunta.getText().trim();
            if (!texto.isEmpty()) {
                lista.add(new Ejercicio(texto));
                nuevaPregunta.clear();
            }
        });

        Button quitar = new Button("Quitar seleccionado");
        estiloBoton(quitar, ROJO);
        quitar.setOnAction(e -> {
            Ejercicio seleccionado = listaVisual.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                lista.remove(seleccionado);
            }
        });

        HBox fila = new HBox(8, nuevaPregunta, anadir, quitar);
        VBox contenedor = new VBox(6, listaVisual, fila);

        return new PanelEjercicios(contenedor, lista);
    }

    private static class PanelEjercicios {

        final VBox nodo;
        final ObservableList<Ejercicio> lista;

        PanelEjercicios(VBox nodo, ObservableList<Ejercicio> lista) {
            this.nodo = nodo;
            this.lista = lista;
        }
    }

    private void cargarActividades() {

        Curso curso = cursos.getValue();

        if (curso == null) {
            tabla.getItems().clear();
            return;
        }

        tabla.setItems(FXCollections.observableArrayList(servicio.listarActividadesPorCurso(curso.getId())));
    }

    private void registrarLog(String operacion, String resultado, String error) {
        Gestor logServicio = new Gestor();
        try {
            logServicio.registrarLog(operacion, "actividades", resultado, error);
        } finally {
            logServicio.cerrar();
        }
    }

    private void mostrarMensaje(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Academy");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void estiloBoton(Button boton, String colorAcento) {

        String base =
                "-fx-background-color: white;"
                + "-fx-text-fill: #12283A;"
                + "-fx-font-size: 13px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;"
                + "-fx-border-color: #D5DDE3;"
                + "-fx-border-radius: 8;"
                + "-fx-cursor: hand;"
                + "-fx-padding: 8 16 8 16;";

        String encima =
                "-fx-background-color: " + colorAcento + ";"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 13px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;"
                + "-fx-border-radius: 8;"
                + "-fx-cursor: hand;"
                + "-fx-padding: 8 16 8 16;";

        boton.setStyle(base);
        boton.setOnMouseEntered(e -> boton.setStyle(encima));
        boton.setOnMouseExited(e -> boton.setStyle(base));
    }
}
