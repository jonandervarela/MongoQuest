
package com.mycompany.reto1_erandio_academy.vista;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VentanaEmpleados extends Application {

    @Override
    public void start(Stage escenario) {

        Label titulo = new Label("ACADEMY");

        titulo.setStyle(
                "-fx-font-size: 28px;"
                + "-fx-font-weight: bold;"
        );

        Label subtitulo = new Label(
                "Sistema de gestión de empleados, tareas, cursos y usuarios"
        );

        Button botonEmpleados =
                new Button("Gestionar empleados");

        Button botonTareas =
                new Button("Gestionar tareas");

        Button botonCursos =
                new Button("Gestionar cursos");

        Button botonAsignaciones =
                new Button("Asignar profesores");

        Button botonRegistrarUsuario =
                new Button("Registrar usuario");

        Button botonUsuarios =
                new Button("Gestionar usuarios");

        Button botonLogin =
                new Button("Iniciar sesión");

        Button botonMatriculas =
                new Button("Gestionar matrículas");

        Button botonActividades =
                new Button("Gestionar actividades");
        
        Button botonEliminarCuenta =
        new Button("Eliminar cuenta");

        // NUEVO BOTÓN: CONSULTAR TEMARIO
        Button botonTemario =
                new Button("Consultar temario");
        
        Button botonValoraciones =
        new Button("Valorar cursos");
        
        Button botonBajaMatricula =
        new Button("Darse de baja de un curso");
        
        Button botonPerfil =
        new Button("Actualizar perfil");

        // Gestión de empleados
        botonEmpleados.setOnAction(e -> {
            new VentanaListaEmpleados().mostrar();
        });

        // Gestión de tareas
        botonTareas.setOnAction(e -> {
            new VentanaTareas().mostrar();
        });

        // Gestión de cursos
        botonCursos.setOnAction(e -> {
            new VentanaCursos().mostrar();
        });

        // Asignar profesores
        botonAsignaciones.setOnAction(e -> {
            new VentanaAsignaciones().mostrar();
        });

        // Registrar usuario
        botonRegistrarUsuario.setOnAction(e -> {
            new VentanaRegistroUsuario().mostrar();
        });

        // Gestionar usuarios
        botonUsuarios.setOnAction(e -> {
            new VentanaListaUsuarios().mostrar();
        });

        // Iniciar sesión
        botonLogin.setOnAction(e -> {
            new VentanaLogin().mostrar();
        });

        // Gestionar matrículas
        botonMatriculas.setOnAction(e -> {
            new VentanaMatriculas().mostrar();
        });

        // Gestionar actividades
        botonActividades.setOnAction(e -> {
            new VentanaActividades().mostrar();
        });

        // Consultar temario
        botonTemario.setOnAction(e -> {
            new VentanaTemarioUsuario().mostrar();
        });
        
        botonValoraciones.setOnAction(e -> {
    new VentanaValoraciones().mostrar();
});
        
        botonBajaMatricula.setOnAction(e -> {
    new VentanaBajaMatricula().mostrar();
});
        
        botonPerfil.setOnAction(e -> {
    new VentanaPerfilUsuario().mostrar();
});
        
        botonEliminarCuenta.setOnAction(e -> {
    new VentanaEliminarCuenta().mostrar();
});

        // Menú principal
        VBox contenido = new VBox(
                20,
                titulo,
                subtitulo,
                botonEmpleados,
                botonTareas,
                botonCursos,
                botonAsignaciones,
                botonRegistrarUsuario,
                botonUsuarios,
                botonLogin,
                botonMatriculas,
                botonActividades,
                botonTemario,
                botonValoraciones,
                botonBajaMatricula,
                botonPerfil,
                botonEliminarCuenta
        );

        contenido.setAlignment(Pos.CENTER);
        contenido.setPadding(new Insets(30));

        Scene escena = new Scene(contenido, 650, 950);

        escenario.setTitle("Academy - MongoQuest");
        escenario.setScene(escena);
        escenario.setMinWidth(500);
        escenario.setMinHeight(700);

        escenario.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
