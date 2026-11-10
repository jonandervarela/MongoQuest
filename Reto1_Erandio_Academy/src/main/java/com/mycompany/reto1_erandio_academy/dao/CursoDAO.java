
package com.mycompany.reto1_erandio_academy.dao;

import com.mycompany.reto1_erandio_academy.modelo.Curso;

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

public class CursoDAO {

    private final MongoClient cliente;
    private final MongoCollection<Document> coleccion;

    // Constructor: conexión con MongoDB
    public CursoDAO() {

        cliente = MongoClients.create("mongodb://localhost:27017");

        MongoDatabase db = cliente.getDatabase("Academy");

        coleccion = db.getCollection("cursos");

        System.out.println("========== DIAGNOSTICO CURSOS ==========");
        System.out.println("BASE DE DATOS: " + db.getName());
        System.out.println("COLECCION: " + coleccion.getNamespace());
        System.out.println("CURSOS EN MONGODB: " + coleccion.countDocuments());
        System.out.println("========================================");
    }

    // Registrar curso
    public void insertar(Curso curso) {

        Document documento = new Document()
                .append("titulo", curso.getNombre())
                .append("descripcion", curso.getDescripcion())
                .append("duracion", curso.getDuracion())
                .append("activo", curso.isActivo())
                .append("actividadIds", new ArrayList<ObjectId>());

        coleccion.insertOne(documento);

        curso.setId(documento.getObjectId("_id"));

        System.out.println("CURSO INSERTADO CORRECTAMENTE");
        System.out.println("ID DEL CURSO: " + curso.getId());

        // Mostrar todos los cursos guardados
        comprobarCursos();
    }

    // Comprobar documentos de MongoDB
    public void comprobarCursos() {

        System.out.println("========== CURSOS GUARDADOS ==========");

        for (Document documento : coleccion.find()) {

            System.out.println(documento.toJson());
        }

        System.out.println("TOTAL DE CURSOS: " + coleccion.countDocuments());
        System.out.println("======================================");
    }

    // Listar cursos
    public List<Curso> listar() {

        List<Curso> cursos = new ArrayList<>();

        for (Document documento : coleccion.find()) {

            Curso curso = new Curso(
                    documento.getObjectId("_id"),
                    documento.getString("titulo"),
                    documento.getString("descripcion"),
                    documento.getString("duracion"),
                    Boolean.TRUE.equals(documento.getBoolean("activo"))
            );

            cursos.add(curso);
        }

        return cursos;
    }

    // Buscar curso por ID
    public Curso buscarPorId(ObjectId id) {

        Document documento = coleccion.find(
                Filters.eq("_id", id)
        ).first();

        if (documento == null) {
            return null;
        }

        return new Curso(
                documento.getObjectId("_id"),
                documento.getString("titulo"),
                documento.getString("descripcion"),
                documento.getString("duracion"),
                Boolean.TRUE.equals(documento.getBoolean("activo"))
        );
    }

    // Modificar curso
    public boolean modificar(Curso curso) {

        if (curso.getId() == null) {
            return false;
        }

        return coleccion.updateOne(
                Filters.eq("_id", curso.getId()),
                Updates.combine(
                        Updates.set("titulo", curso.getNombre()),
                        Updates.set("descripcion", curso.getDescripcion()),
                        Updates.set("duracion", curso.getDuracion()),
                        Updates.set("activo", curso.isActivo())
                )
        ).getModifiedCount() > 0;
    }

    // Eliminar curso
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
