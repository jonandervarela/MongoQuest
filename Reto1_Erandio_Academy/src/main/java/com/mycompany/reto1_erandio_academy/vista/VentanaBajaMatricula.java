
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.dao.UsuarioDAO;
import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.modelo.Matricula;
import com.mycompany.reto1_erandio_academy.servicio.MatriculaServicio;
import com.mycompany.reto1_erandio_academy.servicio.SesionUsuario;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;

public class VentanaBajaMatricula {

    private UsuarioDAO usuarioDAO;
    private MatriculaServicio matriculaServicio;

    private ComboBox<Usuario> comboUsuarios;
    private TableView<Matricula> tablaMatriculas;

    private Usuario usuarioActual;
    private boolean esAdministrador;

    public void mostrar() {

        usuarioActual = SesionUsuario.getUsuarioActual();

        if (usuarioActual == null) {
            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "No hay ningún usuario conectado."
            );
            return;
        }

        esAdministrador = SesionUsuario.tieneRol("ADMIN");

        usuarioDAO = new UsuarioDAO();
        matriculaServicio = new MatriculaServicio();

        Stage ventana = new Stage();

        Label titulo = new Label("DAR DE BAJA DE UN CURSO");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label etiquetaUsuario = new Label("Seleccionar usuario:");

        comboUsuarios = new ComboBox<>();
        comboUsuarios.setPrefWidth(300);

        comboUsuarios.setConverter(new StringConverter<Usuario>() {

            @Override
            public String toString(Usuario usuario) {
                return usuario == null ? "" : usuario.getNombre();
            }

            @Override
            public Usuario fromString(String texto) {
                return null;
            }
        });

        // TABLA DE MATRÍCULAS
        tablaMatriculas = new TableView<>();

        TableColumn<Matricula, String> columnaCurso =
                new TableColumn<>("Curso ID");

        columnaCurso.setCellValueFactory(dato ->
                new javafx.beans.property.SimpleStringProperty(
                        dato.getValue().getCursoId().toString()
                )
        );

        TableColumn<Matricula, String> columnaFecha =
                new TableColumn<>("Fecha");

        columnaFecha.setCellValueFactory(dato ->
                new javafx.beans.property.SimpleStringProperty(
                        dato.getValue().getFecha()
                )
        );

        TableColumn<Matricula, String> columnaEstado =
                new TableColumn<>("Estado");

        columnaEstado.setCellValueFactory(dato ->
                new javafx.beans.property.SimpleStringProperty(
                        dato.getValue().getEstado()
                )
        );

        tablaMatriculas.getColumns().addAll(
                columnaCurso,
                columnaFecha,
                columnaEstado
        );

        columnaCurso.setPrefWidth(300);
        columnaFecha.setPrefWidth(120);
        columnaEstado.setPrefWidth(120);

        // BOTÓN DAR DE BAJA
        Button botonBaja = new Button("Darse de baja");

        botonBaja.setOnAction(e -> darDeBaja());

        // BOTÓN ACTUALIZAR
        Button botonActualizar = new Button("Actualizar lista");

        botonActualizar.setOnAction(e -> cargarMatriculas());

        // CONTENIDO
        VBox contenido = new VBox(
                15,
                titulo,
                etiquetaUsuario,
                comboUsuarios,
                tablaMatriculas,
                botonBaja,
                botonActualizar
        );

        contenido.setPadding(new Insets(25));

        // PERMISOS SEGÚN EL ROL
        if (esAdministrador) {

            // ADMIN PUEDE SELECCIONAR CUALQUIER USUARIO
            cargarUsuarios();

            comboUsuarios.setOnAction(e -> cargarMatriculas());

        } else {

            // USUARIO SOLO PUEDE VER SUS MATRÍCULAS
            etiquetaUsuario.setVisible(false);
            etiquetaUsuario.setManaged(false);

            comboUsuarios.setVisible(false);
            comboUsuarios.setManaged(false);

            cargarMatriculas();
        }

        Scene escena = new Scene(contenido, 800, 550);

        ventana.setTitle(
                esAdministrador
                        ? "Academy - Gestión de bajas"
                        : "Academy - Mis bajas de matrícula"
        );

        ventana.setScene(escena);

        ventana.setOnHidden(e -> {
            matriculaServicio.cerrar();
            usuarioDAO.cerrar();
        });

        ventana.show();
    }

    // CARGAR USUARIOS (SOLO ADMIN)
    private void cargarUsuarios() {

        if (!esAdministrador) {
            return;
        }

        List<Usuario> usuarios = usuarioDAO.listar();

        comboUsuarios.setItems(
                FXCollections.observableArrayList(usuarios)
        );
    }

    // CARGAR MATRÍCULAS
    private void cargarMatriculas() {

        Usuario usuario;

        if (esAdministrador) {

            usuario = comboUsuarios.getValue();

            if (usuario == null) {
                tablaMatriculas.getItems().clear();
                return;
            }

        } else {

            usuario = usuarioActual;
        }

        List<Matricula> matriculas =
                matriculaServicio.listarPorUsuario(usuario.getId());

        tablaMatriculas.setItems(
                FXCollections.observableArrayList(matriculas)
        );
    }

    // DAR DE BAJA UNA MATRÍCULA
    private void darDeBaja() {

        Matricula matricula = tablaMatriculas
                .getSelectionModel()
                .getSelectedItem();

        if (matricula == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Selecciona una matrícula."
            );
            return;
        }

        // COMPROBAR QUE EL USUARIO SOLO MODIFICA SUS MATRÍCULAS
        if (!esAdministrador
                && !matricula.getUsuarioId().equals(usuarioActual.getId())) {

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "No tienes permiso para modificar esta matrícula."
            );
            return;
        }

        if (!"ACTIVA".equals(matricula.getEstado())) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Esta matrícula ya no está activa."
            );
            return;
        }

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmacion.setTitle("Confirmar baja");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText(
                "¿Quieres darte de baja de este curso?"
        );

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL)
                == ButtonType.OK) {

            boolean resultado =
                    matriculaServicio.eliminar(matricula.getId());

            if (resultado) {

                mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Matrícula eliminada correctamente."
                );

                cargarMatriculas();

            } else {

                mostrarMensaje(
                        Alert.AlertType.ERROR,
                        "No se ha podido eliminar la matrícula."
                );
            }
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
