
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.modelo.Curso;
import com.mycompany.reto1_erandio_academy.modelo.Empleado;
import com.mycompany.reto1_erandio_academy.servicio.AsignacionServicio;
import com.mycompany.reto1_erandio_academy.servicio.CursoServicio;
import com.mycompany.reto1_erandio_academy.servicio.EmpleadoServicio;
import com.mycompany.reto1_erandio_academy.servicio.LogServicio;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
                    error);
        } finally {
            logServicio.cerrar();
        }
    }

    public void mostrar() {

        Stage ventana = new Stage();
        ventana.setTitle("Academy - Asignar profesores");

        ComboBox<Curso> comboCursos = new ComboBox<>();
        ComboBox<Empleado> comboProfesores = new ComboBox<>();

        comboCursos.setPromptText("Selecciona un curso");
        comboProfesores.setPromptText("Selecciona un profesor");

        // MOSTRAR NOMBRE DEL CURSO
        comboCursos.setConverter(
                new javafx.util.StringConverter<Curso>() {

            @Override
            public String toString(Curso curso) {
                return curso == null ? "" : curso.getNombre();
            }

            @Override
            public Curso fromString(String texto) {
                return null;
            }
        });

        // MOSTRAR NOMBRE DEL PROFESOR
        comboProfesores.setConverter(
                new javafx.util.StringConverter<Empleado>() {

            @Override
            public String toString(Empleado empleado) {
                return empleado == null ? "" : empleado.getNombre();
            }

            @Override
            public Empleado fromString(String texto) {
                return null;
            }
        });

        // CARGAR CURSOS
        comboCursos.setItems(
                FXCollections.observableArrayList(
                        cursoServicio.listar()));

        // CARGAR PROFESORES ACTIVOS
        List<Empleado> profesores = new ArrayList<>();

        for (Empleado empleado : empleadoServicio.listar()) {

            if (empleado.getPuesto() != null
                    && empleado.getPuesto().equalsIgnoreCase("Profesor")
                    && empleado.isActivo()) {

                profesores.add(empleado);
            }
        }

        comboProfesores.setItems(
                FXCollections.observableArrayList(profesores));

        // LISTA DE PROFESORES ASIGNADOS
        // GUARDAMOS EL OBJETO COMPLETO, INCLUIDO SU ID
        ListView<Empleado> listaAsignaciones = new ListView<>();

        // MOSTRAR SOLAMENTE EL NOMBRE EN LA LISTA
        listaAsignaciones.setCellFactory(param ->
                new ListCell<Empleado>() {

            @Override
            protected void updateItem(
                    Empleado empleado,
                    boolean vacio) {

                super.updateItem(empleado, vacio);

                if (vacio || empleado == null) {
                    setText(null);
                } else {
                    setText(empleado.getNombre());
                }
            }
        });

        // ACTUALIZAR PROFESORES AL SELECCIONAR CURSO
        comboCursos.setOnAction(e -> {

            Curso curso = comboCursos.getValue();

            if (curso != null) {
                cargarAsignaciones(curso, listaAsignaciones);
            }
        });

        Button asignar = new Button("Asignar profesor");
        Button quitar = new Button("Quitar profesor");

        // ASIGNAR PROFESOR
        asignar.setOnAction(e -> {

            Curso curso = comboCursos.getValue();
            Empleado profesor = comboProfesores.getValue();

            try {

                if (curso == null || profesor == null) {
                    mostrarAviso(
                            "Selecciona un curso y un profesor.");
                    return;
                }

                asignacionServicio.asignar(
                        profesor.getId(),
                        curso.getId());

                registrarLog("INSERT", "OK", null);

                cargarAsignaciones(
                        curso,
                        listaAsignaciones);

                mostrarInformacion(
                        "Profesor asignado correctamente.");

            } catch (Exception ex) {

                registrarLog(
                        "INSERT",
                        "ERROR",
                        ex.getMessage());

                mostrarError(ex.getMessage());
            }
        });

        // QUITAR PROFESOR
        quitar.setOnAction(e -> {

            Curso curso = comboCursos.getValue();

            Empleado seleccionado =
                    listaAsignaciones.getSelectionModel()
                            .getSelectedItem();

            if (curso == null || seleccionado == null) {
                mostrarAviso(
                        "Selecciona un curso y un profesor asignado.");
                return;
            }

            Alert confirmacion =
                    new Alert(Alert.AlertType.CONFIRMATION);

            confirmacion.setTitle("Quitar asignación");
            confirmacion.setHeaderText("¿Estás seguro?");
            confirmacion.setContentText(
                    "¿Quieres quitar esta asignación?");

            Optional<ButtonType> resultado =
                    confirmacion.showAndWait();

            if (resultado.isPresent()
                    && resultado.get() == ButtonType.OK) {

                try {

                    // OBTENER EL ID DIRECTAMENTE DEL OBJETO
                    ObjectId empleadoId = seleccionado.getId();

                    if (asignacionServicio.quitar(
                            empleadoId,
                            curso.getId())) {

                        registrarLog("DELETE", "OK", null);

                        cargarAsignaciones(
                                curso,
                                listaAsignaciones);

                        mostrarInformacion(
                                "Asignación eliminada correctamente.");

                    } else {

                        mostrarAviso(
                                "No se ha encontrado la asignación.");
                    }

                } catch (Exception ex) {

                    registrarLog(
                            "DELETE",
                            "ERROR",
                            ex.getMessage());

                    mostrarError(ex.getMessage());
                }
            }
        });

        // DISEÑO DE LA VENTANA
        VBox contenido = new VBox(
                12,
                new Label("Seleccionar curso:"),
                comboCursos,
                new Label("Seleccionar profesor:"),
                comboProfesores,
                asignar,
                new Label("Profesores asignados:"),
                listaAsignaciones,
                quitar
        );

        contenido.setPadding(new Insets(20));

        ventana.setScene(
                new Scene(contenido, 450, 500));

        // CERRAR CONEXIONES
        ventana.setOnHidden(e -> {
            asignacionServicio.cerrar();
            cursoServicio.cerrar();
            empleadoServicio.cerrar();
        });

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
                        curso.getId());

        for (Document documento : asignaciones) {

            ObjectId empleadoId =
                    documento.getObjectId("empleadoId");

            Empleado empleado =
                    empleadoServicio.buscarPorId(empleadoId);

            if (empleado != null) {
                profesoresAsignados.add(empleado);
            }
        }

        lista.setItems(
                FXCollections.observableArrayList(
                        profesoresAsignados));
    }

    // MENSAJE DE AVISO
    private void mostrarAviso(String mensaje) {

        Alert alerta =
                new Alert(Alert.AlertType.WARNING);

        alerta.setTitle("Academy");
        alerta.setHeaderText("Atención");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // MENSAJE DE INFORMACIÓN
    private void mostrarInformacion(String mensaje) {

        Alert alerta =
                new Alert(Alert.AlertType.INFORMATION);

        alerta.setTitle("Academy");
        alerta.setHeaderText("Operación correcta");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // MENSAJE DE ERROR
    private void mostrarError(String mensaje) {

        Alert alerta =
                new Alert(Alert.AlertType.ERROR);

        alerta.setTitle("Academy");
        alerta.setHeaderText("Ha ocurrido un error");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
