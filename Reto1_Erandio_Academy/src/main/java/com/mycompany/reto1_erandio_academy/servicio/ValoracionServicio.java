
package com.mycompany.reto1_erandio_academy.servicio;

import com.mycompany.reto1_erandio_academy.dao.ValoracionDAO;
import com.mycompany.reto1_erandio_academy.modelo.Valoracion;

import org.bson.types.ObjectId;

import java.util.List;

public class ValoracionServicio {

    private final ValoracionDAO valoracionDAO;

    public ValoracionServicio() {
        valoracionDAO = new ValoracionDAO();
    }

    // Registrar valoración
    public boolean registrar(Valoracion valoracion) {

        if (valoracion == null) {
            return false;
        }

        if (valoracion.getUsuarioId() == null
                || valoracion.getCursoId() == null) {
            return false;
        }

        if (valoracion.getPuntuacion() < 1
                || valoracion.getPuntuacion() > 5) {
            return false;
        }

        if (valoracion.getComentario() == null) {
            valoracion.setComentario("");
        }

        valoracionDAO.insertar(valoracion);

        return true;
    }

    // Listar todas las valoraciones
    public List<Valoracion> listar() {
        return valoracionDAO.listar();
    }

    // Listar valoraciones por curso
    public List<Valoracion> listarPorCurso(ObjectId cursoId) {
        return valoracionDAO.listarPorCurso(cursoId);
    }

    // Listar valoraciones por usuario
    public List<Valoracion> listarPorUsuario(ObjectId usuarioId) {
        return valoracionDAO.listarPorUsuario(usuarioId);
    }

    // Cerrar conexión
    public void cerrar() {
        valoracionDAO.cerrar();
    }
}
