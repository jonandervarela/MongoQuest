
package com.mycompany.reto1_erandio_academy.servicio;

import com.mycompany.reto1_erandio_academy.dao.ActividadDAO;
import com.mycompany.reto1_erandio_academy.modelo.Actividad;

import java.util.List;
import org.bson.types.ObjectId;

public class ActividadServicio {

    private final ActividadDAO actividadDAO;

    // Constructor
    public ActividadServicio() {
        actividadDAO = new ActividadDAO();
    }

    // Registrar actividad
    public void registrar(Actividad actividad) {

        validarActividad(actividad);

        actividadDAO.insertar(actividad);
    }

    // Listar todas las actividades
    public List<Actividad> listar() {
        return actividadDAO.listar();
    }

    // Listar actividades de un curso
    public List<Actividad> listarPorCurso(ObjectId cursoId) {

        if (cursoId == null) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un curso.");
        }

        return actividadDAO.listarPorCurso(cursoId);
    }

    // Buscar actividad por ID
    public Actividad buscarPorId(ObjectId id) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "El ID es obligatorio.");
        }

        return actividadDAO.buscarPorId(id);
    }

    // Modificar actividad
    public boolean modificar(Actividad actividad) {

        validarActividad(actividad);

        if (actividad.getId() == null) {
            throw new IllegalArgumentException(
                    "El ID de la actividad es obligatorio.");
        }

        return actividadDAO.modificar(actividad);
    }

    // Eliminar actividad
    public boolean eliminar(ObjectId id) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "Selecciona una actividad.");
        }

        return actividadDAO.eliminar(id);
    }

    // Validar actividad
    private void validarActividad(Actividad actividad) {

        if (actividad == null) {
            throw new IllegalArgumentException(
                    "La actividad no puede estar vacía.");
        }

        if (actividad.getTitulo() == null
                || actividad.getTitulo().isBlank()) {
            throw new IllegalArgumentException(
                    "El título es obligatorio.");
        }

        if (actividad.getDescripcion() == null
                || actividad.getDescripcion().isBlank()) {
            throw new IllegalArgumentException(
                    "La descripción es obligatoria.");
        }

        if (actividad.getCursoId() == null) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un curso.");
        }
    }

    // Cerrar conexión
    public void cerrar() {
        actividadDAO.cerrar();
    }
}
