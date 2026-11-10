
package com.mycompany.reto1_erandio_academy.dao;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class AsignacionDAO {

    private final MongoClient cliente;
    private final MongoCollection<Document> coleccion;

    public AsignacionDAO() {

        cliente = MongoClients.create("mongodb://localhost:27017");

        MongoDatabase db = cliente.getDatabase("Academy");

        coleccion = db.getCollection("asignaciones");
    }

    // Asignar profesor a un curso
    public void asignar(ObjectId empleadoId, ObjectId cursoId) {

        Document documento = new Document()
                .append("empleadoId", empleadoId)
                .append("cursoId", cursoId);

        coleccion.insertOne(documento);
    }

    // Consultar profesores asignados a un curso
    public List<Document> listarPorCurso(ObjectId cursoId) {

        List<Document> asignaciones = new ArrayList<>();

        for (Document documento : coleccion.find(
                Filters.eq("cursoId", cursoId))) {

            asignaciones.add(documento);
        }

        return asignaciones;
    }

    // NUEVO: Consultar cursos asignados a un profesor
    public List<Document> listarPorEmpleado(ObjectId empleadoId) {

        List<Document> asignaciones = new ArrayList<>();

        for (Document documento : coleccion.find(
                Filters.eq("empleadoId", empleadoId))) {

            asignaciones.add(documento);
        }

        return asignaciones;
    }

    // Quitar profesor de un curso
    public boolean quitar(ObjectId empleadoId, ObjectId cursoId) {

        return coleccion.deleteOne(
                Filters.and(
                        Filters.eq("empleadoId", empleadoId),
                        Filters.eq("cursoId", cursoId)
                )
        ).getDeletedCount() > 0;
    }

    // Cerrar conexión
    public void cerrar() {
        cliente.close();
    }
}
