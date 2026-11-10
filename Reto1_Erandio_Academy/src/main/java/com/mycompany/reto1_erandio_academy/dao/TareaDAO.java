package com.mycompany.reto1_erandio_academy.dao;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import com.mycompany.reto1_erandio_academy.modelo.Tarea;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class TareaDAO {

    private final MongoClient cliente;
    private final MongoCollection<Document> coleccion;

    public TareaDAO() {
        cliente = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase db = cliente.getDatabase("Academy");
        coleccion = db.getCollection("tareas");
    }

    // CREAR TAREA
    public void insertar(Tarea tarea) {

        Document documento = new Document()
                .append("titulo", tarea.getTitulo())
                .append("empleadoId", tarea.getProfesorId())
                .append("cursoId", tarea.getCursoId())
                .append("fechaLimite", tarea.getFechaLimite());

        coleccion.insertOne(documento);

        tarea.setId(documento.getObjectId("_id"));
    }

    // LISTAR TODAS LAS TAREAS
    public List<Tarea> listar() {

        List<Tarea> tareas = new ArrayList<>();

        for (Document documento : coleccion.find()) {
            tareas.add(convertir(documento));
        }

        return tareas;
    }

    // LISTAR TAREAS DE UN CURSO
    public List<Tarea> listarPorCurso(ObjectId cursoId) {

        List<Tarea> tareas = new ArrayList<>();

        for (Document documento :
                coleccion.find(Filters.eq("cursoId", cursoId))) {

            tareas.add(convertir(documento));
        }

        return tareas;
    }

    // LISTAR TAREAS DE UN PROFESOR
    public List<Tarea> listarPorProfesor(ObjectId profesorId) {

        List<Tarea> tareas = new ArrayList<>();

        Bson filtro = Filters.or(
                Filters.eq("empleadoId", profesorId),
                Filters.eq("profesorId", profesorId)
        );

        for (Document documento : coleccion.find(filtro)) {
            tareas.add(convertir(documento));
        }

        return tareas;
    }

    // CONVERTIR DOCUMENTO A TAREA
    private Tarea convertir(Document documento) {

        ObjectId empleadoId = documento.getObjectId("empleadoId");

        if (empleadoId == null) {
            empleadoId = documento.getObjectId("profesorId");
        }

        return new Tarea(
                documento.getObjectId("_id"),
                documento.getString("titulo"),
                empleadoId,
                documento.getObjectId("cursoId"),
                documento.getString("fechaLimite")
        );
    }

    // EDITAR TAREA
    public void actualizar(Tarea tarea) {

        Document cambios = new Document()
                .append("titulo", tarea.getTitulo())
                .append("empleadoId", tarea.getProfesorId())
                .append("cursoId", tarea.getCursoId())
                .append("fechaLimite", tarea.getFechaLimite());

        coleccion.updateOne(
                Filters.eq("_id", tarea.getId()),
                new Document("$set", cambios)
        );
    }

    // ELIMINAR TAREA
    public void eliminar(ObjectId id) {

        coleccion.deleteOne(
                Filters.eq("_id", id)
        );
    }

    // CERRAR CONEXIÓN
    public void cerrar() {
        cliente.close();
    }
}