
package com.mycompany.reto1_erandio_academy.dao;

import com.mycompany.reto1_erandio_academy.modelo.Entrega;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class EntregaDAO {

    private final MongoClient cliente;
    private final MongoCollection<Document> coleccion;

    public EntregaDAO() {

        cliente = MongoClients.create("mongodb://localhost:27017");

        MongoDatabase db = cliente.getDatabase("Academy");

        coleccion = db.getCollection("entregas");
    }

    // REGISTRAR ENTREGA
    public void insertar(Entrega entrega) {

        Document documento = new Document()
                .append("tareaId", entrega.getTareaId())
                .append("usuarioId", entrega.getUsuarioId())
                .append("estado", entrega.getEstado())
                .append("fechaEntrega", entrega.getFechaEntrega())
                .append("nombreArchivo", entrega.getNombreArchivo())
                .append("rutaArchivo", entrega.getRutaArchivo());

        coleccion.insertOne(documento);

        entrega.setId(documento.getObjectId("_id"));
    }

    // LISTAR ENTREGAS DE UNA TAREA
    public List<Entrega> listarPorTarea(ObjectId tareaId) {

        List<Entrega> entregas = new ArrayList<>();

        for (Document documento : coleccion.find(
                Filters.eq("tareaId", tareaId))) {

            entregas.add(convertir(documento));
        }

        return entregas;
    }

    // BUSCAR ENTREGA DE UN ALUMNO PARA UNA TAREA
    public Entrega buscarPorTareaYUsuario(
            ObjectId tareaId, ObjectId usuarioId) {

        Document documento = coleccion.find(
                Filters.and(
                        Filters.eq("tareaId", tareaId),
                        Filters.eq("usuarioId", usuarioId)
                )
        ).first();

        if (documento == null) {
            return null;
        }

        return convertir(documento);
    }

    // ACTUALIZAR ESTADO
    public void actualizarEstado(ObjectId id, String estado) {

        coleccion.updateOne(
                Filters.eq("_id", id),
                new Document("$set",
                        new Document("estado", estado))
        );
    }

    // ACTUALIZAR ARCHIVO ENTREGADO
    public void actualizarArchivo(
            ObjectId id,
            String nombreArchivo,
            String rutaArchivo,
            String fechaEntrega) {

        Document cambios = new Document()
                .append("nombreArchivo", nombreArchivo)
                .append("rutaArchivo", rutaArchivo)
                .append("fechaEntrega", fechaEntrega)
                .append("estado", "ENTREGADA");

        coleccion.updateOne(
                Filters.eq("_id", id),
                new Document("$set", cambios)
        );
    }

    // CONVERTIR DOCUMENTO MONGODB EN ENTREGA
    private Entrega convertir(Document documento) {

        return new Entrega(
                documento.getObjectId("_id"),
                documento.getObjectId("tareaId"),
                documento.getObjectId("usuarioId"),
                documento.getString("estado"),
                documento.getString("fechaEntrega"),
                documento.getString("nombreArchivo"),
                documento.getString("rutaArchivo")
        );
    }

    // CERRAR CONEXIÓN
    public void cerrar() {
        cliente.close();
    }
}
