package com.mycompany.reto1_erandio_academy.controlador;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Utilidad común para abrir pantallas FXML en una ventana nueva y para
 * cerrar la ventana actual desde dentro de un controlador. No es una
 * "VentanaXxx": es un ayudante de navegación usado por los controladores FXML.
 */
public final class Navegador {

    private Navegador() {
    }

    /** Rutas de los FXML principales, para no repetir cadenas por toda la app. */
    public static final String LOGIN = "/com/mycompany/reto1_erandio_academy/vista/Login.fxml";
    public static final String MENU_ADMINISTRADOR = "/com/mycompany/reto1_erandio_academy/vista/MenuAdministrador.fxml";
    public static final String MENU_EMPLEADO = "/com/mycompany/reto1_erandio_academy/vista/MenuEmpleado.fxml";
    public static final String MENU_USUARIO = "/com/mycompany/reto1_erandio_academy/vista/MenuUsuario.fxml";

    /** Abre la pantalla FXML indicada en una ventana (Stage) nueva. */
    public static void abrir(String rutaFxml) {
        try {
            FXMLLoader cargador = new FXMLLoader(Navegador.class.getResource(rutaFxml));
            Parent raiz = cargador.load();

            Stage ventana = new Stage();
            ventana.setScene(new Scene(raiz));
            ventana.setTitle("Erandio Academy");
            ventana.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Abre la pantalla FXML indicada en una ventana nueva y devuelve su
     * controlador, para que quien la abre pueda configurarla (por ejemplo,
     * pasarle un Runnable que se ejecute cuando se guarden datos nuevos).
     */
    public static <T> T abrirYDevolverControlador(String rutaFxml) {
        try {
            FXMLLoader cargador = new FXMLLoader(Navegador.class.getResource(rutaFxml));
            Parent raiz = cargador.load();

            Stage ventana = new Stage();
            ventana.setScene(new Scene(raiz));
            ventana.setTitle("Erandio Academy");
            ventana.show();

            return cargador.getController();

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /** Abre la pantalla FXML indicada y cierra la ventana a la que pertenece "origen". */
    public static void abrirYCerrar(String rutaFxml, Node origen) {
        abrir(rutaFxml);
        cerrar(origen);
    }

    /** Cierra la ventana (Stage) a la que pertenece el nodo dado. */
    public static void cerrar(Node origen) {
        Stage actual = (Stage) origen.getScene().getWindow();
        actual.close();
    }
}
