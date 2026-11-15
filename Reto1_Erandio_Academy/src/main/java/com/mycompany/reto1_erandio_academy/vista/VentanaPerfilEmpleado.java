package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.dao.EmpleadoDAO;
import com.mycompany.reto1_erandio_academy.dao.UsuarioDAO;
import com.mycompany.reto1_erandio_academy.modelo.Empleado;
import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.servicio.LogServicio;
import com.mycompany.reto1_erandio_academy.servicio.SesionUsuario;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * "Consultar perfil" del actor Empleado (profesor):
 * sus datos de cuenta (editables, igual que el alumno) más los
 * datos de su ficha de empleado (puesto, departamento, alta),
 * que aquí solo se consultan, no se editan.
 */
public class VentanaPerfilEmpleado {

    private UsuarioDAO usuarioDAO;
    private EmpleadoDAO empleadoDAO;

    private TextField campoNombre;
    private TextField campoEmail;
    private TextField campoTelefono;

    private Usuario usuarioActual;

    public void mostrar() {

        usuarioActual = SesionUsuario.getUsuarioActual();

        if (usuarioActual == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "No hay ningún usuario conectado."
            );
            return;
        }

        usuarioDAO = new UsuarioDAO();
        empleadoDAO = new EmpleadoDAO();

        Stage ventana = new Stage();

        Label titulo = new Label("MI PERFIL");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // ---------- Datos de la cuenta (se pueden modificar) ----------
        Label etiquetaNombre = new Label("Nombre:");
        campoNombre = new TextField(usuarioActual.getNombre());

        Label etiquetaEmail = new Label("Correo electrónico:");
        campoEmail = new TextField(usuarioActual.getEmail());

        Label etiquetaTelefono = new Label("Teléfono:");
        campoTelefono = new TextField(usuarioActual.getTelefono());

        Button botonActualizar = new Button("Actualizar mis datos");
        estiloBoton(botonActualizar);
        botonActualizar.setOnAction(e -> actualizarPerfil());

        // ---------- Datos de la ficha de empleado (solo consulta) ----------
        VBox bloqueEmpleado = construirBloqueEmpleado();

        VBox contenido = new VBox(
                12,
                titulo,
                etiquetaNombre,
                campoNombre,
                etiquetaEmail,
                campoEmail,
                etiquetaTelefono,
                campoTelefono,
                botonActualizar,
                new Separator(),
                bloqueEmpleado
        );

        contenido.setPadding(new Insets(25));
        contenido.setStyle("-fx-background-color: #F2F5F7;");

        ventana.setTitle("Academy - Mi perfil");
        ventana.setScene(new Scene(contenido, 450, 560));
        ventana.show();

        ventana.setOnHidden(e -> {
            usuarioDAO.cerrar();
            empleadoDAO.cerrar();
        });
    }

    // Datos de la ficha de empleado (puesto, departamento...),
    // solo lectura: si quieres cambiarlos, se lo pides al administrador.
    private VBox construirBloqueEmpleado() {

        Label subtitulo = new Label("Datos como profesor");
        subtitulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        if (usuarioActual.getEmpleadoId() == null) {

            Label aviso = new Label(
                    "Tu cuenta no está vinculada a ninguna ficha de "
                    + "empleado. Pide al administrador que la enlace.");
            aviso.setWrapText(true);

            return new VBox(8, subtitulo, aviso);
        }

        Empleado empleado =
                empleadoDAO.buscarPorId(usuarioActual.getEmpleadoId());

        if (empleado == null) {

            Label aviso = new Label(
                    "No se ha encontrado la ficha de empleado asociada.");
            aviso.setWrapText(true);

            return new VBox(8, subtitulo, aviso);
        }

        Label puesto = new Label("Puesto: " + empleado.getPuesto());
        Label departamento = new Label(
                "Departamento: " + empleado.getDepartamento());
        Label estado = new Label(
                "Estado: " + (empleado.isActivo() ? "Activo" : "Inactivo"));

        Label nota = new Label(
                "Estos datos los gestiona el administrador.");
        nota.setStyle("-fx-font-size: 11px; -fx-text-fill: #5B6B79;");

        return new VBox(6, subtitulo, puesto, departamento, estado, nota);
    }

    private void actualizarPerfil() {

        String nombre = campoNombre.getText().trim();
        String email = campoEmail.getText().trim();
        String telefono = campoTelefono.getText().trim();

        if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty()) {

            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Todos los campos son obligatorios.");
            return;
        }

        usuarioActual.setNombre(nombre);
        usuarioActual.setEmail(email);
        usuarioActual.setTelefono(telefono);

        LogServicio logServicio = new LogServicio();

        try {

            boolean resultado = usuarioDAO.modificar(usuarioActual);

            if (resultado) {

                logServicio.registrar("UPDATE", "usuarios", "OK", null);

                mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Tu perfil se ha actualizado correctamente.");

            } else {

                logServicio.registrar(
                        "UPDATE", "usuarios", "ERROR",
                        "No se pudo actualizar el perfil.");

                mostrarMensaje(
                        Alert.AlertType.WARNING,
                        "No se ha podido actualizar el perfil.");
            }

        } catch (Exception ex) {

            logServicio.registrar(
                    "UPDATE", "usuarios", "ERROR", ex.getMessage());

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error al actualizar el perfil: " + ex.getMessage());

        } finally {
            logServicio.cerrar();
        }
    }

    private void mostrarMensaje(Alert.AlertType tipo, String mensaje) {

        Alert alerta = new Alert(tipo);
        alerta.setTitle("Academy");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // ---------- Estilo visual (misma técnica que el resto de la app) ----------
    private void estiloBoton(Button boton) {

        String base =
                "-fx-background-color: white;"
                + "-fx-text-fill: #12283A;"
                + "-fx-font-size: 13px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;"
                + "-fx-border-color: #D5DDE3;"
                + "-fx-border-radius: 8;"
                + "-fx-cursor: hand;"
                + "-fx-padding: 8 16 8 16;";

        String encima =
                "-fx-background-color: #1F6F8B;"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 13px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;"
                + "-fx-border-radius: 8;"
                + "-fx-cursor: hand;"
                + "-fx-padding: 8 16 8 16;";

        boton.setStyle(base);
        boton.setOnMouseEntered(e -> boton.setStyle(encima));
        boton.setOnMouseExited(e -> boton.setStyle(base));
    }
}
