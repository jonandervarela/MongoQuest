package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.servicio.SesionUsuario;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class VentanaMenuEmpleado {

    private static final Color TINTA = Color.web("#12283A");
    private static final Color AZUL = Color.web("#1F6F8B");
    private static final Color ROJO = Color.web("#B3382C");
    private static final Color GRIS = Color.web("#5B6B79");
    private static final Color FONDO = Color.web("#F2F5F7");

    public void mostrar() {

        Stage ventana = new Stage();

        // CABECERA
        Label titulo = new Label("Panel de empleado");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        titulo.setTextFill(Color.WHITE);

        Usuario usuario = SesionUsuario.getUsuarioActual();

        Label sesion = new Label(
                usuario == null
                        ? ""
                        : "Sesión iniciada como " + usuario.getNombre()
        );
        sesion.setTextFill(Color.web("#B9CAD6"));

        VBox cabecera = new VBox(4, titulo, sesion);
        cabecera.setPadding(new Insets(24, 30, 20, 30));
        cabecera.setBackground(fondo(TINTA, 0));

        // BOTONES
        Button botonActividades = crearBoton("Gestionar actividades", AZUL);
        Button botonTareas = crearBoton("Gestionar tareas", AZUL);
        Button botonTemario = crearBoton("Consultar temario", AZUL);
        Button botonSalir = crearBoton("Cerrar sesión", GRIS);

        // ACCIONES
        botonActividades.setOnAction(e -> new VentanaActividades().mostrar());
        botonTareas.setOnAction(e -> new VentanaTareas().mostrar());
        botonTemario.setOnAction(e -> new VentanaTemarioUsuario().mostrar());

        botonSalir.setOnAction(e -> {
            SesionUsuario.cerrarSesion();
            new VentanaLogin().mostrar();
            ventana.close();
        });

        // CONTENIDO
        VBox cuerpo = new VBox(
                12,
                botonActividades,
                botonTareas,
                botonTemario,
                botonSalir
        );

        cuerpo.setAlignment(Pos.CENTER);
        cuerpo.setPadding(new Insets(28));

        VBox raiz = new VBox(cabecera, cuerpo);
        raiz.setBackground(fondo(FONDO, 0));
        raiz.setPrefWidth(420);

        ventana.setTitle("Academy - Empleado");
        ventana.setScene(new Scene(raiz));
        ventana.show();
    }

    // Botón de color que se oscurece al pasar el ratón
    private Button crearBoton(String texto, Color color) {

        Button boton = new Button(texto);
        Color encima = color.deriveColor(0, 1, 0.85, 1);

        boton.setPrefWidth(320);
        boton.setPadding(new Insets(10, 18, 10, 18));
        boton.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        boton.setTextFill(Color.WHITE);
        boton.setBackground(fondo(color, 8));
        boton.setCursor(Cursor.HAND);

        boton.setOnMouseEntered(e -> boton.setBackground(fondo(encima, 8)));
        boton.setOnMouseExited(e -> boton.setBackground(fondo(color, 8)));

        return boton;
    }

    private Background fondo(Color color, double radio) {
        return new Background(new BackgroundFill(
                color, new CornerRadii(radio), Insets.EMPTY));
    }
}
