
package com.mycompany.reto1_erandio_academy.dao;

import com.mycompany.reto1_erandio_academy.modelo.Empleado;
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

public class EmpleadoDAO {

    private final MongoClient cliente;
    private final MongoCollection<Document> coleccion;

    public EmpleadoDAO() {
        cliente = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase db = cliente.getDatabase("Academy");
        coleccion = db.getCollection("empleados");
    }

    // Guardar un empleado
    public void insertar(Empleado empleado) {

        Document documento = new Document()
                .append("nombre", empleado.getNombre())
                .append("puesto", empleado.getPuesto())
                .append("departamento", empleado.getDepartamento())
                .append("activo", empleado.isActivo());

        coleccion.insertOne(documento);
        empleado.setId(documento.getObjectId("_id"));
    }

    // Consultar todos los empleados
    public List<Empleado> listar() {

        List<Empleado> empleados = new ArrayList<>();

        for (Document documento : coleccion.find()) {

            Empleado empleado = new Empleado(
                    documento.getObjectId("_id"),
                    documento.getString("nombre"),
                    documento.getString("puesto"),
                    documento.getString("departamento"),
                    Boolean.TRUE.equals(documento.getBoolean("activo"))
            );

            empleados.add(empleado);
        }

        return empleados;
    }

    // Buscar empleado por ID
    public Empleado buscarPorId(ObjectId id) {

        Document documento = coleccion.find(
                Filters.eq("_id", id)
        ).first();

        if (documento == null) {
            return null;
        }

        return new Empleado(
                documento.getObjectId("_id"),
                documento.getString("nombre"),
                documento.getString("puesto"),
                documento.getString("departamento"),
                Boolean.TRUE.equals(documento.getBoolean("activo"))
        );
    }

    // NUEVO: Modificar empleado
    public boolean modificar(Empleado empleado) {

        if (empleado.getId() == null) {
            return false;
        }

        return coleccion.updateOne(
                Filters.eq("_id", empleado.getId()),
                Updates.combine(
                        Updates.set("nombre", empleado.getNombre()),
                        Updates.set("puesto", empleado.getPuesto()),
                        Updates.set("departamento", empleado.getDepartamento()),
                        Updates.set("activo", empleado.isActivo())
                )
        ).getModifiedCount() > 0;
    }
    
    // Eliminar empleado
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
