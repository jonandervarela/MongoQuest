package com.mycompany.reto1_erandio_academy.dao;

import com.mycompany.reto1_erandio_academy.modelo.Actividad;
import com.mycompany.reto1_erandio_academy.modelo.Ejercicio;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class ActividadDAO {

    private final MongoClient cliente;
    private final MongoCollection<Document> coleccion;

    // Conectar con MongoDB
    public ActividadDAO() {

        cliente = MongoClients.create("mongodb://localhost:27017");

        MongoDatabase db = cliente.getDatabase("Academy");

        coleccion = db.getCollection("actividades");
    }

    // Registrar actividad
    public void insertar(Actividad actividad) {

        Document documento = new Document()
                .append("titulo", actividad.getTitulo())
                .append("descripcion", actividad.getDescripcion())
                .append("cursoId", actividad.getCursoId())
                // NUEVO: los ejercicios ya no se guardan siempre vacíos
                .append("ejercicios", ejerciciosADocumentos(actividad.getEjercicios()));

        coleccion.insertOne(documento);

        actividad.setId(documento.getObjectId("_id"));

        System.out.println("ACTIVIDAD REGISTRADA CORRECTAMENTE");
    }

    // Listar todas las actividades
    public List<Actividad> listar() {

        List<Actividad> actividades = new ArrayList<>();

        for (Document documento : coleccion.find()) {

            actividades.add(convertirActividad(documento));
        }

        return actividades;
    }

    // Buscar actividades por curso
    public List<Actividad> listarPorCurso(ObjectId cursoId) {

        List<Actividad> actividades = new ArrayList<>();

        for (Document documento : coleccion.find(
                Filters.eq("cursoId", cursoId))) {

            actividades.add(convertirActividad(documento));
        }

        return actividades;
    }

    // Buscar actividad por ID
    public Actividad buscarPorId(ObjectId id) {

        Document documento = coleccion.find(
                Filters.eq("_id", id)
        ).first();

        if (documento == null) {
            return null;
        }

        return convertirActividad(documento);
    }

    // Modificar actividad
    public boolean modificar(Actividad actividad) {

        if (actividad == null || actividad.getId() == null) {
            return false;
        }

        return coleccion.updateOne(
                Filters.eq("_id", actividad.getId()),
                Updates.combine(
                        Updates.set("titulo", actividad.getTitulo()),
                        Updates.set("descripcion", actividad.getDescripcion()),
                        Updates.set("cursoId", actividad.getCursoId()),
                        // NUEVO: antes modificar NO tocaba los ejercicios
                        Updates.set("ejercicios",
                                ejerciciosADocumentos(actividad.getEjercicios()))
                )
        ).getModifiedCount() > 0;
    }

    // Eliminar actividad
    public boolean eliminar(ObjectId id) {

        if (id == null) {
            return false;
        }

        return coleccion.deleteOne(
                Filters.eq("_id", id)
        ).getDeletedCount() > 0;
    }

    // NUEVO: convertir List<Ejercicio> a List<Document> para Mongo
    private List<Document> ejerciciosADocumentos(List<Ejercicio> ejercicios) {

        List<Document> documentos = new ArrayList<>();

        if (ejercicios == null) {
            return documentos;
        }

        for (Ejercicio ejercicio : ejercicios) {
            documentos.add(new Document("pregunta", ejercicio.getPregunta()));
        }

        return documentos;
    }

    // NUEVO: leer el array "ejercicios" de un documento de Mongo
    @SuppressWarnings("unchecked")
    private List<Ejercicio> documentosAEjercicios(Document documento) {

        List<Ejercicio> ejercicios = new ArrayList<>();

        List<Document> lista = (List<Document>) documento.get("ejercicios");

        if (lista == null) {
            return ejercicios;
        }

        for (Document item : lista) {
            ejercicios.add(new Ejercicio(item.getString("pregunta")));
        }

        return ejercicios;
    }

    // Convertir documento MongoDB a Actividad
    private Actividad convertirActividad(Document documento) {

        return new Actividad(
                documento.getObjectId("_id"),
                documento.getString("titulo"),
                documento.getString("descripcion"),
                documento.getObjectId("cursoId"),
                documentosAEjercicios(documento)
        );
    }

    // Cerrar conexión
    public void cerrar() {
        cliente.close();
    }
}
