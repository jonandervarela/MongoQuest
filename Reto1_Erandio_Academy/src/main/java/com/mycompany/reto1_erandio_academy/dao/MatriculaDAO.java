
package com.mycompany.reto1_erandio_academy.dao;

import com.mycompany.reto1_erandio_academy.modelo.Matricula;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class MatriculaDAO {

    private final MongoClient cliente;
    private final MongoCollection<Document> coleccion;

    // Conectar con MongoDB
    public MatriculaDAO() {

        cliente = MongoClients.create("mongodb://localhost:27017");

        MongoDatabase db = cliente.getDatabase("Academy");

        coleccion = db.getCollection("matriculas");
    }

    // Registrar matrícula
    public void insertar(Matricula matricula) {

        Document documento = new Document()
                .append("usuarioId", matricula.getUsuarioId())
                .append("cursoId", matricula.getCursoId())
                .append("fecha", matricula.getFecha())
                .append("estado", matricula.getEstado())
                .append("progreso", 0);

        coleccion.insertOne(documento);

        matricula.setId(documento.getObjectId("_id"));

        System.out.println("MATRÍCULA REGISTRADA CORRECTAMENTE");
    }

    // Listar todas las matrículas
    public List<Matricula> listar() {

        List<Matricula> matriculas = new ArrayList<>();

        for (Document documento : coleccion.find()) {

            Matricula matricula = new Matricula(
                    documento.getObjectId("_id"),
                    documento.getObjectId("usuarioId"),
                    documento.getObjectId("cursoId"),
                    documento.getString("fecha"),
                    documento.getString("estado")
            );

            matriculas.add(matricula);
        }

        return matriculas;
    }

    // Buscar matrículas de un usuario
    public List<Matricula> listarPorUsuario(ObjectId usuarioId) {

        List<Matricula> matriculas = new ArrayList<>();

        for (Document documento : coleccion.find(
                Filters.eq("usuarioId", usuarioId))) {

            matriculas.add(new Matricula(
                    documento.getObjectId("_id"),
                    documento.getObjectId("usuarioId"),
                    documento.getObjectId("cursoId"),
                    documento.getString("fecha"),
                    documento.getString("estado")
            ));
        }

        return matriculas;
    }

    // Comprobar si ya está matriculado
    public boolean existeMatricula(
            ObjectId usuarioId, ObjectId cursoId) {

        Document documento = coleccion.find(
                Filters.and(
                        Filters.eq("usuarioId", usuarioId),
                        Filters.eq("cursoId", cursoId),
                        Filters.eq("estado", "ACTIVA")
                )
        ).first();

        return documento != null;
    }

    // Eliminar matrícula
    public boolean eliminar(ObjectId id) {

        if (id == null) {
            return false;
        }

        return coleccion.deleteOne(
                Filters.eq("_id", id)
        ).getDeletedCount() > 0;
    }

    // Cerrar conexión
    public void cerrar() {
        cliente.close();
    }
}
