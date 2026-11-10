
package com.mycompany.reto1_erandio_academy.dao;

import com.mycompany.reto1_erandio_academy.modelo.RegistroLog;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class LogDAO {

    private final MongoClient cliente;
    private final MongoCollection<Document> coleccion;

    public LogDAO() {

        cliente = MongoClients.create(
                "mongodb://localhost:27017"
        );

        MongoDatabase db = cliente.getDatabase("Academy");

        coleccion = db.getCollection("logs");
    }

    // Registrar una operación
    public void insertar(RegistroLog log) {

        Document documento = new Document()
                .append("fecha", log.getFecha())
                .append("usuario", log.getUsuario())
                .append("tipoOperacion", log.getOperacion())
                .append("coleccion", log.getColeccion())
                .append("resultado", log.getResultado())
                .append("error", log.getError());

        coleccion.insertOne(documento);

        log.setId(documento.getObjectId("_id"));
    }

    // Listar todos los registros
    public List<RegistroLog> listar() {

        List<RegistroLog> registros = new ArrayList<>();

        for (Document documento : coleccion.find()) {

            RegistroLog log = new RegistroLog(
                    documento.getDate("fecha"),
                    documento.getString("usuario"),
                    documento.getString("tipoOperacion"),
                    documento.getString("coleccion"),
                    documento.getString("resultado"),
                    documento.getString("error")
            );

            log.setId(documento.getObjectId("_id"));

            registros.add(log);
        }

        return registros;
    }

    // Cerrar conexión
    public void cerrar() {
        cliente.close();
    }
}
