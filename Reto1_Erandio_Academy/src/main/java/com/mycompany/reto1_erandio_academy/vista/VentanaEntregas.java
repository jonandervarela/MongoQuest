package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.modelo.Entrega;
import com.mycompany.reto1_erandio_academy.modelo.Matricula;
import com.mycompany.reto1_erandio_academy.modelo.Tarea;
import com.mycompany.reto1_erandio_academy.modelo.Usuario;

import com.mycompany.reto1_erandio_academy.dao.EntregaDAO;
import com.mycompany.reto1_erandio_academy.dao.MatriculaDAO;
import com.mycompany.reto1_erandio_academy.dao.TareaDAO;

import com.mycompany.reto1_erandio_academy.servicio.EntregaServicio;
import com.mycompany.reto1_erandio_academy.servicio.SesionUsuario;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.bson.types.ObjectId;

public class VentanaEntregas extends Stage {

    private final TareaDAO tareaDAO;
    private final MatriculaDAO matriculaDAO;
    private final EntregaDAO entregaDAO;
    private final EntregaServicio entregaServicio;

    private final Usuario usuario;

    private TableView<Tarea> tablaTareas;
    private TableView<Entrega> tablaEntregas;

    private ObservableList<Tarea> listaTareas;
    private ObservableList<Entrega> listaEntregas;

    private Label lblInformacion;

    public VentanaEntregas() {

        usuario = SesionUsuario.getUsuarioActual();

        tareaDAO = new TareaDAO();
        matriculaDAO = new MatriculaDAO();
        entregaDAO = new EntregaDAO();
        entregaServicio = new EntregaServicio();

        setTitle("Gestión de entregas");
        setWidth(950);
        setHeight(650);

        if (usuario == null) {

            mostrarAlerta("No hay ningún usuario conectado.");

            cerrarConexiones();
            close();
            return;
        }

        crearVentana();

        setOnCloseRequest(event -> cerrarConexiones());
    }

    // CREAR VENTANA
    private void crearVentana() {

        Label titulo = new Label("GESTIÓN DE ENTREGAS");
        titulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label lblRol = new Label("Usuario: " + usuario.getRol());

        lblInformacion = new Label("Selecciona una tarea.");
        lblInformacion.setStyle("-fx-font-size: 13px;");

        crearTablaTareas();
        crearTablaEntregas();

        VBox contenido = new VBox(15);
        contenido.setPadding(new Insets(20));

        contenido.getChildren().addAll(
                titulo,
                lblRol,
                new Separator(),
                new Label("Tareas"),
                tablaTareas,
                new Label("Entregas"),
                tablaEntregas,
                lblInformacion
        );

        VBox.setVgrow(tablaTareas, Priority.ALWAYS);
        VBox.setVgrow(tablaEntregas, Priority.ALWAYS);

        if (SesionUsuario.tieneRol("USUARIO")) {

            crearVistaAlumno(contenido);

        } else if (SesionUsuario.tieneRol("EMPLEADO")) {

            crearVistaProfesor(contenido);

        } else if (SesionUsuario.tieneRol("ADMIN")) {

            crearVistaAdmin(contenido);

        } else {

            mostrarAlerta("No tienes permisos para acceder.");
            close();
            return;
        }

        Scene escena = new Scene(contenido);

        setScene(escena);
    }

    // TABLA DE TAREAS
    private void crearTablaTareas() {

        tablaTareas = new TableView<>();

        TableColumn<Tarea, String> colTitulo =
                new TableColumn<>("Título");

        colTitulo.setCellValueFactory(
                new PropertyValueFactory<>("titulo"));

        TableColumn<Tarea, String> colFecha =
                new TableColumn<>("Fecha límite");

        colFecha.setCellValueFactory(
                new PropertyValueFactory<>("fechaLimite"));

        tablaTareas.getColumns().addAll(colTitulo, colFecha);

        listaTareas = FXCollections.observableArrayList();

        tablaTareas.setItems(listaTareas);

        tablaTareas.setPrefHeight(200);
    }

