
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.servicio.UsuarioServicio;
import com.mycompany.reto1_erandio_academy.servicio.LogServicio;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Optional;

public class VentanaListaUsuarios {

    private final UsuarioServicio servicio = new UsuarioServicio();

    private final String AZUL_OSCURO = "#12283A";
    private final String AZUL = "#1F6F8B";
    private final String FONDO = "#F2F5F7";
    private final String GRIS = "#64748B";

    // REGISTRAR LOG
    private void registrarLog(
            String operacion,
            String resultado,
            String error) {

        LogServicio logServicio = new LogServicio();

        try {
            logServicio.registrar(
                    operacion,
                    "usuarios",
                    resultado,
                    error);
        } finally {
            logServicio.cerrar();
        }
    }

    public void mostrar() {

        Stage ventana = new Stage();
        ventana.setTitle("Erandio Academy - Gestión de usuarios");

        // ==========================================
        // TABLA DE USUARIOS
        // ==========================================

        TableView<Usuario> tabla = new TableView<>();

        tabla.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Usuario, String> nombre =
                new TableColumn<>("Nombre");

        nombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre"));

        TableColumn<Usuario, String> email =
                new TableColumn<>("Correo electrónico");

        email.setCellValueFactory(
                new PropertyValueFactory<>("email"));

        TableColumn<Usuario, String> telefono =
                new TableColumn<>("Teléfono");

        telefono.setCellValueFactory(
                new PropertyValueFactory<>("telefono"));

        tabla.getColumns().addAll(nombre, email, telefono);

        tabla.setPlaceholder(
                new Label("No hay usuarios registrados."));

        // ==========================================
        // LISTA OBSERVABLE Y FILTRO
        // ==========================================

        ObservableList<Usuario> datosUsuarios =
                FXCollections.observableArrayList();

        FilteredList<Usuario> usuariosFiltrados =
                new FilteredList<>(datosUsuarios, usuario -> true);

        tabla.setItems(usuariosFiltrados);

        // ==========================================
        // BUSCADOR
        // ==========================================

        TextField buscador = new TextField();

        buscador.setPromptText(
                "Buscar por nombre, email o teléfono...");

        buscador.setPrefHeight(40);

        buscador.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #DCE3E8;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10;");

        buscador.textProperty().addListener(
                (obs, anterior, nuevo) -> {

            String filtro = nuevo == null
                    ? ""
                    : nuevo.toLowerCase().trim();

            usuariosFiltrados.setPredicate(usuario -> {

                if (filtro.isEmpty()) {
                    return true;
                }

                return contiene(usuario.getNombre(), filtro)
                        || contiene(usuario.getEmail(), filtro)
                        || contiene(usuario.getTelefono(), filtro);
            });
        });

        // ==========================================
        // BOTONES
        // ==========================================

        Button modificar = crearBoton(
                "Modificar usuario", AZUL);

        Button eliminar = crearBoton(
                "Eliminar usuario", "#C0392B");

        Button actualizar = crearBoton(
                "Actualizar lista", AZUL_OSCURO);

        // ==========================================
        // MODIFICAR USUARIO
        // ==========================================

        modificar.setOnAction(e -> {

            Usuario seleccionado =
                    tabla.getSelectionModel().getSelectedItem();

            if (seleccionado == null) {
                mostrarAviso("Selecciona un usuario.");
                return;
            }

            TextField nombreCampo =
                    new TextField(seleccionado.getNombre());

            TextField emailCampo =
                    new TextField(seleccionado.getEmail());

            TextField telefonoCampo =
                    new TextField(seleccionado.getTelefono());

            nombreCampo.setPrefWidth(280);
            emailCampo.setPrefWidth(280);
            telefonoCampo.setPrefWidth(280);

            GridPane formulario = new GridPane();

            formulario.setPadding(new Insets(20));
            formulario.setHgap(15);
            formulario.setVgap(15);

            formulario.add(new Label("Nombre:"), 0, 0);
            formulario.add(nombreCampo, 1, 0);

            formulario.add(new Label("Email:"), 0, 1);
            formulario.add(emailCampo, 1, 1);

            formulario.add(new Label("Teléfono:"), 0, 2);
            formulario.add(telefonoCampo, 1, 2);

            Dialog<ButtonType> dialogo = new Dialog<>();

            dialogo.setTitle("Modificar usuario");
            dialogo.setHeaderText(
                    "Editar información del usuario");

            dialogo.getDialogPane().setContent(formulario);

            dialogo.getDialogPane().getButtonTypes().addAll(
                    ButtonType.OK,
                    ButtonType.CANCEL);

            Optional<ButtonType> resultado =
                    dialogo.showAndWait();

            if (resultado.isPresent()
                    && resultado.get() == ButtonType.OK) {

                try {

                    Usuario actualizado = new Usuario(
                            seleccionado.getId(),
                            nombreCampo.getText().trim(),
                            emailCampo.getText().trim(),
                            seleccionado.getPassword(),
                            telefonoCampo.getText().trim()
                    );

                    if (servicio.modificar(actualizado)) {

                        registrarLog("UPDATE", "OK", null);

                        cargarUsuarios(datosUsuarios);

                        mostrarInformacion(
                                "Usuario modificado correctamente.");

                    } else {

                        mostrarAviso(
                                "No se han realizado cambios.");
                    }

                } catch (Exception ex) {

                    registrarLog(
                            "UPDATE", "ERROR", ex.getMessage());

                    mostrarError(ex.getMessage());
                }
            }
        });

        // ==========================================
        // ELIMINAR USUARIO
        // ==========================================

        eliminar.setOnAction(e -> {

            Usuario seleccionado =
                    tabla.getSelectionModel().getSelectedItem();

            if (seleccionado == null) {
                mostrarAviso("Selecciona un usuario.");
                return;
            }

            Alert confirmacion =
                    new Alert(Alert.AlertType.CONFIRMATION);

            confirmacion.setTitle("Eliminar usuario");

            confirmacion.setHeaderText(
                    "Confirmar eliminación");

            confirmacion.setContentText(
                    "¿Estás seguro de eliminar al usuario "
                    + seleccionado.getNombre() + "?");

            Optional<ButtonType> resultado =
                    confirmacion.showAndWait();

            if (resultado.isPresent()
                    && resultado.get() == ButtonType.OK) {

                try {

                    boolean eliminado =
                            servicio.eliminar(seleccionado.getId());

                    if (eliminado) {

                        registrarLog("DELETE", "OK", null);

                        cargarUsuarios(datosUsuarios);

                        mostrarInformacion(
                                "Usuario eliminado correctamente.");

                    } else {

                        mostrarAviso(
                                "No se ha encontrado el usuario.");
                    }

                } catch (Exception ex) {

                    registrarLog(
                            "DELETE", "ERROR", ex.getMessage());

                    mostrarError(ex.getMessage());
                }
            }
        });

        // ==========================================
        // ACTUALIZAR LISTA
        // ==========================================

        actualizar.setOnAction(e -> {

            buscador.clear();

            cargarUsuarios(datosUsuarios);
        });

        // ==========================================
        // CABECERA
        // ==========================================

        Label etiqueta = new Label("ADMINISTRACIÓN");

        etiqueta.setStyle(
                "-fx-text-fill: " + AZUL + ";" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;");

        Label titulo = new Label("Gestión de usuarios");

        titulo.setStyle(
                "-fx-text-fill: " + AZUL_OSCURO + ";" +
                "-fx-font-size: 27px;" +
                "-fx-font-weight: bold;");

        Label descripcion = new Label(
                "Consulta, busca, modifica y administra los usuarios de la academia.");

        descripcion.setStyle(
                "-fx-text-fill: " + GRIS + ";" +
                "-fx-font-size: 13px;");

        VBox cabecera = new VBox(
                6,
                etiqueta,
                titulo,
                descripcion);

        // ==========================================
        // TARJETA PRINCIPAL
        // ==========================================

        Label tituloTabla = new Label("Usuarios registrados");

        tituloTabla.setStyle(
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + AZUL_OSCURO + ";");

        Label textoBuscar = new Label("Buscar usuarios");

        textoBuscar.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + AZUL_OSCURO + ";");

        VBox zonaBusqueda = new VBox(
                8,
                textoBuscar,
                buscador);

        HBox botones = new HBox(
                10,
                modificar,
                eliminar,
                actualizar);

        botones.setAlignment(Pos.CENTER_LEFT);

        VBox tarjeta = new VBox(
                18,
                tituloTabla,
                zonaBusqueda,
                tabla,
                botones);

        tarjeta.setPadding(new Insets(22));

        tarjeta.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #E1E7EC;" +
                "-fx-border-radius: 14;");

