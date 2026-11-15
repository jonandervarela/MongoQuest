package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.servicio.SesionUsuario;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class VentanaMenuEmpleado {

    private static final String TINTA = "#12283A";
    private static final String AZUL = "#1F6F8B";
    private static final String FONDO = "#F2F5F7";
    private static final String GRIS = "#64748B";
    private static final String ROJO = "#B3382C";

    public void mostrar() {

        Stage ventana = new Stage();

        Usuario usuario = SesionUsuario.getUsuarioActual();

        String nombreUsuario = usuario == null
                ? "Profesor"
                : usuario.getNombre();

        // =========================
        // PANEL PRINCIPAL
        // =========================

        BorderPane raiz = new BorderPane();
        raiz.setStyle("-fx-background-color: " + FONDO + ";");

        // =========================
        // BARRA LATERAL
        // =========================

        VBox lateral = new VBox(12);
        lateral.setPrefWidth(245);
        lateral.setMinWidth(245);
        lateral.setPadding(new Insets(28, 18, 22, 18));
        lateral.setStyle("-fx-background-color: " + TINTA + ";");

        Label marca = new Label("ERANDIO");
        marca.setFont(Font.font("Segoe UI", FontWeight.BOLD, 25));
        marca.setTextFill(Color.WHITE);

        Label subtituloMarca = new Label("ACADEMY");
        subtituloMarca.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        subtituloMarca.setTextFill(Color.web("#75B9D0"));

        VBox logo = new VBox(2, marca, subtituloMarca);
        logo.setPadding(new Insets(0, 8, 25, 8));

        Label etiquetaMenu = new Label("MENÚ PRINCIPAL");
        etiquetaMenu.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        etiquetaMenu.setTextFill(Color.web("#91A7B8"));
        etiquetaMenu.setPadding(new Insets(5, 8, 8, 8));

        Button menuInicio = crearBotonLateral("Inicio", true);
        Button menuActividades = crearBotonLateral("Gestionar actividades", false);
        Button menuTareas = crearBotonLateral("Gestionar tareas", false);
        Button menuTemario = crearBotonLateral("Consultar temario", false);
        Button menuAlumnos = crearBotonLateral("Consultar alumnos", false);
        Button menuPerfil = crearBotonLateral("Mi perfil", false);

        // ACCIONES DEL MENÚ

        menuActividades.setOnAction(
                e -> new VentanaActividades().mostrar()
        );

        menuTareas.setOnAction(
                e -> new VentanaTareas().mostrar()
        );

        menuTemario.setOnAction(
                e -> new VentanaTemarioUsuario().mostrar()
        );

        menuAlumnos.setOnAction(
                e -> new VentanaConsultarAlumnos().mostrar()
        );

        menuPerfil.setOnAction(
                e -> new VentanaPerfilEmpleado().mostrar()
        );

        Region espacio = new Region();
        VBox.setVgrow(espacio, Priority.ALWAYS);

        Label usuarioSesion = new Label(nombreUsuario);
        usuarioSesion.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        usuarioSesion.setTextFill(Color.WHITE);

        Label rolSesion = new Label("PROFESOR");
        rolSesion.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
        rolSesion.setTextFill(Color.web("#75B9D0"));

        VBox informacionSesion = new VBox(5, usuarioSesion, rolSesion);
        informacionSesion.setPadding(new Insets(15, 10, 15, 10));
        informacionSesion.setStyle(
                "-fx-border-color: #385064;"
                + "-fx-border-width: 1 0 0 0;"
        );

        Button botonSalir = crearBotonLateral(
                "Cerrar sesión", false
        );

        botonSalir.setOnAction(e -> {
            SesionUsuario.cerrarSesion();
            new VentanaLogin().mostrar();
            ventana.close();
        });

        lateral.getChildren().addAll(
                logo,
                etiquetaMenu,
                menuInicio,
                menuActividades,
                menuTareas,
                menuTemario,
                menuAlumnos,
                menuPerfil,
                espacio,
                informacionSesion,
                botonSalir
        );

        // =========================
        // CABECERA
        // =========================

        Label titulo = new Label("Panel de empleado");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 25));
        titulo.setTextFill(Color.web(TINTA));

        Label descripcion = new Label(
                "Gestiona tus temas, tareas y alumnos de los cursos que impartes."
        );
        descripcion.setFont(Font.font("Segoe UI", 13));
        descripcion.setTextFill(Color.web(GRIS));

        VBox textosCabecera = new VBox(5, titulo, descripcion);

        String primeraLetra = nombreUsuario.isEmpty()
                ? "P"
                : nombreUsuario.substring(0, 1).toUpperCase();

        Label inicial = new Label(primeraLetra);
        inicial.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        inicial.setTextFill(Color.WHITE);
        inicial.setAlignment(Pos.CENTER);
        inicial.setMinSize(45, 45);
        inicial.setMaxSize(45, 45);
        inicial.setStyle(
                "-fx-background-color: " + AZUL + ";"
                + "-fx-background-radius: 50;"
        );

        Region separadorCabecera = new Region();
        HBox.setHgrow(separadorCabecera, Priority.ALWAYS);

        HBox cabecera = new HBox(
                textosCabecera,
                separadorCabecera,
                inicial
        );

        cabecera.setAlignment(Pos.CENTER_LEFT);
        cabecera.setSpacing(20);
        cabecera.setPadding(new Insets(28, 35, 22, 35));

        // =========================
        // BIENVENIDA
        // =========================

        Label bienvenida = new Label(
                "¡Bienvenido, " + nombreUsuario + "!"
        );

        bienvenida.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 21)
        );
        bienvenida.setTextFill(Color.web(TINTA));

        Label textoBienvenida = new Label(
                "Desde aquí puedes acceder a todas tus herramientas de profesor."
        );

        textoBienvenida.setFont(Font.font("Segoe UI", 13));
        textoBienvenida.setTextFill(Color.web(GRIS));
        textoBienvenida.setWrapText(true);

        VBox tarjetaBienvenida = new VBox(
                9,
                bienvenida,
                textoBienvenida
        );

        tarjetaBienvenida.setPadding(new Insets(23));
        tarjetaBienvenida.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #E1E8ED;"
                + "-fx-border-radius: 12;"
        );

        // =========================
        // TARJETAS
        // =========================

        Label tituloFunciones = new Label("Mis herramientas");
        tituloFunciones.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 19)
        );
        tituloFunciones.setTextFill(Color.web(TINTA));

        GridPane tarjetas = new GridPane();
        tarjetas.setHgap(18);
        tarjetas.setVgap(18);

        ColumnConstraints columna1 = new ColumnConstraints();
        columna1.setPercentWidth(50);
        columna1.setHgrow(Priority.ALWAYS);

        ColumnConstraints columna2 = new ColumnConstraints();
        columna2.setPercentWidth(50);
        columna2.setHgrow(Priority.ALWAYS);

        tarjetas.getColumnConstraints().addAll(
                columna1,
                columna2
        );

        // TARJETA ACTIVIDADES (Gestionar Temas)

        VBox tarjetaActividades = crearTarjeta(
                "Gestionar actividades",
                "Crea, modifica y elimina los temas de tus cursos.",
                "Ver actividades"
        );

        ((Button) tarjetaActividades.getChildren().get(2)).setOnAction(
                e -> new VentanaActividades().mostrar()
        );

        // TARJETA TAREAS

        VBox tarjetaTareas = crearTarjeta(
                "Gestionar tareas",
                "Crea y revisa las tareas de tus cursos.",
                "Ver tareas"
        );

        ((Button) tarjetaTareas.getChildren().get(2)).setOnAction(
                e -> new VentanaTareas().mostrar()
        );

        // TARJETA TEMARIO

        VBox tarjetaTemario = crearTarjeta(
                "Consultar temario",
                "Accede al contenido y temario de tus cursos.",
                "Ver temario"
        );

        ((Button) tarjetaTemario.getChildren().get(2)).setOnAction(
                e -> new VentanaTemarioUsuario().mostrar()
        );

        // TARJETA ALUMNOS

        VBox tarjetaAlumnos = crearTarjeta(
                "Consultar alumnos",
                "Consulta los alumnos matriculados en tus cursos.",
                "Ver alumnos"
        );

        ((Button) tarjetaAlumnos.getChildren().get(2)).setOnAction(
                e -> new VentanaConsultarAlumnos().mostrar()
        );

        // TARJETA PERFIL

        VBox tarjetaPerfil = crearTarjeta(
                "Mi perfil",
                "Consulta tus datos y tu ficha de profesor.",
                "Ver mi perfil"
        );

        ((Button) tarjetaPerfil.getChildren().get(2)).setOnAction(
                e -> new VentanaPerfilEmpleado().mostrar()
        );

        tarjetas.add(tarjetaActividades, 0, 0);
        tarjetas.add(tarjetaTareas, 1, 0);
        tarjetas.add(tarjetaTemario, 0, 1);
        tarjetas.add(tarjetaAlumnos, 1, 1);
        tarjetas.add(tarjetaPerfil, 0, 2);

        // =========================
        // CONTENIDO CENTRAL
        // =========================

        VBox contenido = new VBox(
                22,
                tarjetaBienvenida,
                tituloFunciones,
                tarjetas
        );

        contenido.setPadding(
                new Insets(10, 35, 35, 35)
        );

        contenido.setFillWidth(true);

        VBox zonaCentral = new VBox(
                cabecera,
                contenido
        );

        zonaCentral.setStyle(
                "-fx-background-color: " + FONDO + ";"
        );

        // =========================
        // SCROLL VERTICAL
        // =========================

        ScrollPane scroll = new ScrollPane();

        scroll.setContent(zonaCentral);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(false);
        scroll.setPannable(true);

        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scroll.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scroll.setStyle(
                "-fx-background: " + FONDO + ";"
                + "-fx-background-color: " + FONDO + ";"
                + "-fx-border-color: transparent;"
        );

        // =========================
        // COLOCAR ELEMENTOS
        // =========================

        raiz.setLeft(lateral);
        raiz.setCenter(scroll);

        // =========================
        // MOSTRAR VENTANA
        // =========================

        ventana.setTitle("Erandio Academy - Panel de empleado");
        ventana.setMinWidth(950);
        ventana.setMinHeight(650);
        ventana.setWidth(1150);
        ventana.setHeight(780);

        ventana.setScene(new Scene(raiz));
        ventana.show();
    }

    // =========================
    // BOTONES LATERALES
    // =========================

    private Button crearBotonLateral(
            String texto,
            boolean seleccionado
    ) {

        Button boton = new Button(texto);

        boton.setMaxWidth(Double.MAX_VALUE);
        boton.setAlignment(Pos.CENTER_LEFT);
        boton.setPadding(new Insets(12, 14, 12, 14));
        boton.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        boton.setCursor(Cursor.HAND);

        String colorFondo = seleccionado
                ? AZUL
                : "transparent";

        boton.setStyle(
                "-fx-background-color: " + colorFondo + ";"
                + "-fx-text-fill: white;"
                + "-fx-background-radius: 7;"
        );

        if (!seleccionado) {

            boton.setOnMouseEntered(e ->
                    boton.setStyle(
                            "-fx-background-color: #25445B;"
                            + "-fx-text-fill: white;"
                            + "-fx-background-radius: 7;"
                    )
            );

            boton.setOnMouseExited(e ->
                    boton.setStyle(
                            "-fx-background-color: transparent;"
                            + "-fx-text-fill: white;"
                            + "-fx-background-radius: 7;"
                    )
            );
        }

        return boton;
    }

    // =========================
    // CREAR TARJETAS
    // =========================

    private VBox crearTarjeta(
            String titulo,
            String descripcion,
            String textoBoton
    ) {

        Label nombre = new Label(titulo);

        nombre.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 16)
        );

        nombre.setTextFill(Color.web(TINTA));

        Label detalle = new Label(descripcion);

        detalle.setFont(Font.font("Segoe UI", 12));
        detalle.setTextFill(Color.web(GRIS));
        detalle.setWrapText(true);
        detalle.setMinHeight(40);

        Button boton = new Button(textoBoton);

        boton.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 12)
        );

        boton.setTextFill(Color.WHITE);
        boton.setPadding(new Insets(10, 18, 10, 18));
        boton.setCursor(Cursor.HAND);

        boton.setStyle(
                "-fx-background-color: " + AZUL + ";"
                + "-fx-background-radius: 7;"
        );

        boton.setOnMouseEntered(e ->
                boton.setStyle(
                        "-fx-background-color: #18566D;"
                        + "-fx-background-radius: 7;"
                )
        );

        boton.setOnMouseExited(e ->
                boton.setStyle(
                        "-fx-background-color: " + AZUL + ";"
                        + "-fx-background-radius: 7;"
                )
        );

        VBox tarjeta = new VBox(
                13,
                nombre,
                detalle,
                boton
        );

        tarjeta.setPadding(new Insets(20));
        tarjeta.setMinHeight(155);
        tarjeta.setMaxWidth(Double.MAX_VALUE);

        tarjeta.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #E1E8ED;"
                + "-fx-border-radius: 12;"
        );

        return tarjeta;
    }
}
