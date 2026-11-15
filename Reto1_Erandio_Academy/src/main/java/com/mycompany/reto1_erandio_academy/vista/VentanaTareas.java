
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

    private FilteredList<Tarea> tareasFiltradas;
    private TextField buscador;

    private static final String TINTA = "#12283A";
    private static final String AZUL = "#1F6F8B";
    private static final String FONDO = "#F2F5F7";
    private static final String GRIS = "#64748B";

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
                    error
            );
        } finally {
            logServicio.cerrar();
        }
    }

    // MOSTRAR VENTANA

    public void mostrar() {

        Stage ventana = new Stage();
        ventana.setTitle("Erandio Academy - Gestión de tareas");

        boolean esProfesor = rolActual.equals("EMPLEADO");

        // CABECERA

        Label titulo = new Label("Gestión de tareas");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 27));
        titulo.setTextFill(Color.web(TINTA));

        Label descripcion = new Label(
                esProfesor
                        ? "Crea y administra las tareas de tus cursos asignados."
                        : "Consulta y administra las tareas de la academia."
        );

        descripcion.setFont(Font.font("Segoe UI", 14));
        descripcion.setTextFill(Color.web(GRIS));

        Label etiquetaRol = new Label(
                esProfesor ? "PANEL DEL PROFESOR" : "PANEL DE ADMINISTRACIÓN"
        );

        etiquetaRol.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        etiquetaRol.setTextFill(Color.web(AZUL));

        VBox cabecera = new VBox(
                7,
                etiquetaRol,
                titulo,
                descripcion
        );

        cabecera.setPadding(new Insets(28, 30, 18, 30));

        // TABLA

        TableColumn<Tarea, String> colTitulo =
                new TableColumn<>("Título");

        colTitulo.setCellValueFactory(
                new PropertyValueFactory<>("titulo")
        );
        colTitulo.setMinWidth(180);

        TableColumn<Tarea, String> colProfesor =
                new TableColumn<>("Profesor ID");

        colProfesor.setCellValueFactory(
                new PropertyValueFactory<>("profesorId")
        );
        colProfesor.setMinWidth(180);

        TableColumn<Tarea, String> colCurso =
                new TableColumn<>("Curso ID");

        colCurso.setCellValueFactory(
                new PropertyValueFactory<>("cursoId")
        );
        colCurso.setMinWidth(180);

        TableColumn<Tarea, String> colFecha =
                new TableColumn<>("Fecha límite");

        colFecha.setCellValueFactory(
                new PropertyValueFactory<>("fechaLimite")
        );
        colFecha.setMinWidth(140);

        tabla.getColumns().clear();

        tabla.getColumns().addAll(
                colTitulo,
                colProfesor,
                colCurso,
                colFecha
        );

        tabla.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        tabla.setPlaceholder(
                new Label("No hay tareas para mostrar.")
        );

        tabla.setStyle(
                "-fx-background-color: white;"
                + "-fx-border-color: #DCE4EA;"
                + "-fx-border-radius: 8;"
        );

        // BUSCADOR

        buscador = new TextField();
        buscador.setPromptText(
                "Buscar por título, profesor, curso o fecha..."
        );
        buscador.setPrefHeight(40);
        buscador.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 8;"
                + "-fx-border-color: #DCE4EA;"
                + "-fx-border-radius: 8;"
                + "-fx-font-size: 13px;"
        );

        // FORMULARIO

        TextField campoTitulo = new TextField();
        campoTitulo.setPromptText("Escribe el título de la tarea");
        campoTitulo.setPrefWidth(350);

        ComboBox<Curso> curso = new ComboBox<>();
        curso.setPromptText("Selecciona uno de tus cursos");
        curso.setPrefWidth(350);

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
        fecha.setPromptText("Selecciona fecha límite");
        fecha.setPrefWidth(350);

        if (esProfesor) {
            cargarCursosAsignados(curso);
        }

        Label tituloFormulario = new Label("Crear o modificar tarea");
        tituloFormulario.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 16)
        );
        tituloFormulario.setTextFill(Color.web(TINTA));

        GridPane formulario = new GridPane();
        formulario.setHgap(18);
        formulario.setVgap(14);
        formulario.setPadding(new Insets(18));

        formulario.add(new Label("Título de la tarea"), 0, 0);
        formulario.add(campoTitulo, 1, 0);

        formulario.add(new Label("Curso asignado"), 0, 1);
        formulario.add(curso, 1, 1);

        formulario.add(new Label("Fecha límite"), 0, 2);
        formulario.add(fecha, 1, 2);

        formulario.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 10;"
                + "-fx-border-color: #DCE4EA;"
                + "-fx-border-radius: 10;"
        );

        VBox panelFormulario = new VBox(
                10,
                tituloFormulario,
                formulario
        );

        panelFormulario.setPadding(new Insets(15));
        panelFormulario.setStyle(
                "-fx-background-color: #EAF0F4;"
                + "-fx-background-radius: 10;"
        );

        panelFormulario.setVisible(esProfesor);
        panelFormulario.setManaged(esProfesor);

        // BOTONES

        Button guardar = crearBoton(
                "Crear tarea", AZUL
        );

        Button guardarCambios = crearBoton(
                "Guardar cambios", "#3976A8"
        );

        Button editar = crearBoton(
                "Editar seleccionada", "#3976A8"
        );

        Button eliminar = crearBoton(
                "Eliminar tarea", "#B3382C"
        );

        Button actualizar = crearBoton(
                "Actualizar lista", "#64748B"
        );

        Button exportar = crearBoton(
                "Exportar a Excel", "#16834A"
        );

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

                if (campoTitulo.getText().trim().isEmpty()
                        || curso.getValue() == null
                        || fecha.getValue() == null) {

                    mostrarAlerta(
                            Alert.AlertType.WARNING,
                            "Datos incompletos",
                            "Completa el título, el curso y la fecha."
                    );

                    return;
                }

                Tarea tarea = new Tarea(
                        null,
                        campoTitulo.getText().trim(),
                        empleadoIdActual,
                        curso.getValue().getId(),
                        fecha.getValue().toString()
                );

                tareaServicio.registrar(tarea);

                registrarLog("INSERT", "OK", null);

                cargarTareas();

                limpiarFormulario(campoTitulo, fecha, curso);

                tareaEnEdicion = null;

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Tarea creada",
                        "La tarea se ha creado correctamente."
                );

            } catch (Exception ex) {

                registrarLog(
                        "INSERT",
                        "ERROR",
                        ex.getMessage()
                );

                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Error al crear tarea",
                        ex.getMessage()
                );
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
                        "Selecciona una fila de la tabla."
                );

                return;
            }

            if (!esTareaDelProfesor(seleccionada)) {

                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Sin permiso",
                        "Solo puedes editar tus propias tareas."
                );

                return;
            }

            tareaEnEdicion = seleccionada;

            campoTitulo.setText(seleccionada.getTitulo());

            fecha.setValue(
                    LocalDate.parse(seleccionada.getFechaLimite())
            );

            curso.getItems().stream()
                    .filter(c -> c.getId().equals(
                            seleccionada.getCursoId()))
                    .findFirst()
                    .ifPresent(curso::setValue);

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Editar tarea",
                    "Modifica los datos y pulsa Guardar cambios."
            );
        });

        // GUARDAR CAMBIOS

        guardarCambios.setOnAction(e -> {

            if (tareaEnEdicion == null) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Sin selección",
                        "Primero selecciona una tarea para editar."
                );

                return;
            }

            try {

                if (campoTitulo.getText().trim().isEmpty()
                        || curso.getValue() == null
                        || fecha.getValue() == null) {

                    mostrarAlerta(
                            Alert.AlertType.WARNING,
                            "Datos incompletos",
                            "Completa todos los campos."
                    );

                    return;
                }

                if (!esTareaDelProfesor(tareaEnEdicion)) {

                    mostrarAlerta(
                            Alert.AlertType.ERROR,
                            "Sin permiso",
                            "No puedes modificar esta tarea."
                    );

                    return;
                }

                tareaEnEdicion.setTitulo(
                        campoTitulo.getText().trim()
                );

                tareaEnEdicion.setCursoId(
                        curso.getValue().getId()
                );

                tareaEnEdicion.setFechaLimite(
                        fecha.getValue().toString()
                );

                tareaServicio.actualizar(tareaEnEdicion);

                registrarLog("UPDATE", "OK", null);

                cargarTareas();

                tareaEnEdicion = null;

                limpiarFormulario(campoTitulo, fecha, curso);

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Actualización correcta",
                        "La tarea se ha actualizado."
                );

            } catch (Exception ex) {

                registrarLog(
                        "UPDATE",
                        "ERROR",
                        ex.getMessage()
                );

                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Error al actualizar",
                        ex.getMessage()
                );
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
                        "Selecciona la tarea que quieres eliminar."
                );

                return;
            }

            if (!esTareaDelProfesor(seleccionada)) {

                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Sin permiso",
                        "No puedes eliminar esta tarea."
                );

                return;
            }

            Alert confirmacion =
                    new Alert(Alert.AlertType.CONFIRMATION);

            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText("¿Eliminar esta tarea?");
            confirmacion.setContentText(
                    seleccionada.getTitulo()
            );

            if (confirmacion.showAndWait()
                    .orElse(ButtonType.CANCEL) == ButtonType.OK) {

                try {

                    tareaServicio.eliminar(seleccionada.getId());

                    registrarLog("DELETE", "OK", null);

                    cargarTareas();

                    tareaEnEdicion = null;

                    limpiarFormulario(campoTitulo, fecha, curso);

                    mostrarAlerta(
                            Alert.AlertType.INFORMATION,
                            "Tarea eliminada",
                            "La tarea se ha eliminado correctamente."
                    );

                } catch (Exception ex) {

                    registrarLog(
                            "DELETE",
                            "ERROR",
                            ex.getMessage()
                    );

                    mostrarAlerta(
                            Alert.AlertType.ERROR,
                            "Error",
                            ex.getMessage()
                    );
                }
            }
        });

        // EXPORTAR A EXCEL

        exportar.setOnAction(e -> {

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

                VBox contenido = new VBox(
                        12,
                        new Label(
                                "Selecciona las columnas que quieres exportar:"
                        ),
                        id,
                        tituloCol,
                        profesor,
                        cursoCol,
                        fechaCol
                );

                contenido.setPadding(new Insets(18));

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
                            "Selecciona al menos una columna."
                    );

                    return;
                }

                FileChooser selector = new FileChooser();

                selector.setTitle("Guardar tareas");
                selector.setInitialFileName("tareas.xlsx");

                selector.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter(
                                "Archivo Excel",
                                "*.xlsx"
                        )
                );

                File archivo =
                        selector.showSaveDialog(ventana);

                if (archivo != null) {

                    ExportarExcel exportador =
                            new ExportarExcel();

                    exportador.exportarTareas(
                            obtenerTareasVisibles(),
                            columnas,
                            archivo.getAbsolutePath()
                    );

                    mostrarAlerta(
                            Alert.AlertType.INFORMATION,
                            "Exportación correcta",
                            "El archivo se ha guardado correctamente."
                    );
                }

            } catch (Exception ex) {

                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Error al exportar",
                        ex.getMessage()
                );
            }
        });

        // BUSCADOR EN TIEMPO REAL

        buscador.textProperty().addListener(
                (observable, anterior, nuevo) -> {

                    if (tareasFiltradas == null) {
                        return;
                    }

                    String filtro = nuevo == null
                            ? ""
                            : nuevo.trim().toLowerCase();

                    tareasFiltradas.setPredicate(tarea -> {

                        if (filtro.isEmpty()) {
                            return true;
                        }

                        return contiene(tarea.getTitulo(), filtro)
                                || contiene(
                                        tarea.getProfesorId() == null
                                                ? ""
                                                : tarea.getProfesorId().toString(),
                                        filtro
                                )
                                || contiene(
                                        tarea.getCursoId() == null
                                                ? ""
                                                : tarea.getCursoId().toString(),
                                        filtro
                                )
                                || contiene(tarea.getFechaLimite(), filtro);
                    });
                }
        );

        // BARRA DE ACCIONES

        HBox botones = new HBox(
                10,
                guardar,
                guardarCambios,
                editar,
                eliminar,
                actualizar,
                exportar
        );

        botones.setAlignment(Pos.CENTER_LEFT);
        botones.setPadding(new Insets(8, 0, 8, 0));

        botones.setMinHeight(48);

        // PANEL DE TABLA

        Label tituloTabla = new Label("Listado de tareas");
        tituloTabla.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 17)
        );
        tituloTabla.setTextFill(Color.web(TINTA));

        VBox panelTabla = new VBox(
                12,
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

        VBox contenidoPrincipal = new VBox(
                16,
                panelFormulario,
                panelTabla,
                botones
        );

        contenidoPrincipal.setPadding(
                new Insets(0, 30, 25, 30)
        );

        VBox.setVgrow(panelTabla, Priority.ALWAYS);

        VBox raiz = new VBox(
                cabecera,
                contenidoPrincipal
        );

        raiz.setStyle(
                "-fx-background-color: " + FONDO + ";"
        );

        cargarTareas();

        Scene escena = new Scene(raiz, 1150, 760);

        ventana.setScene(escena);
        ventana.setMinWidth(900);
        ventana.setMinHeight(620);

        ventana.setOnCloseRequest(e -> cerrarServicios());

        ventana.centerOnScreen();
        ventana.show();
    }

    // CARGAR CURSOS ASIGNADOS AL PROFESOR

    private void cargarCursosAsignados(ComboBox<Curso> combo) {

        try {

            combo.getItems().clear();

            List<Document> asignaciones =
                    asignacionDAO.listarPorEmpleado(
                            empleadoIdActual
                    );

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
                    ex.getMessage()
            );
        }
    }

    // CARGAR TAREAS SEGÚN EL ROL

    private void cargarTareas() {

        try {

            tareasFiltradas = new FilteredList<>(
                    FXCollections.observableArrayList(
                            obtenerTareasVisibles()
                    ),
                    tarea -> true
            );

            tabla.setItems(tareasFiltradas);

            // Mantener el filtro si ya hay texto escrito
            if (buscador != null
                    && !buscador.getText().isBlank()) {

                String filtro = buscador.getText()
                        .trim().toLowerCase();

                tareasFiltradas.setPredicate(tarea ->
                        contiene(tarea.getTitulo(), filtro)
                        || contiene(
                                tarea.getProfesorId() == null
                                        ? ""
                                        : tarea.getProfesorId().toString(),
                                filtro
                        )
                        || contiene(
                                tarea.getCursoId() == null
                                        ? ""
                                        : tarea.getCursoId().toString(),
                                filtro
                        )
                        || contiene(tarea.getFechaLimite(), filtro)
                );
            }

        } catch (Exception ex) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error al cargar tareas",
                    ex.getMessage()
            );
        }
    }

    // OBTENER TAREAS VISIBLES

    private List<Tarea> obtenerTareasVisibles() {

        if (rolActual.equals("ADMIN")) {
            return tareaServicio.listar();
        }

        if (rolActual.equals("EMPLEADO")) {
            return tareaServicio.listarPorProfesor(
                    empleadoIdActual
            );
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
                        tarea.getProfesorId()
                );
    }

    // COMPROBAR TEXTO

    private boolean contiene(String texto, String filtro) {

        return texto != null
                && texto.toLowerCase().contains(filtro);
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

    // ESTILO DE BOTONES

    private Button crearBoton(String texto, String color) {

        Button boton = new Button(texto);

        boton.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 12)
        );

        boton.setTextFill(Color.WHITE);
        boton.setCursor(Cursor.HAND);
        boton.setPadding(new Insets(11, 16, 11, 16));

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

    // MOSTRAR ALERTAS

    private void mostrarAlerta(
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

    // CERRAR SERVICIOS

    private void cerrarServicios() {

        try {
            tareaServicio.cerrar();
        } catch (Exception ex) {
            System.out.println(
                    "Error cerrando TareaServicio: "
                    + ex.getMessage()
            );
        }

        try {
            cursoServicio.cerrar();
        } catch (Exception ex) {
            System.out.println(
                    "Error cerrando CursoServicio: "
                    + ex.getMessage()
            );
        }

        try {
            asignacionDAO.cerrar();
        } catch (Exception ex) {
            System.out.println(
                    "Error cerrando AsignacionDAO: "
                    + ex.getMessage()
            );
        }
    }
}
