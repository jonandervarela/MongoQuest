package com.mycompany.reto1_erandio_academy.vista;
import com.mycompany.reto1_erandio_academy.controlador.Gestor;

import com.mycompany.reto1_erandio_academy.controlador.DAOImpl;

import com.mycompany.reto1_erandio_academy.controlador.SesionUsuario;
import com.mycompany.reto1_erandio_academy.controlador.ExportarExcel;
import com.mycompany.reto1_erandio_academy.modelo.Curso;
import com.mycompany.reto1_erandio_academy.modelo.Tarea;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.bson.Document;
import org.bson.types.ObjectId;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class TareasController implements Initializable {

    private Gestor tareaServicio;
    private Gestor cursoServicio;
    private DAOImpl asignacionDAO;

    private Tarea tareaEnEdicion = null;

    private String rolActual = "";
    private ObjectId empleadoIdActual;

    @FXML private VBox raiz;
    @FXML private Label tituloVentana;
    @FXML private GridPane formulario;
    @FXML private TextField titulo;
    @FXML private ComboBox<Curso> curso;
    @FXML private DatePicker fecha;
    @FXML private Button guardar;
    @FXML private Button guardarCambios;
    @FXML private Button editar;
    @FXML private Button eliminar;
    @FXML private Button actualizar;
    @FXML private Button exportar;
    @FXML private TableView<Tarea> tabla;
    @FXML private TableColumn<Tarea, String> colTitulo;
    @FXML private TableColumn<Tarea, String> colProfesor;
    @FXML private TableColumn<Tarea, String> colCurso;
    @FXML private TableColumn<Tarea, String> colFecha;

    private void registrarLog(String operacion, String resultado, String error) {
        Gestor logServicio = new Gestor();
        try {
            logServicio.registrarLog(operacion, "tareas", resultado, error);
        } finally {
            logServicio.cerrar();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colProfesor.setCellValueFactory(new PropertyValueFactory<>("profesorId"));
        colCurso.setCellValueFactory(new PropertyValueFactory<>("cursoId"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaLimite"));
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        if (!SesionUsuario.haySesion()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Sin sesión", "No hay ningún usuario conectado.");
            deshabilitarTodo();
            return;
        }

        rolActual = SesionUsuario.getUsuarioActual().getRol() == null
                ? ""
                : SesionUsuario.getUsuarioActual().getRol().trim().toUpperCase();

        empleadoIdActual = SesionUsuario.getUsuarioActual().getEmpleadoId();

        if (!rolActual.equals("ADMIN") && !rolActual.equals("EMPLEADO")) {
            mostrarAlerta(Alert.AlertType.ERROR, "Sin permiso", "No tienes permiso para acceder a las tareas.");
            deshabilitarTodo();
            return;
        }

        if (rolActual.equals("EMPLEADO") && empleadoIdActual == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Cuenta sin vincular",
                    "La cuenta del profesor no está vinculada a un empleado.");
            deshabilitarTodo();
            return;
        }

        tareaServicio = new Gestor();
        cursoServicio = new Gestor();
        asignacionDAO = new DAOImpl();

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

        boolean esProfesor = rolActual.equals("EMPLEADO");

        if (esProfesor) {
            cargarCursosAsignados();
        }

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

        cargarTareas();

        raiz.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((o2, oldW, newW) -> {
                    if (newW != null) {
                        newW.addEventHandler(WindowEvent.WINDOW_HIDDEN, e -> cerrarServicios());
                    }
                });
            }
        });
    }

    private void deshabilitarTodo() {
        formulario.setDisable(true);
        guardar.setDisable(true);
        guardarCambios.setDisable(true);
        editar.setDisable(true);
        eliminar.setDisable(true);
        actualizar.setDisable(true);
        exportar.setDisable(true);
    }

    @FXML
    private void onGuardar() {

        try {
            if (titulo.getText().trim().isEmpty() || curso.getValue() == null || fecha.getValue() == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Datos incompletos", "Completa el título, el curso y la fecha.");
                return;
            }

            Tarea tarea = new Tarea(
                    null,
                    titulo.getText().trim(),
                    empleadoIdActual,
                    curso.getValue().getId(),
                    fecha.getValue().toString());

            tareaServicio.registrarTarea(tarea);
            registrarLog("INSERT", "OK", null);
            cargarTareas();
            limpiarFormulario();
            tareaEnEdicion = null;
            mostrarAlerta(Alert.AlertType.INFORMATION, "Tarea creada", "La tarea se ha creado correctamente.");

        } catch (Exception ex) {
            registrarLog("INSERT", "ERROR", ex.getMessage());
            mostrarAlerta(Alert.AlertType.ERROR, "Error al crear tarea", ex.getMessage());
        }
    }

    @FXML
    private void onActualizar() {
        cargarTareas();
    }

    @FXML
    private void onEditar() {

        Tarea seleccionada = tabla.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona una tarea", "Selecciona una fila de la tabla.");
            return;
        }

        if (!esTareaDelProfesor(seleccionada)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Sin permiso", "Solo puedes editar tus propias tareas.");
            return;
        }

        tareaEnEdicion = seleccionada;
        titulo.setText(seleccionada.getTitulo());
        fecha.setValue(LocalDate.parse(seleccionada.getFechaLimite()));

        curso.getItems().stream()
                .filter(c -> c.getId().equals(seleccionada.getCursoId()))
                .findFirst()
                .ifPresent(curso::setValue);

        mostrarAlerta(Alert.AlertType.INFORMATION, "Editar tarea", "Modifica los datos y pulsa Guardar cambios.");
    }

    @FXML
    private void onGuardarCambios() {

        if (tareaEnEdicion == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin selección", "Primero selecciona una tarea para editar.");
            return;
        }

        try {
            if (titulo.getText().trim().isEmpty() || curso.getValue() == null || fecha.getValue() == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Datos incompletos", "Completa todos los campos.");
                return;
            }

            if (!esTareaDelProfesor(tareaEnEdicion)) {
                mostrarAlerta(Alert.AlertType.ERROR, "Sin permiso", "No puedes modificar esta tarea.");
                return;
            }

            tareaEnEdicion.setTitulo(titulo.getText().trim());
            tareaEnEdicion.setCursoId(curso.getValue().getId());
            tareaEnEdicion.setFechaLimite(fecha.getValue().toString());

            tareaServicio.actualizarTarea(tareaEnEdicion);
            registrarLog("UPDATE", "OK", null);
            cargarTareas();
            tareaEnEdicion = null;
            limpiarFormulario();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Actualización correcta", "La tarea se ha actualizado.");

        } catch (Exception ex) {
            registrarLog("UPDATE", "ERROR", ex.getMessage());
            mostrarAlerta(Alert.AlertType.ERROR, "Error al actualizar", ex.getMessage());
        }
    }

    @FXML
    private void onEliminar() {

        Tarea seleccionada = tabla.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona una tarea", "Selecciona la tarea que quieres eliminar.");
            return;
        }

        if (!esTareaDelProfesor(seleccionada)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Sin permiso", "No puedes eliminar esta tarea.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar esta tarea?");
        confirmacion.setContentText(seleccionada.getTitulo());

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {

            try {
                tareaServicio.eliminarTarea(seleccionada.getId());
                registrarLog("DELETE", "OK", null);
                cargarTareas();
                tareaEnEdicion = null;
                limpiarFormulario();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Tarea eliminada", "La tarea se ha eliminado correctamente.");

            } catch (Exception ex) {
                registrarLog("DELETE", "ERROR", ex.getMessage());
                mostrarAlerta(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        }
    }

    @FXML
    private void onExportar() {

        try {
            CheckBox id = new CheckBox("ID");
            CheckBox tituloCol = new CheckBox("Título");
            CheckBox profesor = new CheckBox("Profesor ID");
            CheckBox cursoCol = new CheckBox("Curso ID");
            CheckBox fechaCol = new CheckBox("Fecha límite");

            tituloCol.setSelected(true);
            profesor.setSelected(true);
            cursoCol.setSelected(true);
            fechaCol.setSelected(true);

            VBox contenido = new VBox(10);
            contenido.setPadding(new Insets(10));
            contenido.getChildren().addAll(
                    new Label("Selecciona las columnas que quieres exportar:"),
                    id, tituloCol, profesor, cursoCol, fechaCol);

            Alert seleccion = new Alert(Alert.AlertType.CONFIRMATION);
            seleccion.setTitle("Exportar tareas");
            seleccion.setHeaderText("Columnas del Excel");
            seleccion.getDialogPane().setContent(contenido);

            if (seleccion.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                return;
            }

            List<String> columnas = new ArrayList<>();
            if (id.isSelected()) columnas.add("ID");
            if (tituloCol.isSelected()) columnas.add("Título");
            if (profesor.isSelected()) columnas.add("Profesor ID");
            if (cursoCol.isSelected()) columnas.add("Curso ID");
            if (fechaCol.isSelected()) columnas.add("Fecha límite");

            if (columnas.isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Sin columnas", "Selecciona al menos una columna.");
                return;
            }

            FileChooser selector = new FileChooser();
            selector.setTitle("Guardar tareas");
            selector.setInitialFileName("tareas.xlsx");
            selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo Excel", "*.xlsx"));

            File archivo = selector.showSaveDialog((Stage) raiz.getScene().getWindow());

            if (archivo != null) {
                ExportarExcel exportador = new ExportarExcel();
                exportador.exportarTareas(obtenerTareasVisibles(), columnas, archivo.getAbsolutePath());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Exportación correcta", "El archivo se ha guardado correctamente.");
            }

        } catch (Exception ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al exportar", ex.getMessage());
        }
    }

    private void cargarCursosAsignados() {

        try {
            curso.getItems().clear();

            List<Document> asignaciones = asignacionDAO.listarAsignacionesPorEmpleado(empleadoIdActual);

            for (Document asignacion : asignaciones) {
                ObjectId cursoId = asignacion.getObjectId("cursoId");

                if (cursoId != null) {
                    Curso c = cursoServicio.buscarCursoPorId(cursoId);
                    if (c != null && c.isActivo()) {
                        curso.getItems().add(c);
                    }
                }
            }

        } catch (Exception ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al cargar cursos", ex.getMessage());
        }
    }

    private void cargarTareas() {
        try {
            tabla.setItems(FXCollections.observableArrayList(obtenerTareasVisibles()));
        } catch (Exception ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al cargar tareas", ex.getMessage());
        }
    }

    private List<Tarea> obtenerTareasVisibles() {

        if (rolActual.equals("ADMIN")) {
            return tareaServicio.listarTareas();
        }

        if (rolActual.equals("EMPLEADO")) {
            return tareaServicio.listarTareasPorProfesor(empleadoIdActual);
        }

        return new ArrayList<>();
    }

    private boolean esTareaDelProfesor(Tarea tarea) {
        if (tarea == null) {
            return false;
        }
        return rolActual.equals("EMPLEADO")
                && empleadoIdActual != null
                && empleadoIdActual.equals(tarea.getProfesorId());
    }

    private void limpiarFormulario() {
        titulo.clear();
        fecha.setValue(null);
        curso.setValue(null);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje == null ? "Ha ocurrido un error." : mensaje);
        alerta.showAndWait();
    }

    private void cerrarServicios() {

        try {
            if (tareaServicio != null) tareaServicio.cerrar();
        } catch (Exception ex) {
            System.out.println("Error cerrando Gestor: " + ex.getMessage());
        }

        try {
            if (cursoServicio != null) cursoServicio.cerrar();
        } catch (Exception ex) {
            System.out.println("Error cerrando Gestor: " + ex.getMessage());
        }

        try {
            if (asignacionDAO != null) asignacionDAO.cerrar();
        } catch (Exception ex) {
            System.out.println("Error cerrando DAO: " + ex.getMessage());
        }
    }
}
