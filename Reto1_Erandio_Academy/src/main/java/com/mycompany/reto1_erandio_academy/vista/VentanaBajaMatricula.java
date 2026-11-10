
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.dao.UsuarioDAO;
import com.mycompany.reto1_erandio_academy.dao.CursoDAO;
import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.modelo.Curso;
import com.mycompany.reto1_erandio_academy.modelo.Matricula;
import com.mycompany.reto1_erandio_academy.servicio.MatriculaServicio;

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
    private CursoDAO cursoDAO;
    private MatriculaServicio matriculaServicio;

    private ComboBox<Usuario> comboUsuarios;
    private TableView<Matricula> tablaMatriculas;

    public void mostrar() {

        usuarioDAO = new UsuarioDAO();
        cursoDAO = new CursoDAO();
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

        comboUsuarios.setOnAction(e -> cargarMatriculas());

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

        Button botonBaja = new Button("Darse de baja");

        botonBaja.setOnAction(e -> darDeBaja());

        Button botonActualizar = new Button("Actualizar lista");

        botonActualizar.setOnAction(e -> cargarMatriculas());

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

        cargarUsuarios();

        Scene escena = new Scene(contenido, 650, 550);

        ventana.setTitle("Academy - Baja de matrícula");
        ventana.setScene(escena);
        ventana.show();

        ventana.setOnHidden(e -> {
            matriculaServicio.cerrar();
            usuarioDAO.cerrar();
            cursoDAO.cerrar();
        });
    }

    private void cargarUsuarios() {

        List<Usuario> usuarios = usuarioDAO.listar();

        comboUsuarios.setItems(
                FXCollections.observableArrayList(usuarios)
        );
    }

    private void cargarMatriculas() {

        Usuario usuario = comboUsuarios.getValue();

        if (usuario == null) {
            tablaMatriculas.getItems().clear();
            return;
        }

        List<Matricula> matriculas =
                matriculaServicio.listarPorUsuario(usuario.getId());

        tablaMatriculas.setItems(
                FXCollections.observableArrayList(matriculas)
        );
    }

    private void darDeBaja() {

        Matricula matricula = tablaMatriculas.getSelectionModel()
                .getSelectedItem();

        if (matricula == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Selecciona una matrícula."
            );
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
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

    private void mostrarMensaje(Alert.AlertType tipo, String mensaje) {

        Alert alerta = new Alert(tipo);
        alerta.setTitle("Academy");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
