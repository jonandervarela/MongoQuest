package com.mycompany.reto1_erandio_academy;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;

public class CrearTareaPrueba {

    public static void main(String[] args) {

        String uri = "mongodb://localhost:27017";

        try (MongoClient cliente = MongoClients.create(uri)) {

            MongoDatabase db = cliente.getDatabase("Academy");

            MongoCollection<Document> empleados =
                    db.getCollection("empleados");

            MongoCollection<Document> tareas =
                    db.getCollection("tareas");

            // Buscar a Ana García
            Document ana = empleados.find(
                    new Document("nombre", "Ana García")
            ).first();

            if (ana == null) {
                System.out.println("No se ha encontrado a Ana García.");
                return;
            }

            ObjectId empleadoId = ana.getObjectId("_id");

            // Evitar duplicar la tarea al repetir la prueba
            Document filtro = new Document(
                    "titulo", "Preparar curso de Java"
            ).append("empleadoId", empleadoId);

            if (tareas.find(filtro).first() != null) {
                System.out.println("La tarea ya existe.");
                return;
            }

            // Crear y asignar la tarea
            Document tarea = new Document()
                    .append("titulo", "Preparar curso de Java")
                    .append("empleadoId", empleadoId)
                    .append("estado", "PENDIENTE")
                    .append("fechaLimite", "2026-11-05");

            tareas.insertOne(tarea);

            System.out.println("Tarea creada correctamente.");
            System.out.println("Empleado: " + ana.getString("nombre"));
            System.out.println("Tarea: " + tarea.getString("titulo"));
            System.out.println("Estado: " + tarea.getString("estado"));

        } catch (Exception e) {
            System.out.println("Error al crear la tarea:");
            e.printStackTrace();
        }
    }
}