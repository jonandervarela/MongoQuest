
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.dao.CursoDAO;
import com.mycompany.reto1_erandio_academy.dao.UsuarioDAO;
import com.mycompany.reto1_erandio_academy.modelo.Curso;
import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.modelo.Valoracion;
import com.mycompany.reto1_erandio_academy.servicio.ValoracionServicio;
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

public class VentanaValoraciones {

    private CursoDAO cursoDAO;
    private UsuarioDAO usuarioDAO;
    private ValoracionServicio valoracionServicio;

    private ComboBox<Curso> comboCursos;
    private ComboBox<Usuario> comboUsuarios;
    private ComboBox<Integer> comboPuntuacion;

    private TextArea campoComentario;

    private TableView<Valoracion> tabla;

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
                    "valoraciones",
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
                    "No hay ningún usuario conectado."
            );
            return;
        }

        esAdministrador = SesionUsuario.tieneRol("ADMIN");

        cursoDAO = new CursoDAO();
        usuarioDAO = new UsuarioDAO();
        valoracionServicio = new ValoracionServicio();

        Stage ventana = new Stage();

        Label titulo = new Label("VALORAR CURSO");

        titulo.setStyle(
                "-fx-font-size: 24px;"
                + "-fx-font-weight: bold;"
        );

        VBox contenido = new VBox(12);
        contenido.setPadding(new Insets(25));

        contenido.getChildren().add(titulo);

        // ADMIN PUEDE SELECCIONAR CUALQUIER USUARIO
        if (esAdministrador) {

            Label etiquetaUsuario =
                    new Label("Seleccionar usuario:");

            comboUsuarios = new ComboBox<>();

            comboUsuarios.setPrefWidth(300);

            comboUsuarios.setItems(
                    FXCollections.observableArrayList(
                            usuarioDAO.listar()
                    )
            );

            comboUsuarios.setConverter(
                    new javafx.util.StringConverter<Usuario>() {

                @Override
                public String toString(Usuario usuario) {
                    return usuario == null
                            ? "" : usuario.getNombre();
                }

                @Override
                public Usuario fromString(String texto) {
                    return null;
                }
            });

            contenido.getChildren().addAll(
                    etiquetaUsuario,
                    comboUsuarios
            );

        } else {

            // USUARIO SOLAMENTE PUEDE VALORAR CON SU CUENTA
            Label etiquetaUsuario = new Label(
                    "Usuario: " + usuarioActual.getNombre()
            );

            contenido.getChildren().add(etiquetaUsuario);
        }

        Label etiquetaCurso = new Label("Seleccionar curso:");

        comboCursos = new ComboBox<>();

        comboCursos.setPrefWidth(300);

        comboCursos.setItems(
                FXCollections.observableArrayList(
                        cursoDAO.listar()
                )
        );

        comboCursos.setConverter(
                new javafx.util.StringConverter<Curso>() {

            @Override
            public String toString(Curso curso) {
                return curso == null
                        ? "" : curso.getNombre();
            }

            @Override
            public Curso fromString(String texto) {
                return null;
            }
        });

        Label etiquetaPuntuacion =
                new Label("Puntuación (1 a 5):");

        comboPuntuacion = new ComboBox<>();

        comboPuntuacion.setItems(
                FXCollections.observableArrayList(
                        1, 2, 3, 4, 5
                )
        );

        comboPuntuacion.setPromptText(
                "Selecciona una puntuación"
        );

        Label etiquetaComentario = new Label("Comentario:");

        campoComentario = new TextArea();

        campoComentario.setPromptText(
                "Escribe tu opinión sobre el curso..."
        );

        campoComentario.setPrefRowCount(4);

        Button botonGuardar =
                new Button("Guardar valoración");

        botonGuardar.setOnAction(e -> guardarValoracion());

        contenido.getChildren().addAll(
                etiquetaCurso,
                comboCursos,
                etiquetaPuntuacion,
                comboPuntuacion,
                etiquetaComentario,
                campoComentario,
                botonGuardar
        );

        // TABLA DE VALORACIONES
        tabla = new TableView<>();

        crearColumnas();

        Button botonActualizar =
                new Button("Actualizar valoraciones");

        botonActualizar.setOnAction(e -> {
            cargarNombres();
            cargarValoraciones();
            tabla.refresh();
        });

        contenido.getChildren().addAll(
                new Label("Valoraciones registradas:"),
                tabla,
                botonActualizar
        );

        cargarNombres();
        cargarValoraciones();

        Scene escena = new Scene(contenido, 750, 750);

        ventana.setTitle("Academy - Valoraciones");
        ventana.setScene(escena);

        ventana.setOnHidden(e -> {
            valoracionServicio.cerrar();
            usuarioDAO.cerrar();
            cursoDAO.cerrar();
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

    // CREAR COLUMNAS
    private void crearColumnas() {

        TableColumn<Valoracion, String> usuarioColumna =
                new TableColumn<>("Usuario");

        usuarioColumna.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        nombresUsuarios.getOrDefault(
                                d.getValue().getUsuarioId().toString(),
                                "Usuario desconocido"
                        )
                )
        );

        TableColumn<Valoracion, String> cursoColumna =
                new TableColumn<>("Curso");

        cursoColumna.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        nombresCursos.getOrDefault(
                                d.getValue().getCursoId().toString(),
                                "Curso desconocido"
                        )
                )
        );

        TableColumn<Valoracion, String> puntuacionColumna =
                new TableColumn<>("Puntuación");

        puntuacionColumna.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(d.getValue().getPuntuacion())
                )
        );

        TableColumn<Valoracion, String> comentarioColumna =
                new TableColumn<>("Comentario");

        comentarioColumna.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getComentario()
                )
        );

        usuarioColumna.setPrefWidth(140);
        cursoColumna.setPrefWidth(150);
        puntuacionColumna.setPrefWidth(100);
        comentarioColumna.setPrefWidth(250);

        tabla.getColumns().addAll(
                usuarioColumna,
                cursoColumna,
                puntuacionColumna,
                comentarioColumna
        );
    }

    // GUARDAR VALORACIÓN
    private void guardarValoracion() {

        Usuario usuario;

        if (esAdministrador) {
            usuario = comboUsuarios.getValue();
        } else {
            usuario = usuarioActual;
        }

        Curso curso = comboCursos.getValue();

        Integer puntuacion = comboPuntuacion.getValue();

        String comentario = campoComentario.getText().trim();

        if (usuario == null || curso == null || puntuacion == null) {

            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Debes seleccionar usuario, curso y puntuación."
            );

            return;
        }

        try {

            Valoracion valoracion = new Valoracion(
                    usuario.getId(),
                    curso.getId(),
                    puntuacion,
                    comentario
            );

            boolean resultado =
                    valoracionServicio.registrar(valoracion);

            if (resultado) {

                // REGISTRAR OPERACIÓN CORRECTA
                registrarLog("INSERT", "OK", null);

                mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Valoración guardada correctamente."
                );

                campoComentario.clear();
                comboPuntuacion.setValue(null);

                cargarValoraciones();

            } else {

                // REGISTRAR OPERACIÓN FALLIDA
                registrarLog(
                        "INSERT",
                        "ERROR",
                        "No se ha podido guardar la valoración."
                );

                mostrarMensaje(
                        Alert.AlertType.ERROR,
                        "No se ha podido guardar la valoración."
                );
            }

        } catch (Exception ex) {

            // REGISTRAR EXCEPCIÓN
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
    }

    // CARGAR VALORACIONES SEGÚN EL ROL
    private void cargarValoraciones() {

        if (esAdministrador) {

            tabla.setItems(
                    FXCollections.observableArrayList(
                            valoracionServicio.listar()
                    )
            );

        } else {

            tabla.setItems(
                    FXCollections.observableArrayList(
                            valoracionServicio.listarPorUsuario(
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
