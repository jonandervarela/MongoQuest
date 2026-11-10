package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.dao.AsignacionDAO;
import com.mycompany.reto1_erandio_academy.modelo.Curso;
import com.mycompany.reto1_erandio_academy.modelo.Tarea;
import com.mycompany.reto1_erandio_academy.servicio.CursoServicio;
import com.mycompany.reto1_erandio_academy.servicio.TareaServicio;
import com.mycompany.reto1_erandio_academy.servicio.LogServicio;
import com.mycompany.reto1_erandio_academy.servicio.SesionUsuario;
import com.mycompany.reto1_erandio_academy.servicio.ExportarExcel;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class VentanaTareas {

    private final TareaServicio tareaServicio;
    private final CursoServicio cursoServicio;
    private final AsignacionDAO asignacionDAO;

    private final TableView<Tarea> tabla = new TableView<>();

    private Tarea tareaEnEdicion = null;

    private final String rolActual;
    private final ObjectId empleadoIdActual;

    public VentanaTareas() {

        tareaServicio = new TareaServicio();
        cursoServicio = new CursoServicio();
        asignacionDAO = new AsignacionDAO();

        if (!SesionUsuario.haySesion()) {
            throw new IllegalStateException(
                    "No hay ningún usuario conectado.");
        }

        rolActual = SesionUsuario.getUsuarioActual().getRol() == null
                ? ""
                : SesionUsuario.getUsuarioActual()
                        .getRol().trim().toUpperCase();

        empleadoIdActual =
                SesionUsuario.getUsuarioActual().getEmpleadoId();

        if (!rolActual.equals("ADMIN")
                && !rolActual.equals("EMPLEADO")) {

            throw new IllegalStateException(
                    "No tienes permiso para acceder a las tareas.");
        }

        if (rolActual.equals("EMPLEADO")
                && empleadoIdActual == null) {

            throw new IllegalStateException(
                    "La cuenta del profesor no está vinculada a un empleado.");
        }
    }

    // REGISTRAR LOG

    private void registrarLog(
            String operacion,
            String resultado,
            String error) {

        LogServicio logServicio = new LogServicio();

        try {
            logServicio.registrar(
                    operacion,
                    "tareas",
                    resultado,
                    error);
        } finally {
            logServicio.cerrar();
        }
    }

    // MOSTRAR VENTANA

    public void mostrar() {

        Stage ventana = new Stage();
        ventana.setTitle("Academy - Gestión de tareas");

        // COLUMNAS DE LA TABLA

        TableColumn<Tarea, String> colTitulo =
                new TableColumn<>("Título");

        colTitulo.setCellValueFactory(
                new PropertyValueFactory<>("titulo"));

        TableColumn<Tarea, String> colProfesor =
                new TableColumn<>("Profesor ID");

        colProfesor.setCellValueFactory(
                new PropertyValueFactory<>("profesorId"));

        TableColumn<Tarea, String> colCurso =
                new TableColumn<>("Curso ID");

        colCurso.setCellValueFactory(
                new PropertyValueFactory<>("cursoId"));

        TableColumn<Tarea, String> colFecha =
                new TableColumn<>("Fecha límite");

        colFecha.setCellValueFactory(
                new PropertyValueFactory<>("fechaLimite"));

        tabla.getColumns().clear();

        tabla.getColumns().addAll(
                colTitulo,
                colProfesor,
                colCurso,
                colFecha);

        tabla.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);

        // FORMULARIO

        TextField titulo = new TextField();
        titulo.setPromptText("Título de la tarea");

        ComboBox<Curso> curso = new ComboBox<>();

        curso.setPromptText("Selecciona uno de tus cursos");

        curso.setConverter(new javafx.util.StringConverter<Curso>() {

            @Override
            public String toString(Curso c) {
                return c == null ? "" : c.getNombre();
            }

            @Override
            public Curso fromString(String texto) {
                return null;
            }
        });

        DatePicker fecha = new DatePicker();

        // CARGAR CURSOS DEL PROFESOR

        if (rolActual.equals("EMPLEADO")) {
            cargarCursosAsignados(curso);
        }

        // BOTONES

        Button guardar = new Button("Crear tarea");

        Button guardarCambios =
                new Button("Guardar cambios");

        Button editar =
                new Button("Editar seleccionada");

        Button eliminar =
                new Button("Eliminar tarea");

        Button actualizar =
                new Button("Actualizar lista");

        Button exportar =
                new Button("Exportar a Excel");

        boolean esProfesor =
                rolActual.equals("EMPLEADO");

        // FORMULARIO VISUAL

        GridPane formulario = new GridPane();

        formulario.setPadding(new Insets(10));
        formulario.setHgap(10);
        formulario.setVgap(10);

        formulario.add(new Label("Título:"), 0, 0);
        formulario.add(titulo, 1, 0);

        formulario.add(new Label("Curso:"), 0, 1);
        formulario.add(curso, 1, 1);

        formulario.add(new Label("Fecha límite:"), 0, 2);
        formulario.add(fecha, 1, 2);

        formulario.setVisible(esProfesor);
        formulario.setManaged(esProfesor);

        guardar.setVisible(esProfesor);
        guardar.setManaged(esProfesor);

        guardarCambios.setVisible(esProfesor);
        guardarCambios.setManaged(esProfesor);

        editar.setVisible(esProfesor);
        editar.setManaged(esProfesor);

        eliminar.setVisible(esProfesor);
        eliminar.setManaged(esProfesor);

        // CREAR TAREA

        guardar.setOnAction(e -> {

            try {

                if (titulo.getText().trim().isEmpty()
                        || curso.getValue() == null
                        || fecha.getValue() == null) {

                    mostrarAlerta(
                            Alert.AlertType.WARNING,
                            "Datos incompletos",
                            "Completa el título, el curso y la fecha.");

                    return;
                }

                Tarea tarea = new Tarea(
                        null,
                        titulo.getText().trim(),
                        empleadoIdActual,
                        curso.getValue().getId(),
                        fecha.getValue().toString());

                tareaServicio.registrar(tarea);

                registrarLog("INSERT", "OK", null);

                cargarTareas();

                limpiarFormulario(titulo, fecha, curso);

                tareaEnEdicion = null;

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Tarea creada",
                        "La tarea se ha creado correctamente.");

            } catch (Exception ex) {

                registrarLog(
                        "INSERT",
                        "ERROR",
                        ex.getMessage());

                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Error al crear tarea",
                        ex.getMessage());
            }
        });

        // ACTUALIZAR LISTA

        actualizar.setOnAction(e -> cargarTareas());

        // EDITAR TAREA

        editar.setOnAction(e -> {

            Tarea seleccionada =
                    tabla.getSelectionModel().getSelectedItem();

            if (seleccionada == null) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Selecciona una tarea",
                        "Selecciona una fila de la tabla.");

                return;
            }

            if (!esTareaDelProfesor(seleccionada)) {

                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Sin permiso",
                        "Solo puedes editar tus propias tareas.");

                return;
            }

            tareaEnEdicion = seleccionada;

            titulo.setText(seleccionada.getTitulo());

            fecha.setValue(
                    LocalDate.parse(
                            seleccionada.getFechaLimite()));

            curso.getItems().stream()
                    .filter(c -> c.getId().equals(
                            seleccionada.getCursoId()))
                    .findFirst()
                    .ifPresent(curso::setValue);

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Editar tarea",
                    "Modifica los datos y pulsa Guardar cambios.");
        });
        

        // GUARDAR CAMBIOS

        guardarCambios.setOnAction(e -> {

            if (tareaEnEdicion == null) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Sin selección",
                        "Primero selecciona una tarea para editar.");

                return;
            }

            try {

                if (titulo.getText().trim().isEmpty()
                        || curso.getValue() == null
                        || fecha.getValue() == null) {

                    mostrarAlerta(
                            Alert.AlertType.WARNING,
                            "Datos incompletos",
                            "Completa todos los campos.");

                    return;
                }

                if (!esTareaDelProfesor(tareaEnEdicion)) {

                    mostrarAlerta(
                            Alert.AlertType.ERROR,
                            "Sin permiso",
                            "No puedes modificar esta tarea.");

                    return;
                }

                tareaEnEdicion.setTitulo(
                        titulo.getText().trim());

                tareaEnEdicion.setCursoId(
                        curso.getValue().getId());

                tareaEnEdicion.setFechaLimite(
                        fecha.getValue().toString());

                tareaServicio.actualizar(tareaEnEdicion);

                registrarLog("UPDATE", "OK", null);

                cargarTareas();

                tareaEnEdicion = null;

                limpiarFormulario(titulo, fecha, curso);

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Actualización correcta",
                        "La tarea se ha actualizado.");

            } catch (Exception ex) {

                registrarLog(
                        "UPDATE",
                        "ERROR",
                        ex.getMessage());

                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Error al actualizar",
                        ex.getMessage());
            }
        });

        // ELIMINAR TAREA

        eliminar.setOnAction(e -> {

            Tarea seleccionada =
                    tabla.getSelectionModel().getSelectedItem();

            if (seleccionada == null) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Selecciona una tarea",
                        "Selecciona la tarea que quieres eliminar.");

                return;
            }

            if (!esTareaDelProfesor(seleccionada)) {

                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Sin permiso",
                        "No puedes eliminar esta tarea.");

                return;
            }

            Alert confirmacion =
                    new Alert(Alert.AlertType.CONFIRMATION);

            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText("¿Eliminar esta tarea?");
            confirmacion.setContentText(
                    seleccionada.getTitulo());

            if (confirmacion.showAndWait()
                    .orElse(ButtonType.CANCEL) == ButtonType.OK) {

                try {

                    tareaServicio.eliminar(
                            seleccionada.getId());

                    registrarLog("DELETE", "OK", null);

                    cargarTareas();

                    tareaEnEdicion = null;

                    limpiarFormulario(titulo, fecha, curso);

                    mostrarAlerta(
                            Alert.AlertType.INFORMATION,
                            "Tarea eliminada",
                            "La tarea se ha eliminado correctamente.");

                } catch (Exception ex) {

                    registrarLog(
                            "DELETE",
                            "ERROR",
                            ex.getMessage());

                    mostrarAlerta(
                            Alert.AlertType.ERROR,
                            "Error",
                            ex.getMessage());
                }
            }
        });

        // EXPORTAR A EXCEL CON SELECCIÓN DE COLUMNAS

        exportar.setOnAction(e -> {

            try {

                CheckBox id = new CheckBox("ID");
                CheckBox tituloCol = new CheckBox("Título");
                CheckBox profesor = new CheckBox("Profesor ID");
                CheckBox cursoCol = new CheckBox("Curso ID");
                CheckBox fechaCol = new CheckBox("Fecha límite");

                // Columnas seleccionadas por defecto

                tituloCol.setSelected(true);
                profesor.setSelected(true);
                cursoCol.setSelected(true);
                fechaCol.setSelected(true);

                VBox contenido = new VBox(10);

                contenido.setPadding(new Insets(10));

                contenido.getChildren().addAll(
                        new Label("Selecciona las columnas que quieres exportar:"),
                        id,
                        tituloCol,
                        profesor,
                        cursoCol,
                        fechaCol
                );

                Alert seleccion =
                        new Alert(Alert.AlertType.CONFIRMATION);

                seleccion.setTitle("Exportar tareas");
                seleccion.setHeaderText("Columnas del Excel");
                seleccion.getDialogPane().setContent(contenido);

                if (seleccion.showAndWait()
                        .orElse(ButtonType.CANCEL) != ButtonType.OK) {

                    return;
                }

                List<String> columnas = new ArrayList<>();

                if (id.isSelected()) {
                    columnas.add("ID");
                }

                if (tituloCol.isSelected()) {
                    columnas.add("Título");
                }

                if (profesor.isSelected()) {
                    columnas.add("Profesor ID");
                }

                if (cursoCol.isSelected()) {
                    columnas.add("Curso ID");
                }

                if (fechaCol.isSelected()) {
                    columnas.add("Fecha límite");
                }

                if (columnas.isEmpty()) {

                    mostrarAlerta(
                            Alert.AlertType.WARNING,
                            "Sin columnas",
                            "Selecciona al menos una columna.");

                    return;
                }

                // Seleccionar dónde guardar el archivo

                FileChooser selector = new FileChooser();

                selector.setTitle("Guardar tareas");

                selector.setInitialFileName("tareas.xlsx");

                selector.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter(
                                "Archivo Excel",
                                "*.xlsx"));

                File archivo =
                        selector.showSaveDialog(ventana);

                if (archivo != null) {

                    ExportarExcel exportador =
                            new ExportarExcel();

                    exportador.exportarTareas(
                            obtenerTareasVisibles(),
                            columnas,
                            archivo.getAbsolutePath());

                    mostrarAlerta(
                            Alert.AlertType.INFORMATION,
                            "Exportación correcta",
                            "El archivo se ha guardado correctamente.");
                }

            } catch (Exception ex) {

                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Error al exportar",
                        ex.getMessage());
            }
        });

        // BOTONES

        HBox botones = new HBox(10);

        botones.setPadding(new Insets(10));

        botones.getChildren().addAll(
                guardar,
                guardarCambios,
                editar,
                eliminar,
                actualizar,
                exportar);

        // VENTANA PRINCIPAL

        VBox raiz = new VBox(10);

        raiz.setPadding(new Insets(15));

        raiz.getChildren().addAll(
                new Label("GESTIÓN DE TAREAS"),
                formulario,
                botones,
                tabla);

        cargarTareas();

        Scene escena = new Scene(raiz, 1100, 650);

        ventana.setScene(escena);

        ventana.setOnCloseRequest(e -> cerrarServicios());

        ventana.show();
    }
    
    // CARGAR CURSOS ASIGNADOS AL PROFESOR

    private void cargarCursosAsignados(ComboBox<Curso> combo) {

        try {

            combo.getItems().clear();

            List<Document> asignaciones =
                    asignacionDAO.listarPorEmpleado(
                            empleadoIdActual);

            for (Document asignacion : asignaciones) {

                ObjectId cursoId =
                        asignacion.getObjectId("cursoId");

                if (cursoId != null) {

                    Curso curso =
                            cursoServicio.buscarPorId(cursoId);

                    if (curso != null && curso.isActivo()) {
                        combo.getItems().add(curso);
                    }
                }
            }

        } catch (Exception ex) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error al cargar cursos",
                    ex.getMessage());
        }
    }

    // CARGAR TAREAS SEGÚN EL ROL

    private void cargarTareas() {

        try {

            tabla.setItems(
                    FXCollections.observableArrayList(
                            obtenerTareasVisibles()));

        } catch (Exception ex) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error al cargar tareas",
                    ex.getMessage());
        }
    }

    // OBTENER TAREAS VISIBLES

    private List<Tarea> obtenerTareasVisibles() {

        if (rolActual.equals("ADMIN")) {
            return tareaServicio.listar();
        }

        if (rolActual.equals("EMPLEADO")) {
            return tareaServicio.listarPorProfesor(
                    empleadoIdActual);
        }

        return new ArrayList<>();
    }

    // COMPROBAR PROPIEDAD DE LA TAREA

    private boolean esTareaDelProfesor(Tarea tarea) {

        if (tarea == null) {
            return false;
        }

        return rolActual.equals("EMPLEADO")
                && empleadoIdActual != null
                && empleadoIdActual.equals(
                        tarea.getProfesorId());
    }

    // LIMPIAR FORMULARIO

    private void limpiarFormulario(
            TextField titulo,
            DatePicker fecha,
            ComboBox<Curso> curso) {

        titulo.clear();
        fecha.setValue(null);
        curso.setValue(null);
    }

    // MOSTRAR ALERTAS

    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alerta = new Alert(tipo);

        alerta.setTitle(titulo);
        alerta.setHeaderText(null);

        alerta.setContentText(
                mensaje == null
                        ? "Ha ocurrido un error."
                        : mensaje);

        alerta.showAndWait();
    }

    // CERRAR SERVICIOS

    private void cerrarServicios() {

        try {
            tareaServicio.cerrar();
        } catch (Exception ex) {
            System.out.println(
                    "Error cerrando TareaServicio: "
                    + ex.getMessage());
        }

        try {
            cursoServicio.cerrar();
        } catch (Exception ex) {
            System.out.println(
                    "Error cerrando CursoServicio: "
                    + ex.getMessage());
        }

        try {
            asignacionDAO.cerrar();
        } catch (Exception ex) {
            System.out.println(
                    "Error cerrando AsignacionDAO: "
                    + ex.getMessage());
        }
    }
}