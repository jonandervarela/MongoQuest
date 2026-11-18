package com.mycompany.reto1_erandio_academy.principal;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage escenarioPrincipal) throws IOException {
        FXMLLoader cargador = new FXMLLoader(getClass().getResource(
                "/com/mycompany/reto1_erandio_academy/vista/Login.fxml"));
        Parent raiz = cargador.load();

        escenarioPrincipal.setScene(new Scene(raiz));
        escenarioPrincipal.setTitle("Erandio Academy");
        escenarioPrincipal.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
