
package com.mycompany.reto1_erandio_academy.dao;

import com.mycompany.reto1_erandio_academy.modelo.Usuario;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private final MongoClient cliente;
    private final MongoCollection<Document> coleccion;

    // Conectar con MongoDB
    public UsuarioDAO() {

        cliente = MongoClients.create("mongodb://localhost:27017");

        MongoDatabase db = cliente.getDatabase("Academy");

        coleccion = db.getCollection("usuarios");
    }

    // Registrar alumno
    public void insertar(Usuario usuario) {

        Document documento = new Document()
                .append("nombre", usuario.getNombre())
                .append("email", usuario.getEmail())
                .append("password", usuario.getPassword())
                .append("telefono", usuario.getTelefono())
                .append("rol", "USUARIO")
                .append("empleadoId", null)
                .append("perfil", new Document());

        coleccion.insertOne(documento);

        usuario.setId(documento.getObjectId("_id"));
        usuario.setRol("USUARIO");
        usuario.setEmpleadoId(null);

        System.out.println("ALUMNO REGISTRADO CORRECTAMENTE");
    }

    // Registrar profesor
    public void insertarEmpleado(Usuario usuario) {

        if (usuario == null || usuario.getEmpleadoId() == null) {
            throw new IllegalArgumentException(
                    "El profesor debe tener una ficha de empleado.");
        }

        Document documento = new Document()
                .append("nombre", usuario.getNombre())
                .append("email", usuario.getEmail())
                .append("password", usuario.getPassword())
                .append("telefono", usuario.getTelefono())
                .append("rol", "EMPLEADO")
                .append("empleadoId", usuario.getEmpleadoId())
                .append("perfil", new Document());

        coleccion.insertOne(documento);

        usuario.setId(documento.getObjectId("_id"));
        usuario.setRol("EMPLEADO");

        System.out.println("PROFESOR REGISTRADO CORRECTAMENTE");
    }

    // Listar usuarios
    public List<Usuario> listar() {

        List<Usuario> usuarios = new ArrayList<>();

        for (Document documento : coleccion.find()) {

            usuarios.add(convertir(documento));
        }

        return usuarios;
    }

    // Buscar usuario por ID
    public Usuario buscarPorId(ObjectId id) {

        Document documento = coleccion.find(
                Filters.eq("_id", id)
        ).first();

        if (documento == null) {
            return null;
        }

        return convertir(documento);
    }

    // Buscar usuario por email
    public Usuario buscarPorEmail(String email) {

        Document documento = coleccion.find(
                Filters.eq("email", email)
        ).first();

        if (documento == null) {
            return null;
        }

        return convertir(documento);
    }

    // Convertir documento MongoDB a Usuario
    private Usuario convertir(Document documento) {

        Usuario usuario = new Usuario(
                documento.getObjectId("_id"),
                documento.getString("nombre"),
                documento.getString("email"),
                documento.getString("password"),
                documento.getString("telefono")
        );

        usuario.setRol(documento.getString("rol"));

        usuario.setEmpleadoId(
                documento.getObjectId("empleadoId")
        );

        return usuario;
    }

    // Modificar usuario
    public boolean modificar(Usuario usuario) {

        if (usuario == null || usuario.getId() == null) {
            return false;
        }

        Document cambios = new Document()
                .append("nombre", usuario.getNombre())
                .append("email", usuario.getEmail())
                .append("telefono", usuario.getTelefono());

        return coleccion.updateOne(
                Filters.eq("_id", usuario.getId()),
                new Document("$set", cambios)
        ).getModifiedCount() > 0;
    }

    // Eliminar usuario
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
