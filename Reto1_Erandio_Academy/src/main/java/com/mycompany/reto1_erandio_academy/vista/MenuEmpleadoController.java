package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.controlador.SesionUsuario;
import com.mycompany.reto1_erandio_academy.controlador.Navegador;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MenuEmpleadoController implements Initializable {

    private static final String TINTA = "#12283A";
    private static final String AZUL = "#1F6F8B";
    private static final String AZUL_HOVER = "#18566D";
    private static final String FONDO = "#F2F5F7";
    private static final String GRIS = "#64748B";

    @FXML private BorderPane raiz;
    @FXML private VBox lateral;
    @FXML private Label marca;
    @FXML private Label subtituloMarca;
    @FXML private Label etiquetaMenu;
    @FXML private Button menuInicio;
    @FXML private Button menuActividades;
    @FXML private Button menuTareas;
    @FXML private Button menuTemario;
    @FXML private Button menuAlumnos;
    @FXML private Button menuPerfil;
    @FXML private VBox informacionSesion;
    @FXML private Label usuarioSesion;
    @FXML private Label rolSesion;
    @FXML private Button botonSalir;

    @FXML private HBox cabecera;
    @FXML private Label titulo;
    @FXML private Label descripcion;
    @FXML private Label inicial;

    @FXML private VBox tarjetaBienvenida;
    @FXML private Label bienvenida;
    @FXML private Label textoBienvenida;
    @FXML private Label tituloFunciones;

    @FXML private VBox tarjetaActividades;
    @FXML private Label nombreActividades;
    @FXML private Label detalleActividades;
    @FXML private Button botonActividades;

    @FXML private VBox tarjetaTareas;
    @FXML private Label nombreTareas;
    @FXML private Label detalleTareas;
    @FXML private Button botonTareas;

    @FXML private VBox tarjetaTemario;
    @FXML private Label nombreTemario;
    @FXML private Label detalleTemario;
    @FXML private Button botonTemario;

    @FXML private VBox tarjetaAlumnos;
    @FXML private Label nombreAlumnos;
    @FXML private Label detalleAlumnos;
    @FXML private Button botonAlumnos;

    @FXML private VBox tarjetaPerfil;
    @FXML private Label nombrePerfil;
    @FXML private Label detallePerfil;
    @FXML private Button botonPerfil;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Usuario usuario = SesionUsuario.getUsuarioActual();
        String nombreUsuario = usuario == null ? "Profesor" : usuario.getNombre();

        raiz.setStyle("-fx-background-color: " + FONDO + ";");
        lateral.setStyle("-fx-background-color: " + TINTA + ";");
        marca.setStyle("-fx-font-size: 25px; -fx-font-weight: bold; -fx-text-fill: white;");
        subtituloMarca.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #75B9D0;");
        etiquetaMenu.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #91A7B8;");

        estiloBotonLateral(menuInicio, true);
        estiloBotonLateral(menuActividades, false);
        estiloBotonLateral(menuTareas, false);
        estiloBotonLateral(menuTemario, false);
        estiloBotonLateral(menuAlumnos, false);
        estiloBotonLateral(menuPerfil, false);
        estiloBotonLateral(botonSalir, false);

        usuarioSesion.setText(nombreUsuario);
        usuarioSesion.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
        rolSesion.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #75B9D0;");
        informacionSesion.setStyle("-fx-border-color: #385064; -fx-border-width: 1 0 0 0;");

        titulo.setStyle("-fx-font-size: 25px; -fx-font-weight: bold; -fx-text-fill: " + TINTA + ";");
        descripcion.setStyle("-fx-font-size: 13px; -fx-text-fill: " + GRIS + ";");

        String primeraLetra = nombreUsuario.isEmpty() ? "P" : nombreUsuario.substring(0, 1).toUpperCase();
        inicial.setText(primeraLetra);
        inicial.setStyle(
                "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;"
                + "-fx-background-color: " + AZUL + ";"
                + "-fx-background-radius: 50;"
        );

        tarjetaBienvenida.setStyle(
                "-fx-background-color: white; -fx-background-radius: 12;"
                + "-fx-border-color: #E1E8ED; -fx-border-radius: 12;"
        );
        bienvenida.setText("¡Bienvenido, " + nombreUsuario + "!");
        bienvenida.setStyle("-fx-font-size: 21px; -fx-font-weight: bold; -fx-text-fill: " + TINTA + ";");
        textoBienvenida.setStyle("-fx-font-size: 13px; -fx-text-fill: " + GRIS + ";");
        tituloFunciones.setStyle("-fx-font-size: 19px; -fx-font-weight: bold; -fx-text-fill: " + TINTA + ";");

        estiloTarjeta(tarjetaActividades, nombreActividades, detalleActividades, botonActividades);
        estiloTarjeta(tarjetaTareas, nombreTareas, detalleTareas, botonTareas);
        estiloTarjeta(tarjetaTemario, nombreTemario, detalleTemario, botonTemario);
        estiloTarjeta(tarjetaAlumnos, nombreAlumnos, detalleAlumnos, botonAlumnos);
        estiloTarjeta(tarjetaPerfil, nombrePerfil, detallePerfil, botonPerfil);
    }

    private void estiloBotonLateral(Button boton, boolean seleccionado) {
        String colorFondo = seleccionado ? AZUL : "transparent";
        String base = "-fx-background-color: " + colorFondo + "; -fx-text-fill: white; -fx-background-radius: 7;";
        boton.setStyle(base);

        if (!seleccionado) {
            boton.setOnMouseEntered(e -> boton.setStyle("-fx-background-color: #25445B; -fx-text-fill: white; -fx-background-radius: 7;"));
            boton.setOnMouseExited(e -> boton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-background-radius: 7;"));
        }
    }

    private void estiloTarjeta(VBox tarjeta, Label nombre, Label detalle, Button boton) {
        tarjeta.setStyle(
                "-fx-background-color: white; -fx-background-radius: 12;"
                + "-fx-border-color: #E1E8ED; -fx-border-radius: 12;"
        );
        nombre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TINTA + ";");
        detalle.setStyle("-fx-font-size: 12px; -fx-text-fill: " + GRIS + ";");
        boton.setStyle("-fx-background-color: " + AZUL + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 7;");
        boton.setOnMouseEntered(e -> boton.setStyle("-fx-background-color: " + AZUL_HOVER + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 7;"));
        boton.setOnMouseExited(e -> boton.setStyle("-fx-background-color: " + AZUL + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 7;"));
    }

    @FXML private void onTareas() { Navegador.abrir("/com/mycompany/reto1_erandio_academy/vista/Tareas.fxml"); }
    @FXML private void onTemario() { Navegador.abrir("/com/mycompany/reto1_erandio_academy/vista/TemarioUsuario.fxml"); }

    @FXML
    private void onActividades() {
        Navegador.abrir("/com/mycompany/reto1_erandio_academy/vista/Actividades.fxml");
    }

    @FXML
    private void onAlumnos() {
        Navegador.abrir("/com/mycompany/reto1_erandio_academy/vista/ConsultarAlumnos.fxml");
    }

    @FXML
    private void onPerfil() {
        Navegador.abrir("/com/mycompany/reto1_erandio_academy/vista/PerfilEmpleado.fxml");
    }

    @FXML
    private void onSalir() {
        SesionUsuario.cerrarSesion();
        Navegador.abrirYCerrar(Navegador.LOGIN, raiz);
    }
}
