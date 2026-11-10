package com.mycompany.reto1_erandio_academy.servicio;

import com.mycompany.reto1_erandio_academy.dao.TareaDAO;
import com.mycompany.reto1_erandio_academy.modelo.Tarea;

import java.util.List;
import org.bson.types.ObjectId;

public class TareaServicio {

    private final TareaDAO tareaDAO;

    public TareaServicio() {
        tareaDAO = new TareaDAO();
    }

    // REGISTRAR TAREA
    public void registrar(Tarea tarea) {

        validarTarea(tarea);

        tareaDAO.insertar(tarea);
    }

    // LISTAR TODAS LAS TAREAS
    public List<Tarea> listar() {

        return tareaDAO.listar();
    }

    // LISTAR TAREAS DE UN CURSO
    public List<Tarea> listarPorCurso(ObjectId cursoId) {

        if (cursoId == null) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un curso.");
        }

        return tareaDAO.listarPorCurso(cursoId);
    }

    // LISTAR TAREAS DE UN PROFESOR
    public List<Tarea> listarPorProfesor(ObjectId profesorId) {

        if (profesorId == null) {
            throw new IllegalArgumentException(
                    "No se ha identificado al profesor.");
        }

        return tareaDAO.listarPorProfesor(profesorId);
    }

    // EDITAR TAREA
    public void actualizar(Tarea tarea) {

        if (tarea == null || tarea.getId() == null) {
            throw new IllegalArgumentException(
                    "Selecciona una tarea para editar.");
        }

        validarTarea(tarea);

        tareaDAO.actualizar(tarea);
    }

    // ELIMINAR TAREA
    public void eliminar(ObjectId id) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "Selecciona una tarea para eliminar.");
        }

        tareaDAO.eliminar(id);
    }

    // VALIDAR DATOS
    private void validarTarea(Tarea tarea) {

        if (tarea == null) {
            throw new IllegalArgumentException(
                    "Los datos de la tarea son obligatorios.");
        }

        if (tarea.getTitulo() == null
                || tarea.getTitulo().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El título de la tarea es obligatorio.");
        }

        if (tarea.getProfesorId() == null) {

            throw new IllegalArgumentException(
                    "Debes seleccionar un profesor.");
        }

        if (tarea.getCursoId() == null) {

            throw new IllegalArgumentException(
                    "Debes seleccionar un curso.");
        }
    }

    // CERRAR CONEXIÓN
    public void cerrar() {

        tareaDAO.cerrar();
    }
}