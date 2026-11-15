package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.dao.CursoDAO;
import com.mycompany.reto1_erandio_academy.dao.EntregaDAO;
import com.mycompany.reto1_erandio_academy.dao.TareaDAO;
import com.mycompany.reto1_erandio_academy.dao.UsuarioDAO;
import com.mycompany.reto1_erandio_academy.modelo.Curso;
import com.mycompany.reto1_erandio_academy.modelo.Entrega;
import com.mycompany.reto1_erandio_academy.modelo.Matricula;
import com.mycompany.reto1_erandio_academy.modelo.Tarea;
import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.modelo.Valoracion;
import com.mycompany.reto1_erandio_academy.servicio.AsignacionServicio;
import com.mycompany.reto1_erandio_academy.servicio.MatriculaServicio;
import com.mycompany.reto1_erandio_academy.servicio.SesionUsuario;
import com.mycompany.reto1_erandio_academy.servicio.ValoracionServicio;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Ventana "Consultar alumnos" del profesor (actor Empleado).
 *
 * Muestra, para el curso seleccionado (solo los cursos que ese
 * profesor imparte), los alumnos matriculados y, al seleccionar
 * uno, sus entregas de las tareas de ese curso y su valoración.
 */
public class VentanaConsultarAlumnos {

    private final CursoDAO cursoDAO = new CursoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final MatriculaServicio matriculaServicio = new MatriculaServicio();
    private final TareaDAO tareaDAO = new TareaDAO();
    private final EntregaDAO entregaDAO = new EntregaDAO();
    private final ValoracionServicio valoracionServicio = new ValoracionServicio();
    private final AsignacionServicio asignacionServicio = new AsignacionServicio();

    private final TableView<FilaAlumno> tablaAlumnos = new TableView<>();
    private final TableView<FilaEntrega> tablaEntregas = new TableView<>();
    private final Label etiquetaValoracion = new Label();

