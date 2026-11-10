
package com.mycompany.reto1_erandio_academy.servicio;

import com.mycompany.reto1_erandio_academy.dao.EntregaDAO;
import com.mycompany.reto1_erandio_academy.modelo.Entrega;

import org.bson.types.ObjectId;

import java.time.LocalDate;

public class EntregaServicio {

    private final EntregaDAO entregaDAO;

    public EntregaServicio() {
        entregaDAO = new EntregaDAO();
    }

    // CREAR ENTREGA PENDIENTE
    public void crearEntrega(ObjectId tareaId, ObjectId usuarioId) {

        if (tareaId == null || usuarioId == null) {
            throw new IllegalArgumentException(
                    "La tarea y el alumno son obligatorios");
        }

        Entrega existente = entregaDAO.buscarPorTareaYUsuario(
                tareaId, usuarioId);

        if (existente != null) {
            return;
        }

        Entrega entrega = new Entrega(
                null,
                tareaId,
                usuarioId,
                "PENDIENTE",
                null,
                null,
                null
        );

        entregaDAO.insertar(entrega);
    }

    // REGISTRAR ARCHIVO DEL ALUMNO
    public void entregarArchivo(
            ObjectId tareaId,
            ObjectId usuarioId,
            String nombreArchivo,
            String rutaArchivo) {

        if (tareaId == null || usuarioId == null) {
            throw new IllegalArgumentException(
                    "La tarea y el alumno son obligatorios");
        }

        if (nombreArchivo == null || nombreArchivo.isBlank()
                || rutaArchivo == null || rutaArchivo.isBlank()) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un archivo");
        }

        String nombre = nombreArchivo.toLowerCase();

        if (!nombre.endsWith(".pdf")
                && !nombre.endsWith(".doc")
                && !nombre.endsWith(".docx")) {
            throw new IllegalArgumentException(
                    "Solo se permiten archivos PDF y Word");
        }

        Entrega entrega = entregaDAO.buscarPorTareaYUsuario(
                tareaId, usuarioId);

        if (entrega == null) {
            throw new IllegalArgumentException(
                    "No existe una entrega pendiente para esta tarea");
        }

        entregaDAO.actualizarArchivo(
                entrega.getId(),
                nombreArchivo,
                rutaArchivo,
                LocalDate.now().toString()
        );
    }

    // CAMBIAR ESTADO DESDE EL PROFESOR
    public void cambiarEstado(ObjectId entregaId, String estado) {

        if (entregaId == null) {
            throw new IllegalArgumentException(
                    "La entrega no es válida");
        }

        if (!estado.equals("EN_REVISION")
                && !estado.equals("APROBADA")
                && !estado.equals("CORRECCION_SOLICITADA")) {
            throw new IllegalArgumentException(
                    "Estado no permitido");
        }

        entregaDAO.actualizarEstado(entregaId, estado);
    }

    public void cerrar() {
        entregaDAO.cerrar();
    }
}
