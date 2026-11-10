
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.dao.UsuarioDAO;
import com.mycompany.reto1_erandio_academy.dao.CursoDAO;
import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.modelo.Curso;
import com.mycompany.reto1_erandio_academy.modelo.Matricula;
import com.mycompany.reto1_erandio_academy.servicio.MatriculaServicio;
import com.mycompany.reto1_erandio_academy.servicio.SesionUsuario;
import com.mycompany.reto1_erandio_academy.servicio.LogServicio;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class VentanaMatriculas {

    private UsuarioDAO usuarioDAO;
    private CursoDAO cursoDAO;
    private MatriculaServicio servicio;

    private final TableView<Matricula> tabla = new TableView<>();

    private Usuario usuarioActual;
    private boolean esAdministrador;

    private Map<String, String> nombresUsuarios = new HashMap<>();
    private Map<String, String> nombresCursos = new HashMap<>();

    // REGISTRAR LOG
    private void registrarLog(
            String operacion,
            String resultado,
            String error) {

        LogServicio logServicio = new LogServicio();

        try {
            logServicio.registrar(
                    operacion,
                    "matriculas",
                    resultado,
                    error);
        } finally {
            logServicio.cerrar();
        }
    }

    public void mostrar() {

        usuarioActual = SesionUsuario.getUsuarioActual();

        if (usuarioActual == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "No hay ningún usuario conectado.");
            return;
        }

        esAdministrador = SesionUsuario.tieneRol("ADMIN");

        usuarioDAO = new UsuarioDAO();
        cursoDAO = new CursoDAO();
        servicio = new MatriculaServicio();

        Stage ventana = new Stage();
        ventana.setTitle("Academy - Matrículas");

        VBox contenido = new VBox(12);
        contenido.setPadding(new Insets(20));

        Label titulo = new Label("GESTIÓN DE MATRÍCULAS");

        titulo.setStyle(
                "-fx-font-size: 24px;"
                + "-fx-font-weight: bold;"
        );

        contenido.getChildren().add(titulo);

        // ADMINISTRADOR
        if (esAdministrador) {

            ComboBox<Usuario> usuarios = new ComboBox<>();
            ComboBox<Curso> cursos = new ComboBox<>();

            usuarios.setPromptText("Selecciona un usuario");
            cursos.setPromptText("Selecciona un curso");

            usuarios.setItems(
                    FXCollections.observableArrayList(
                            usuarioDAO.listar()
                    )
            );

            cursos.setItems(
                    FXCollections.observableArrayList(
                            cursoDAO.listar()
                    )
            );

            // MOSTRAR NOMBRE DEL USUARIO
            usuarios.setCellFactory(lista ->
                    new ListCell<Usuario>() {

                @Override
                protected void updateItem(
                        Usuario usuario, boolean vacio) {

                    super.updateItem(usuario, vacio);

                    setText(vacio || usuario == null
                            ? null : usuario.getNombre());
                }
            });

            usuarios.setButtonCell(new ListCell<Usuario>() {

                @Override
                protected void updateItem(
                        Usuario usuario, boolean vacio) {

                    super.updateItem(usuario, vacio);

                    setText(vacio || usuario == null
                            ? null : usuario.getNombre());
                }
            });

            // MOSTRAR NOMBRE DEL CURSO
            cursos.setCellFactory(lista ->
                    new ListCell<Curso>() {

                @Override
                protected void updateItem(
                        Curso curso, boolean vacio) {

                    super.updateItem(curso, vacio);

                    setText(vacio || curso == null
                            ? null : curso.getNombre());
                }
            });

            cursos.setButtonCell(new ListCell<Curso>() {

                @Override
                protected void updateItem(
                        Curso curso, boolean vacio) {

                    super.updateItem(curso, vacio);

                    setText(vacio || curso == null
                            ? null : curso.getNombre());
                }
            });

            Button matricular = new Button("Matricular usuario");

            matricular.setOnAction(e -> {

                Usuario usuario = usuarios.getValue();
                Curso curso = cursos.getValue();

                try {

                    if (usuario == null || curso == null) {

                        mostrarMensaje(
                                Alert.AlertType.WARNING,
                                "Selecciona un usuario y un curso."
                        );

                        return;
                    }

                    servicio.matricular(
                            usuario.getId(),
                            curso.getId()
                    );

                    // REGISTRAR LOG
                    registrarLog("INSERT", "OK", null);

                    cargarMatriculas();

                    mostrarMensaje(
                            Alert.AlertType.INFORMATION,
                            "Usuario matriculado correctamente."
                    );

                } catch (Exception ex) {

                    // REGISTRAR ERROR
                    registrarLog(
                            "INSERT",
                            "ERROR",
                            ex.getMessage()
                    );

                    mostrarMensaje(
                            Alert.AlertType.ERROR,
                            ex.getMessage()
                    );
                }
            });

            contenido.getChildren().addAll(
                    new Label("Seleccionar usuario:"),
                    usuarios,
                    new Label("Seleccionar curso:"),
                    cursos,
                    matricular
            );
        }

        // CARGAR NOMBRES ANTES DE CREAR LAS COLUMNAS
        cargarNombres();

        crearColumnas();

        Button actualizar = new Button("Actualizar lista");

        actualizar.setOnAction(e -> {
            cargarNombres();
            cargarMatriculas();
            tabla.refresh();
        });

        contenido.getChildren().addAll(
                new Label("Matrículas registradas:"),
                tabla,
                actualizar
        );

        // BOTÓN DE BAJA SOLAMENTE PARA ADMIN
        if (esAdministrador) {

            Button eliminar = new Button("Dar de baja");

            eliminar.setOnAction(e -> {

                Matricula seleccionada =
                        tabla.getSelectionModel().getSelectedItem();

                if (seleccionada == null) {

                    mostrarMensaje(
                            Alert.AlertType.WARNING,
                            "Selecciona una matrícula."
                    );

                    return;
                }

                Alert confirmacion =
                        new Alert(Alert.AlertType.CONFIRMATION);

                confirmacion.setTitle("Dar de baja");
                confirmacion.setHeaderText("¿Estás seguro?");
                confirmacion.setContentText(
                        "¿Quieres eliminar esta matrícula?");

                java.util.Optional<ButtonType> resultado =
                        confirmacion.showAndWait();

                if (resultado.isPresent()
                        && resultado.get() == ButtonType.OK) {

                    try {

                        servicio.eliminar(seleccionada.getId());

                        // REGISTRAR LOG
                        registrarLog("DELETE", "OK", null);

                        cargarMatriculas();

                        mostrarMensaje(
                                Alert.AlertType.INFORMATION,
                                "Matrícula eliminada correctamente."
                        );

                    } catch (Exception ex) {

                        // REGISTRAR ERROR
                        registrarLog(
                                "DELETE",
                                "ERROR",
                                ex.getMessage()
                        );

                        mostrarMensaje(
                                Alert.AlertType.ERROR,
                                ex.getMessage()
                        );
                    }
                }
            });

            contenido.getChildren().add(eliminar);
        }

        cargarMatriculas();

        ventana.setScene(
                new Scene(contenido, 850, 600)
        );

        ventana.setOnHidden(e -> {

            usuarioDAO.cerrar();
            cursoDAO.cerrar();
            servicio.cerrar();
        });

        ventana.show();
    }

    // CARGAR NOMBRES DE USUARIOS Y CURSOS
    private void cargarNombres() {

        nombresUsuarios.clear();
        nombresCursos.clear();

        for (Usuario usuario : usuarioDAO.listar()) {

            nombresUsuarios.put(
                    usuario.getId().toString(),
                    usuario.getNombre()
            );
        }

        for (Curso curso : cursoDAO.listar()) {

            nombresCursos.put(
                    curso.getId().toString(),
                    curso.getNombre()
            );
        }
    }

    // CREAR COLUMNAS DE LA TABLA
    private void crearColumnas() {

        tabla.getColumns().clear();

        TableColumn<Matricula, String> usuarioColumna =
                new TableColumn<>("Usuario");

        usuarioColumna.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        nombresUsuarios.getOrDefault(
                                d.getValue().getUsuarioId().toString(),
                                "Usuario desconocido"
                        )
                )
        );

        TableColumn<Matricula, String> cursoColumna =
                new TableColumn<>("Curso");

        cursoColumna.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        nombresCursos.getOrDefault(
                                d.getValue().getCursoId().toString(),
                                "Curso desconocido"
                        )
                )
        );

        TableColumn<Matricula, String> fechaColumna =
                new TableColumn<>("Fecha");

        fechaColumna.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getFecha()
                )
        );

        TableColumn<Matricula, String> estadoColumna =
                new TableColumn<>("Estado");

        estadoColumna.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getEstado()
                )
        );

        usuarioColumna.setPrefWidth(180);
        cursoColumna.setPrefWidth(200);
        fechaColumna.setPrefWidth(130);
        estadoColumna.setPrefWidth(120);

        tabla.getColumns().addAll(
                usuarioColumna,
                cursoColumna,
                fechaColumna,
                estadoColumna
        );
    }

    // CARGAR MATRÍCULAS SEGÚN EL ROL
    private void cargarMatriculas() {

        if (esAdministrador) {

            tabla.setItems(
                    FXCollections.observableArrayList(
                            servicio.listar()
                    )
            );

        } else {

            tabla.setItems(
                    FXCollections.observableArrayList(
                            servicio.listarPorUsuario(
                                    usuarioActual.getId()
                            )
                    )
            );
        }
    }

    // MOSTRAR MENSAJES
    private void mostrarMensaje(
            Alert.AlertType tipo,
            String mensaje) {

        Alert alerta = new Alert(tipo);

        alerta.setTitle("Academy");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}
