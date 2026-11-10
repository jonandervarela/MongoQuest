
package com.mycompany.reto1_erandio_academy.servicio;

import com.mycompany.reto1_erandio_academy.dao.AsignacionDAO;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;

public class AsignacionServicio {

    private final AsignacionDAO asignacionDAO;

    public AsignacionServicio() {
        asignacionDAO = new AsignacionDAO();
    }

    // Asignar profesor a un curso
    public void asignar(ObjectId empleadoId, ObjectId cursoId) {

        if (empleadoId == null) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un profesor.");
        }

        if (cursoId == null) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un curso.");
        }

        // Evitar asignaciones duplicadas
        List<Document> asignaciones =
                asignacionDAO.listarPorCurso(cursoId);

        for (Document documento : asignaciones) {

            if (empleadoId.equals(
                    documento.getObjectId("empleadoId"))) {

                throw new IllegalArgumentException(
                        "Este profesor ya está asignado al curso.");
            }
        }

        asignacionDAO.asignar(empleadoId, cursoId);
    }

    // Consultar profesores de un curso
    public List<Document> listarPorCurso(ObjectId cursoId) {

        if (cursoId == null) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un curso.");
        }

        return asignacionDAO.listarPorCurso(cursoId);
    }

    // Quitar profesor de un curso
    public boolean quitar(ObjectId empleadoId, ObjectId cursoId) {

        if (empleadoId == null || cursoId == null) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un profesor y un curso.");
        }

        return asignacionDAO.quitar(empleadoId, cursoId);
    }

    // Cerrar conexión
    public void cerrar() {
        asignacionDAO.cerrar();
    }
}