    public void mostrar() {

        Stage ventana = new Stage();
        ventana.setTitle("Academy - Consultar alumnos");

        // ---------- Selector de curso (solo los del profesor) ----------
        List<Curso> cursosPermitidos = cursosDelProfesorActual();

        ComboBox<Curso> cursos = new ComboBox<>();
        cursos.setPromptText("Selecciona un curso");
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

        // ---------- Tabla de alumnos matriculados ----------
        TableColumn<FilaAlumno, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().nombre));

        TableColumn<FilaAlumno, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().email));

        TableColumn<FilaAlumno, String> colEstado = new TableColumn<>("Matrícula");
        colEstado.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().estadoMatricula));

        TableColumn<FilaAlumno, Number> colProgreso = new TableColumn<>("Progreso %");
        colProgreso.setCellValueFactory(d ->
                new SimpleIntegerProperty(d.getValue().progreso));

        tablaAlumnos.getColumns().setAll(
                colNombre, colEmail, colEstado, colProgreso);
        tablaAlumnos.setPrefHeight(180);

        // ---------- Tabla de entregas del alumno seleccionado ----------
        TableColumn<FilaEntrega, String> colTarea = new TableColumn<>("Tarea");
        colTarea.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().tarea));

        TableColumn<FilaEntrega, String> colFechaLimite = new TableColumn<>("Fecha límite");
        colFechaLimite.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().fechaLimite));

        TableColumn<FilaEntrega, String> colEstadoEntrega = new TableColumn<>("Entrega");
        colEstadoEntrega.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().estadoEntrega));

        TableColumn<FilaEntrega, String> colFechaEntrega = new TableColumn<>("Fecha entrega");
        colFechaEntrega.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().fechaEntrega));

        TableColumn<FilaEntrega, String> colArchivo = new TableColumn<>("Archivo");
        colArchivo.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().archivo));

        tablaEntregas.getColumns().setAll(
                colTarea, colFechaLimite, colEstadoEntrega,
                colFechaEntrega, colArchivo);
        tablaEntregas.setPrefHeight(160);

        etiquetaValoracion.setWrapText(true);

        Label tituloAlumnos = new Label("Alumnos matriculados en el curso:");
        Label tituloDetalle = new Label("Tareas, entregas y valoración del alumno seleccionado:");

        VBox contenido = new VBox(
                10,
                new Label("Curso:"),
                cursos,
                tituloAlumnos,
                tablaAlumnos,
                tituloDetalle,
                tablaEntregas,
                etiquetaValoracion
        );
        contenido.setPadding(new Insets(20));
        contenido.setStyle("-fx-background-color: #F2F5F7;");

        if (cursosPermitidos.isEmpty()) {
            contenido.getChildren().add(0, new Label(
                    "No tienes ningún curso asignado todavía. "
                    + "Pide al administrador que te asigne uno."));
        }

        // ---------- Eventos ----------
        cursos.setOnAction(e -> cargarAlumnos(cursos.getValue()));

        tablaAlumnos.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, seleccionado) ->
                        cargarDetalleAlumno(cursos.getValue(), seleccionado));

        ventana.setScene(new Scene(contenido, 780, 650));

        ventana.setOnHidden(e -> {
            cursoDAO.cerrar();
            usuarioDAO.cerrar();
            matriculaServicio.cerrar();
            tareaDAO.cerrar();
            entregaDAO.cerrar();
            valoracionServicio.cerrar();
            asignacionServicio.cerrar();
        });

        ventana.show();
    }

    // Cursos que imparte el profesor que ha entrado (mismo criterio
    // que en "Gestionar actividades": admin ve todos, empleado solo
    // los suyos según la colección "asignaciones")
    private List<Curso> cursosDelProfesorActual() {

        List<Curso> todos = cursoDAO.listar();

        Usuario usuario = SesionUsuario.getUsuarioActual();

        if (usuario == null || !SesionUsuario.tieneRol("EMPLEADO")) {
            return todos;
        }

        if (usuario.getEmpleadoId() == null) {
            return new ArrayList<>();
        }

        Set<ObjectId> cursosAsignados = new HashSet<>();

        for (Document asignacion
                : asignacionServicio.listarPorEmpleado(usuario.getEmpleadoId())) {

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

    // Carga la tabla de alumnos matriculados en el curso elegido
    private void cargarAlumnos(Curso curso) {

        tablaEntregas.getItems().clear();
        etiquetaValoracion.setText("");

        if (curso == null) {
            tablaAlumnos.getItems().clear();
            return;
        }

        List<FilaAlumno> filas = new ArrayList<>();

        for (Matricula matricula
                : matriculaServicio.listarPorCurso(curso.getId())) {

            Usuario alumno = usuarioDAO.buscarPorId(matricula.getUsuarioId());

            if (alumno == null) {
                continue;
            }

            FilaAlumno fila = new FilaAlumno();
            fila.usuario = alumno;
            fila.nombre = alumno.getNombre();
            fila.email = alumno.getEmail();
            fila.estadoMatricula = matricula.getEstado();
            fila.progreso = matricula.getProgreso();

            filas.add(fila);
        }

        tablaAlumnos.setItems(FXCollections.observableArrayList(filas));
    }

    // Carga las entregas y la valoración del alumno seleccionado
    // en el curso elegido
    private void cargarDetalleAlumno(Curso curso, FilaAlumno alumno) {

        tablaEntregas.getItems().clear();
        etiquetaValoracion.setText("");

        if (curso == null || alumno == null) {
            return;
        }

        // Tareas del curso y, para cada una, si este alumno ha entregado
        List<FilaEntrega> filas = new ArrayList<>();

        for (Tarea tarea : tareaDAO.listarPorCurso(curso.getId())) {

            Entrega entrega = entregaDAO.buscarPorTareaYUsuario(
                    tarea.getId(), alumno.usuario.getId());

            FilaEntrega fila = new FilaEntrega();
            fila.tarea = tarea.getTitulo();
            fila.fechaLimite = tarea.getFechaLimite();

            if (entrega == null) {
                fila.estadoEntrega = "Sin entregar";
                fila.fechaEntrega = "";
                fila.archivo = "";
            } else {
                fila.estadoEntrega = entrega.getEstado();
                fila.fechaEntrega = entrega.getFechaEntrega();
                fila.archivo = entrega.getNombreArchivo();
            }

            filas.add(fila);
        }

        tablaEntregas.setItems(FXCollections.observableArrayList(filas));

        // Valoración de ese alumno para este curso (si la ha hecho)
        Valoracion valoracion = null;

        for (Valoracion v
                : valoracionServicio.listarPorCurso(curso.getId())) {

            if (v.getUsuarioId().equals(alumno.usuario.getId())) {
                valoracion = v;
                break;
            }
        }

        if (valoracion == null) {
            etiquetaValoracion.setText("Este alumno no ha valorado el curso.");
        } else {
            etiquetaValoracion.setText(
                    "Valoración del curso: " + valoracion.getPuntuacion()
                    + "/5 — " + (valoracion.getComentario() == null
                            ? "(sin comentario)" : valoracion.getComentario()));
        }
    }

    // ---------- Filas auxiliares para las tablas ----------

    private static class FilaAlumno {
        Usuario usuario;
        String nombre;
        String email;
        String estadoMatricula;
        int progreso;
    }

    private static class FilaEntrega {
        String tarea;
        String fechaLimite;
        String estadoEntrega;
        String fechaEntrega;
        String archivo;
    }
}