    // TABLA DE ENTREGAS
    private void crearTablaEntregas() {

        tablaEntregas = new TableView<>();

        TableColumn<Entrega, String> colEstado =
                new TableColumn<>("Estado");

        colEstado.setCellValueFactory(
                new PropertyValueFactory<>("estado"));

        TableColumn<Entrega, String> colFecha =
                new TableColumn<>("Fecha entrega");

        colFecha.setCellValueFactory(
                new PropertyValueFactory<>("fechaEntrega"));

        TableColumn<Entrega, String> colArchivo =
                new TableColumn<>("Archivo");

        colArchivo.setCellValueFactory(
                new PropertyValueFactory<>("nombreArchivo"));

        tablaEntregas.getColumns().addAll(
                colEstado, colFecha, colArchivo);

        listaEntregas = FXCollections.observableArrayList();

        tablaEntregas.setItems(listaEntregas);

        tablaEntregas.setPrefHeight(200);
    }

    // VISTA DEL ALUMNO
    private void crearVistaAlumno(VBox contenido) {

        cargarTareasAlumno();

        Button btnEntregar = new Button("Entregar archivo");

        btnEntregar.setOnAction(event -> entregarArchivo());

        tablaTareas.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, seleccionada) -> {

                    if (seleccionada != null) {
                        mostrarEntregaAlumno(seleccionada);
                    }
                });

        HBox botones = new HBox(btnEntregar);
        botones.setAlignment(Pos.CENTER_RIGHT);

        contenido.getChildren().add(botones);
    }

    // CARGAR TAREAS DEL ALUMNO
    private void cargarTareasAlumno() {

        listaTareas.clear();

        List<Matricula> matriculas =
                matriculaDAO.listarPorUsuario(usuario.getId());

        for (Matricula matricula : matriculas) {

            if (!"ACTIVA".equalsIgnoreCase(matricula.getEstado())) {
                continue;
            }

            List<Tarea> tareas =
                    tareaDAO.listarPorCurso(matricula.getCursoId());

            for (Tarea tarea : tareas) {

                if (!contieneTarea(tarea.getId())) {
                    listaTareas.add(tarea);
                }
            }
        }
    }

    // COMPROBAR SI LA TAREA YA ESTÁ EN LA TABLA
    private boolean contieneTarea(ObjectId tareaId) {

        for (Tarea tarea : listaTareas) {

            if (tarea.getId().equals(tareaId)) {
                return true;
            }
        }

        return false;
    }

    // MOSTRAR ENTREGA DEL ALUMNO
    private void mostrarEntregaAlumno(Tarea tarea) {

        listaEntregas.clear();

        Entrega entrega = entregaDAO.buscarPorTareaYUsuario(
                tarea.getId(), usuario.getId());

        if (entrega != null) {

            listaEntregas.add(entrega);

            lblInformacion.setText(
                    "Estado de tu entrega: " + entrega.getEstado());

        } else {

            lblInformacion.setText(
                    "Todavía no existe una entrega para esta tarea.");
        }
    }

    // ENTREGAR ARCHIVO
    private void entregarArchivo() {

        Tarea tarea = tablaTareas.getSelectionModel()
                .getSelectedItem();

        if (tarea == null) {

            mostrarAlerta("Selecciona primero una tarea.");
            return;
        }

        FileChooser selector = new FileChooser();

        selector.setTitle("Seleccionar trabajo");

        selector.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(
                        "Archivos PDF y Word",
                        "*.pdf", "*.doc", "*.docx")
        );

        File archivo = selector.showOpenDialog(this);

        if (archivo == null) {
            return;
        }

        try {

            // Crear entrega si todavía no existe
            entregaServicio.crearEntrega(
                    tarea.getId(), usuario.getId());

            // Registrar archivo
            entregaServicio.entregarArchivo(
                    tarea.getId(),
                    usuario.getId(),
                    archivo.getName(),
                    archivo.getAbsolutePath()
            );

            mostrarInformacion("Archivo entregado correctamente.");

            mostrarEntregaAlumno(tarea);

        } catch (Exception e) {

            mostrarAlerta("Error al entregar el archivo: "
                    + e.getMessage());
        }
    }

    // VISTA DEL PROFESOR
    private void crearVistaProfesor(VBox contenido) {

        listaTareas.setAll(
                tareaDAO.listarPorProfesor(usuario.getEmpleadoId()));

        tablaTareas.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, seleccionada) -> {

                    if (seleccionada != null) {
                        cargarEntregasTarea(seleccionada);
                    }
                });

        Button btnAbrir = new Button("Abrir archivo");
        Button btnAprobar = new Button("Aprobar");
        Button btnCorregir = new Button("Solicitar corrección");

        btnAbrir.setOnAction(event -> abrirArchivo());

        btnAprobar.setOnAction(event ->
                cambiarEstadoSeleccionado("APROBADA"));

        btnCorregir.setOnAction(event ->
                cambiarEstadoSeleccionado("CORRECCION_SOLICITADA"));

        HBox botones = new HBox(10,
                btnAbrir, btnAprobar, btnCorregir);

        botones.setAlignment(Pos.CENTER_RIGHT);

        contenido.getChildren().add(botones);
    }

    // VISTA DEL ADMINISTRADOR
    private void crearVistaAdmin(VBox contenido) {

        listaTareas.setAll(tareaDAO.listar());

        tablaTareas.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, seleccionada) -> {

                    if (seleccionada != null) {
                        cargarEntregasTarea(seleccionada);
                    }
                });
    }

    // CARGAR ENTREGAS DE UNA TAREA
    private void cargarEntregasTarea(Tarea tarea) {

        listaEntregas.clear();

        listaEntregas.addAll(
                entregaDAO.listarPorTarea(tarea.getId()));

        lblInformacion.setText(
                "Entregas encontradas: " + listaEntregas.size());
    }

    // ABRIR ARCHIVO DEL ALUMNO
    private void abrirArchivo() {

        Entrega entrega = tablaEntregas.getSelectionModel()
                .getSelectedItem();

        if (entrega == null) {

            mostrarAlerta("Selecciona una entrega.");
            return;
        }

        if (entrega.getRutaArchivo() == null) {

            mostrarAlerta("El alumno todavía no ha entregado ningún archivo.");
            return;
        }

        try {

            File archivo = new File(entrega.getRutaArchivo());

            if (!archivo.exists()) {

                mostrarAlerta("No se encuentra el archivo.");
                return;
            }

            Desktop.getDesktop().open(archivo);

        } catch (IOException e) {

            mostrarAlerta("No se pudo abrir el archivo.");
        }
    }

    // CAMBIAR ESTADO DE UNA ENTREGA
    private void cambiarEstadoSeleccionado(String estado) {

        Entrega entrega = tablaEntregas.getSelectionModel()
                .getSelectedItem();

        if (entrega == null) {

            mostrarAlerta("Selecciona una entrega.");
            return;
        }

        if (entrega.getNombreArchivo() == null) {

            mostrarAlerta("El alumno todavía no ha entregado el trabajo.");
            return;
        }

        try {

            entregaServicio.cambiarEstado(
                    entrega.getId(), estado);

            mostrarInformacion("Estado actualizado correctamente.");

            Tarea tarea = tablaTareas.getSelectionModel()
                    .getSelectedItem();

            if (tarea != null) {
                cargarEntregasTarea(tarea);
            }

        } catch (Exception e) {

            mostrarAlerta("Error: " + e.getMessage());
        }
    }

    // MOSTRAR MENSAJE DE INFORMACIÓN
    private void mostrarInformacion(String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);

        alerta.setTitle("Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }

    // MOSTRAR ERROR
    private void mostrarAlerta(String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.WARNING);

        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }

    // CERRAR CONEXIONES
    private void cerrarConexiones() {

        tareaDAO.cerrar();
        matriculaDAO.cerrar();
        entregaDAO.cerrar();
        entregaServicio.cerrar();
    }
}