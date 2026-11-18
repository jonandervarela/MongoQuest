package com.mycompany.reto1_erandio_academy.vista;
import com.mycompany.reto1_erandio_academy.controlador.Gestor;

import com.mycompany.reto1_erandio_academy.modelo.Curso;
import com.mycompany.reto1_erandio_academy.modelo.Empleado;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;

import org.bson.Document;
import org.bson.types.ObjectId;

public class AsignacionesController implements Initializable {

    private final Gestor asignacionServicio = new Gestor();
    private final Gestor cursoServicio = new Gestor();
    private final Gestor empleadoServicio = new Gestor();

    @FXML private VBox contenido;
    @FXML private ComboBox<Curso> comboCursos;
    @FXML private ComboBox<Empleado> comboProfesores;
    @FXML private Button asignar;
    @FXML private ListView<Empleado> listaAsignaciones;
    @FXML private Button quitar;

    private void registrarLog(String operacion, String resultado, String error) {
        Gestor logServicio = new Gestor();
        try {
            logServicio.registrarLog(operacion, "asignaciones", resultado, error);
        } finally {
            logServicio.cerrar();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        comboCursos.setConverter(new javafx.util.StringConverter<Curso>() {
            @Override
            public String toString(Curso curso) {
                return curso == null ? "" : curso.getNombre();
            }

            @Override
            public Curso fromString(String texto) {
                return null;
            }
        });

        comboProfesores.setConverter(new javafx.util.StringConverter<Empleado>() {
            @Override
            public String toString(Empleado empleado) {
                return empleado == null ? "" : empleado.getNombre();
            }

            @Override
            public Empleado fromString(String texto) {
                return null;
            }
        });

        comboCursos.setItems(FXCollections.observableArrayList(cursoServicio.listarCursos()));

        List<Empleado> profesores = new ArrayList<>();
        for (Empleado empleado : empleadoServicio.listarEmpleados()) {
            if (empleado.getPuesto() != null
                    && empleado.getPuesto().equalsIgnoreCase("Profesor")
                    && empleado.isActivo()) {
                profesores.add(empleado);
            }
        }
        comboProfesores.setItems(FXCollections.observableArrayList(profesores));

        listaAsignaciones.setCellFactory(param -> new ListCell<Empleado>() {
            @Override
            protected void updateItem(Empleado empleado, boolean vacio) {
                super.updateItem(empleado, vacio);
                setText(vacio || empleado == null ? null : empleado.getNombre());
            }
        });

        comboCursos.setOnAction(e -> {
            Curso curso = comboCursos.getValue();
            if (curso != null) {
                cargarAsignaciones(curso);
            }
        });

        contenido.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((o2, oldW, newW) -> {
                    if (newW != null) {
                        newW.addEventHandler(WindowEvent.WINDOW_HIDDEN, e -> {
                            asignacionServicio.cerrar();
                            cursoServicio.cerrar();
                            empleadoServicio.cerrar();
                        });
                    }
                });
            }
        });
    }

    @FXML
    private void onAsignar() {

        Curso curso = comboCursos.getValue();
        Empleado profesor = comboProfesores.getValue();

        try {
            if (curso == null || profesor == null) {
                mostrarAviso("Selecciona un curso y un profesor.");
                return;
            }

            asignacionServicio.asignarProfesor(profesor.getId(), curso.getId());
            registrarLog("INSERT", "OK", null);
            cargarAsignaciones(curso);
            mostrarInformacion("Profesor asignado correctamente.");

        } catch (Exception ex) {
            registrarLog("INSERT", "ERROR", ex.getMessage());
            mostrarError(ex.getMessage());
        }
    }

    @FXML
    private void onQuitar() {

        Curso curso = comboCursos.getValue();
        Empleado seleccionado = listaAsignaciones.getSelectionModel().getSelectedItem();

        if (curso == null || seleccionado == null) {
            mostrarAviso("Selecciona un curso y un profesor asignado.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Quitar asignación");
        confirmacion.setHeaderText("¿Estás seguro?");
        confirmacion.setContentText("¿Quieres quitar esta asignación?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {

            try {
                ObjectId empleadoId = seleccionado.getId();

                if (asignacionServicio.quitarProfesor(empleadoId, curso.getId())) {
                    registrarLog("DELETE", "OK", null);
                    cargarAsignaciones(curso);
                    mostrarInformacion("Asignación eliminada correctamente.");
                } else {
                    mostrarAviso("No se ha encontrado la asignación.");
                }

            } catch (Exception ex) {
                registrarLog("DELETE", "ERROR", ex.getMessage());
                mostrarError(ex.getMessage());
            }
        }
    }

    private void cargarAsignaciones(Curso curso) {

        List<Empleado> profesoresAsignados = new ArrayList<>();
        List<Document> asignaciones = asignacionServicio.listarAsignacionesPorCurso(curso.getId());

        for (Document documento : asignaciones) {
            ObjectId empleadoId = documento.getObjectId("empleadoId");
            Empleado empleado = empleadoServicio.buscarEmpleadoPorId(empleadoId);
            if (empleado != null) {
                profesoresAsignados.add(empleado);
            }
        }

        listaAsignaciones.setItems(FXCollections.observableArrayList(profesoresAsignados));
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
