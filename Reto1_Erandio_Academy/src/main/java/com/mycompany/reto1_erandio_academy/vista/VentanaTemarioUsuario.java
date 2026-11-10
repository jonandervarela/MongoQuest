
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.dao.CursoDAO;
import com.mycompany.reto1_erandio_academy.modelo.Curso;
import com.mycompany.reto1_erandio_academy.modelo.Actividad;
import com.mycompany.reto1_erandio_academy.servicio.ActividadServicio;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;

public class VentanaTemarioUsuario {

    private final CursoDAO cursoDAO;
    private final ActividadServicio actividadServicio;

    private ComboBox<Curso> comboCursos;
    private TableView<Actividad> tablaActividades;

    public VentanaTemarioUsuario() {
        cursoDAO = new CursoDAO();
        actividadServicio = new ActividadServicio();
    }

    public void mostrar() {

        Stage ventana = new Stage();

        Label titulo = new Label("CONSULTAR TEMARIO");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label etiquetaCurso = new Label("Seleccionar curso:");

        comboCursos = new ComboBox<>();
        comboCursos.setPrefWidth(300);

        comboCursos.setConverter(new StringConverter<Curso>() {
            @Override
            public String toString(Curso curso) {
                return curso == null ? "" : curso.getNombre();
            }

            @Override
            public Curso fromString(String texto) {
                return null;
            }
        });

        comboCursos.setCellFactory(lista -> new ListCell<Curso>() {
            @Override
            protected void updateItem(Curso curso, boolean vacio) {
                super.updateItem(curso, vacio);
                setText(vacio || curso == null ? null : curso.getNombre());
            }
        });

        tablaActividades = new TableView<>();

        TableColumn<Actividad, String> columnaTitulo =
                new TableColumn<>("Título");

        columnaTitulo.setCellValueFactory(
                dato -> new javafx.beans.property.SimpleStringProperty(
                        dato.getValue().getTitulo()
                )
        );

        TableColumn<Actividad, String> columnaDescripcion =
                new TableColumn<>("Descripción");

        columnaDescripcion.setCellValueFactory(
                dato -> new javafx.beans.property.SimpleStringProperty(
                        dato.getValue().getDescripcion()
                )
        );

        tablaActividades.getColumns().addAll(
                columnaTitulo,
                columnaDescripcion
        );

        columnaTitulo.setPrefWidth(200);
        columnaDescripcion.setPrefWidth(350);

        comboCursos.setOnAction(e -> cargarTemario());

        Button botonActualizar = new Button("Actualizar temario");
        botonActualizar.setOnAction(e -> cargarTemario());

        VBox contenido = new VBox(
                15,
                titulo,
                etiquetaCurso,
                comboCursos,
                tablaActividades,
                botonActualizar
        );

        contenido.setPadding(new Insets(25));

        cargarCursos();

        Scene escena = new Scene(contenido, 650, 550);

        ventana.setTitle("Academy - Consultar temario");
        ventana.setScene(escena);
        ventana.show();

        ventana.setOnHidden(e -> {
            actividadServicio.cerrar();
            cursoDAO.cerrar();
        });
    }

    private void cargarCursos() {

        List<Curso> cursos = cursoDAO.listar();

        comboCursos.setItems(
                FXCollections.observableArrayList(cursos)
        );
    }

    private void cargarTemario() {

        Curso curso = comboCursos.getValue();

        if (curso == null) {
            return;
        }

        List<Actividad> actividades =
                actividadServicio.listarPorCurso(curso.getId());

        tablaActividades.setItems(
                FXCollections.observableArrayList(actividades)
        );
    }
}