        VBox.setVgrow(tabla, Priority.ALWAYS);

        VBox contenido = new VBox(
                22,
                cabecera,
                tarjeta);

        contenido.setPadding(new Insets(30));

        contenido.setStyle(
                "-fx-background-color: " + FONDO + ";");

        // ==========================================
        // CARGAR DATOS
        // ==========================================

        cargarUsuarios(datosUsuarios);

        // ==========================================
        // VENTANA
        // ==========================================

        Scene escena = new Scene(contenido, 1050, 680);

        ventana.setScene(escena);

        ventana.setMinWidth(800);
        ventana.setMinHeight(550);

        ventana.setOnHidden(e -> servicio.cerrar());

        ventana.show();
    }

    // ==========================================
    // CARGAR USUARIOS
    // ==========================================

    private void cargarUsuarios(
            ObservableList<Usuario> datosUsuarios) {

        try {

            datosUsuarios.clear();

            for (Usuario usuario : servicio.listar()) {
                datosUsuarios.add(usuario);
            }

        } catch (Exception ex) {

            mostrarError(ex.getMessage());
        }
    }

    // ==========================================
    // BUSCADOR
    // ==========================================

    private boolean contiene(String texto, String filtro) {

        return texto != null
                && texto.toLowerCase().contains(filtro);
    }

    // ==========================================
    // ESTILO DE BOTONES
    // ==========================================

    private Button crearBoton(
            String texto,
            String color) {

        Button boton = new Button(texto);

        boton.setPrefHeight(40);

        boton.setPadding(
                new Insets(0, 18, 0, 18));

        boton.setCursor(Cursor.HAND);

        boton.setStyle(
                "-fx-background-color: " + color + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;");

        return boton;
    }

    // ==========================================
    // MENSAJES
    // ==========================================

    private void mostrarAviso(String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.WARNING);

        alerta.setTitle("Erandio Academy");
        alerta.setHeaderText("Atención");
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }

    private void mostrarInformacion(String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);

        alerta.setTitle("Erandio Academy");
        alerta.setHeaderText("Operación correcta");
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }

    private void mostrarError(String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.ERROR);

        alerta.setTitle("Erandio Academy");
        alerta.setHeaderText("Ha ocurrido un error");
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}
