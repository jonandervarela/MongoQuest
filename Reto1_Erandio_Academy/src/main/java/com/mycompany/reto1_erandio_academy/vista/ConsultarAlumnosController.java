package com.mycompany.reto1_erandio_academy.vista;
import com.mycompany.reto1_erandio_academy.controlador.Gestor;

import com.mycompany.reto1_erandio_academy.controlador.DAOImpl;

import com.mycompany.reto1_erandio_academy.controlador.SesionUsuario;
import com.mycompany.reto1_erandio_academy.modelo.Curso;
import com.mycompany.reto1_erandio_academy.modelo.Entrega;
import com.mycompany.reto1_erandio_academy.modelo.Matricula;
import com.mycompany.reto1_erandio_academy.modelo.Tarea;
import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.modelo.Valoracion;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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

import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * "Consultar alumnos" del profesor (actor Empleado).
 * Muestra, para el curso seleccionado (solo los que ese profesor imparte),
 * los alumnos matriculados y, al seleccionar uno, sus entregas y valoración.
 */
public class ConsultarAlumnosController implements Initializable {

    private final DAOImpl cursoDAO = new DAOImpl();
    private final DAOImpl usuarioDAO = new DAOImpl();
    private final Gestor matriculaServicio = new Gestor();
    private final DAOImpl tareaDAO = new DAOImpl();
    private final DAOImpl entregaDAO = new DAOImpl();
    private final Gestor valoracionServicio = new Gestor();
    private final Gestor asignacionServicio = new Gestor();

    @FXML private VBox contenido;
    @FXML private Label avisoSinCursos;
    @FXML private ComboBox<Curso> cursos;
    @FXML private Label tituloAlumnos;
    @FXML private TableView<FilaAlumno> tablaAlumnos;
    @FXML private TableColumn<FilaAlumno, String> colNombre;
    @FXML private TableColumn<FilaAlumno, String> colEmail;
    @FXML private TableColumn<FilaAlumno, String> colEstado;
    @FXML private TableColumn<FilaAlumno, Number> colProgreso;
    @FXML private Label tituloDetalle;
    @FXML private TableView<FilaEntrega> tablaEntregas;
    @FXML private TableColumn<FilaEntrega, String> colTarea;
    @FXML private TableColumn<FilaEntrega, String> colFechaLimite;
    @FXML private TableColumn<FilaEntrega, String> colEstadoEntrega;
    @FXML private TableColumn<FilaEntrega, String> colFechaEntrega;
    @FXML private TableColumn<FilaEntrega, String> colArchivo;
    @FXML private Label etiquetaValoracion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        contenido.setStyle("-fx-background-color: #F2F5F7;");

        List<Curso> cursosPermitidos = cursosDelProfesorActual();

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

        colNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().nombre));
        colEmail.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().email));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().estadoMatricula));
        colProgreso.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().progreso));

        colTarea.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().tarea));
        colFechaLimite.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().fechaLimite));
        colEstadoEntrega.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().estadoEntrega));
        colFechaEntrega.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().fechaEntrega));
        colArchivo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().archivo));

        if (cursosPermitidos.isEmpty()) {
            avisoSinCursos.setVisible(true);
            avisoSinCursos.setManaged(true);
        }

        cursos.setOnAction(e -> cargarAlumnos(cursos.getValue()));

        tablaAlumnos.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, seleccionado) ->
                        cargarDetalleAlumno(cursos.getValue(), seleccionado));

        contenido.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((o2, oldW, newW) -> {
                    if (newW != null) {
                        newW.addEventHandler(WindowEvent.WINDOW_HIDDEN, e -> cerrarDaos());
                    }
                });
            }
        });
    }

    private void cerrarDaos() {
        cursoDAO.cerrar();
        usuarioDAO.cerrar();
        matriculaServicio.cerrar();
        tareaDAO.cerrar();
        entregaDAO.cerrar();
        valoracionServicio.cerrar();
        asignacionServicio.cerrar();
    }

    // Cursos que imparte el profesor que ha entrado (admin ve todos,
    // empleado solo los suyos según la colección "asignaciones")
    private List<Curso> cursosDelProfesorActual() {

        List<Curso> todos = cursoDAO.listarCursos();

        Usuario usuario = SesionUsuario.getUsuarioActual();

        if (usuario == null || !SesionUsuario.tieneRol("EMPLEADO")) {
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

    private void cargarAlumnos(Curso curso) {

        tablaEntregas.getItems().clear();
        etiquetaValoracion.setText("");

        if (curso == null) {
            tablaAlumnos.getItems().clear();
            return;
        }

        List<FilaAlumno> filas = new ArrayList<>();

        for (Matricula matricula : matriculaServicio.listarMatriculasPorCurso(curso.getId())) {

            Usuario alumno = usuarioDAO.buscarUsuarioPorId(matricula.getUsuarioId());

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

    private void cargarDetalleAlumno(Curso curso, FilaAlumno alumno) {

        tablaEntregas.getItems().clear();
        etiquetaValoracion.setText("");

        if (curso == null || alumno == null) {
            return;
        }

        List<FilaEntrega> filas = new ArrayList<>();

        for (Tarea tarea : tareaDAO.listarTareasPorCurso(curso.getId())) {

            Entrega entrega = entregaDAO.buscarEntregaPorTareaYUsuario(tarea.getId(), alumno.usuario.getId());

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

        Valoracion valoracion = null;

        for (Valoracion v : valoracionServicio.listarValoracionesPorCurso(curso.getId())) {
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
