package com.mycompany.reto1_erandio_academy;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class InsertarDatosPrueba {

    public static void main(String[] args) {

        String uri = "mongodb://localhost:27017";

        try (MongoClient cliente = MongoClients.create(uri)) {

            MongoDatabase db = cliente.getDatabase("Academy");

            MongoCollection<Document> empleados =
                    db.getCollection("empleados");

            // Evitar insertar los mismos datos cada vez
            if (empleados.countDocuments(
                    new Document("nombre", "Ana García")) == 0) {

                Document empleado = new Document()
                        .append("nombre", "Ana García")
                        .append("puesto", "Desarrolladora")
                        .append("departamento", "Tecnología")
                        .append("activo", true);

                empleados.insertOne(empleado);

                System.out.println("Empleado insertado correctamente.");
                System.out.println("ID: " + empleado.getObjectId("_id"));

            } else {
                System.out.println("El empleado de prueba ya existe.");
            }

            System.out.println(
                    "Total de empleados: " + empleados.countDocuments()
            );

        } catch (Exception e) {
            System.out.println("Error al insertar datos:");
            e.printStackTrace();
        }
    }
}