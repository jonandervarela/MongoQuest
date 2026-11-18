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
import javafx.scene.layout.VBox;

public class MenuUsuarioController implements Initializable {

    private static final String TINTA = "#12283A";
    private static final String AZUL = "#1F6F8B";
    private static final String AZUL_HOVER = "#2C87A6";
    private static final String ROJO = "#B3382C";
    private static final String ROJO_HOVER = "#C94C3E";
    private static final String GRIS = "#5B6B79";
    private static final String GRIS_HOVER = "#74828E";
    private static final String FONDO = "#F2F5F7";

    @FXML private VBox raiz;
    @FXML private VBox cabecera;
    @FXML private Label titulo;
    @FXML private Label sesion;
    @FXML private VBox cuerpo;
    @FXML private Button botonTemario;
    @FXML private Button botonPerfil;
    @FXML private Button botonValoraciones;
    @FXML private Button botonMatriculas;
    @FXML private Button botonBaja;
    @FXML private Button botonEliminar;
    @FXML private Button botonSalir;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        raiz.setStyle("-fx-background-color: " + FONDO + ";");
        cabecera.setStyle("-fx-background-color: " + TINTA + ";");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        sesion.setStyle("-fx-text-fill: #B9CAD6;");

        Usuario usuario = SesionUsuario.getUsuarioActual();
        sesion.setText(usuario == null ? "" : "Sesión iniciada como " + usuario.getNombre());

        estiloBoton(botonTemario, AZUL, AZUL_HOVER);
        estiloBoton(botonPerfil, AZUL, AZUL_HOVER);
        estiloBoton(botonValoraciones, AZUL, AZUL_HOVER);
        estiloBoton(botonMatriculas, AZUL, AZUL_HOVER);
        estiloBoton(botonBaja, AZUL, AZUL_HOVER);
        estiloBoton(botonEliminar, ROJO, ROJO_HOVER);
        estiloBoton(botonSalir, GRIS, GRIS_HOVER);
    }

    private void estiloBoton(Button boton, String color, String colorHover) {
        String base = "-fx-background-color: " + color + ";"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 14px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;"
                + "-fx-cursor: hand;";
        String hover = "-fx-background-color: " + colorHover + ";"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 14px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;"
                + "-fx-cursor: hand;";
        boton.setStyle(base);
        boton.setOnMouseEntered(e -> boton.setStyle(hover));
        boton.setOnMouseExited(e -> boton.setStyle(base));
    }

    @FXML private void onTemario() { Navegador.abrir("/com/mycompany/reto1_erandio_academy/vista/TemarioUsuario.fxml"); }
    @FXML private void onPerfil() { Navegador.abrir("/com/mycompany/reto1_erandio_academy/vista/PerfilUsuario.fxml"); }
    @FXML private void onValoraciones() { Navegador.abrir("/com/mycompany/reto1_erandio_academy/vista/Valoraciones.fxml"); }
    @FXML private void onMatriculas() { Navegador.abrir("/com/mycompany/reto1_erandio_academy/vista/Matriculas.fxml"); }
    @FXML private void onBaja() { Navegador.abrir("/com/mycompany/reto1_erandio_academy/vista/BajaMatricula.fxml"); }
    @FXML private void onEliminar() { Navegador.abrir("/com/mycompany/reto1_erandio_academy/vista/EliminarCuenta.fxml"); }

    @FXML
    private void onSalir() {
        SesionUsuario.cerrarSesion();
        Navegador.abrirYCerrar(Navegador.LOGIN, raiz);
    }
}
