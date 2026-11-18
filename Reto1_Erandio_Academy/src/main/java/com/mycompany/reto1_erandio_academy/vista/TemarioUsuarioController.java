package com.mycompany.reto1_erandio_academy.vista;
import com.mycompany.reto1_erandio_academy.controlador.Gestor;

import com.mycompany.reto1_erandio_academy.controlador.DAOImpl;

import com.mycompany.reto1_erandio_academy.modelo.Actividad;
import com.mycompany.reto1_erandio_academy.modelo.Curso;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;

public class TemarioUsuarioController implements Initializable {

    private final DAOImpl cursoDAO = new DAOImpl();
    private final Gestor actividadServicio = new Gestor();

    @FXML private VBox contenido;
    @FXML private Label titulo;
    @FXML private ComboBox<Curso> comboCursos;
    @FXML private TableView<Actividad> tablaActividades;
    @FXML private TableColumn<Actividad, String> columnaTitulo;
    @FXML private TableColumn<Actividad, String> columnaDescripcion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

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

        comboCursos.setCellFactory(lista -> new ListCell<Curso>() {
            @Override
            protected void updateItem(Curso curso, boolean vacio) {
                super.updateItem(curso, vacio);
                setText(vacio || curso == null ? null : curso.getNombre());
            }
        });

        columnaTitulo.setCellValueFactory(dato ->
                new javafx.beans.property.SimpleStringProperty(dato.getValue().getTitulo()));
        columnaDescripcion.setCellValueFactory(dato ->
                new javafx.beans.property.SimpleStringProperty(dato.getValue().getDescripcion()));

        comboCursos.setOnAction(e -> cargarTemario());

        cargarCursos();

        contenido.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((o2, oldW, newW) -> {
                    if (newW != null) {
                        newW.addEventHandler(WindowEvent.WINDOW_HIDDEN, e -> {
                            actividadServicio.cerrar();
                            cursoDAO.cerrar();
                        });
                    }
                });
            }
        });
    }

    private void cargarCursos() {
        List<Curso> cursos = cursoDAO.listarCursos();
        comboCursos.setItems(FXCollections.observableArrayList(cursos));
    }

    @FXML
    private void onActualizar() {
        cargarTemario();
    }

    private void cargarTemario() {

        Curso curso = comboCursos.getValue();

        if (curso == null) {
            return;
        }

        List<Actividad> actividades = actividadServicio.listarActividadesPorCurso(curso.getId());
        tablaActividades.setItems(FXCollections.observableArrayList(actividades));
    }
}
