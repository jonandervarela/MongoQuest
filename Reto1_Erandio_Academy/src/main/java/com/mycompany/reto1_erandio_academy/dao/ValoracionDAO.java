
package com.mycompany.reto1_erandio_academy.dao;

import com.mycompany.reto1_erandio_academy.modelo.Valoracion;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class ValoracionDAO {

    private final MongoClient cliente;
    private final MongoCollection<Document> coleccion;

    // Conectar con MongoDB
    public ValoracionDAO() {

        cliente = MongoClients.create("mongodb://localhost:27017");

        MongoDatabase db = cliente.getDatabase("Academy");

        coleccion = db.getCollection("valoraciones");
    }

    // Registrar valoración
    public void insertar(Valoracion valoracion) {

        Document documento = new Document()
                .append("usuarioId", valoracion.getUsuarioId())
                .append("cursoId", valoracion.getCursoId())
                .append("puntuacion", valoracion.getPuntuacion())
                .append("comentario", valoracion.getComentario());

        coleccion.insertOne(documento);

        valoracion.setId(documento.getObjectId("_id"));

        System.out.println("VALORACIÓN REGISTRADA CORRECTAMENTE");
    }

    // Listar todas las valoraciones
    public List<Valoracion> listar() {

        List<Valoracion> valoraciones = new ArrayList<>();

        for (Document documento : coleccion.find()) {
            valoraciones.add(convertirValoracion(documento));
        }

        return valoraciones;
    }

    // Buscar valoraciones por curso
    public List<Valoracion> listarPorCurso(ObjectId cursoId) {

        List<Valoracion> valoraciones = new ArrayList<>();

        for (Document documento : coleccion.find(
                Filters.eq("cursoId", cursoId))) {

            valoraciones.add(convertirValoracion(documento));
        }

        return valoraciones;
    }

    // Buscar valoraciones por usuario
    public List<Valoracion> listarPorUsuario(ObjectId usuarioId) {

        List<Valoracion> valoraciones = new ArrayList<>();

        for (Document documento : coleccion.find(
                Filters.eq("usuarioId", usuarioId))) {

            valoraciones.add(convertirValoracion(documento));
        }

        return valoraciones;
    }

    // Convertir documento MongoDB a Valoracion
    private Valoracion convertirValoracion(Document documento) {

        return new Valoracion(
                documento.getObjectId("_id"),
                documento.getObjectId("usuarioId"),
                documento.getObjectId("cursoId"),
                documento.getInteger("puntuacion"),
                documento.getString("comentario")
        );
    }

    // Cerrar conexión
    public void cerrar() {
        cliente.close();
    }
}
