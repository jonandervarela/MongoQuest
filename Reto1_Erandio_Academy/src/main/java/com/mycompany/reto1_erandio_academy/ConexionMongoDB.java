package com.mycompany.reto1_erandio_academy;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class ConexionMongoDB {

    public static void main(String[] args) {

        String uri = "mongodb://localhost:27017";

        try (MongoClient cliente = MongoClients.create(uri)) {

            MongoDatabase db = cliente.getDatabase("Academy");

            // Comprueba la conexión con el servidor
            db.runCommand(new org.bson.Document("ping", 1));

            System.out.println("CONEXION CORRECTA");
            System.out.println("Base de datos: " + db.getName());

        } catch (Exception e) {

            System.out.println("ERROR DE CONEXION");
            System.out.println(e.getMessage());
        }
    }
}