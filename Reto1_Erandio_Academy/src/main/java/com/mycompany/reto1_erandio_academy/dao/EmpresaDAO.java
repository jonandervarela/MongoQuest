
package com.mycompany.reto1_erandio_academy.dao;

import com.mycompany.reto1_erandio_academy.modelo.Empresa;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class EmpresaDAO {

    private final MongoClient cliente;
    private final MongoCollection<Document> coleccion;

    public EmpresaDAO() {

        cliente = MongoClients.create("mongodb://localhost:27017");

        MongoDatabase db = cliente.getDatabase("Academy");

        coleccion = db.getCollection("empresa");
    }

    // BUSCAR EMPRESA
    public Empresa obtenerEmpresa() {

        Document documento = coleccion.find().first();

        if (documento == null) {
            return null;
        }

        Empresa empresa = new Empresa();

        empresa.setId(documento.getObjectId("_id"));
        empresa.setNombre(documento.getString("nombre"));
        empresa.setSector(documento.getString("sector"));
        empresa.setActividad(documento.getString("actividad"));
        empresa.setDireccion(documento.getString("direccion"));
        empresa.setDescripcion(documento.getString("descripcion"));
        empresa.setEstado(documento.getString("estado"));

        return empresa;
    }

    // ACTUALIZAR EMPRESA
    public void actualizar(Empresa empresa) {

        Document datos = new Document()
                .append("nombre", empresa.getNombre())
                .append("sector", empresa.getSector())
                .append("actividad", empresa.getActividad())
                .append("direccion", empresa.getDireccion())
                .append("descripcion", empresa.getDescripcion())
                .append("estado", empresa.getEstado());

        coleccion.updateOne(
                new Document("_id", empresa.getId()),
                new Document("$set", datos)
        );
    }

    public void cerrar() {
        cliente.close();
    }
}
