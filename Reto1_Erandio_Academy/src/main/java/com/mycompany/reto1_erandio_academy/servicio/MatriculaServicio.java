package com.mycompany.reto1_erandio_academy.servicio;

import com.mycompany.reto1_erandio_academy.dao.MatriculaDAO;
import com.mycompany.reto1_erandio_academy.modelo.Matricula;

import org.bson.types.ObjectId;

import java.util.List;

public class MatriculaServicio {

    private final MatriculaDAO matriculaDAO;

    public MatriculaServicio() {
        matriculaDAO = new MatriculaDAO();
    }

    // Matricular a un alumno en un curso
    public void matricular(ObjectId usuarioId, ObjectId cursoId) {

        if (usuarioId == null || cursoId == null) {
            throw new IllegalArgumentException(
                    "Faltan datos para matricularse.");
        }

        if (matriculaDAO.existeMatricula(usuarioId, cursoId)) {
            throw new IllegalArgumentException(
                    "Ya estás matriculado en este curso.");
        }

        Matricula matricula = new Matricula(
                null, usuarioId, cursoId,
                java.time.LocalDate.now().toString(),
                "ACTIVA"
        );

        matriculaDAO.insertar(matricula);
    }

    // Listar todas las matrículas
    public List<Matricula> listar() {
        return matriculaDAO.listar();
    }

    // Listar matrículas de un usuario
    public List<Matricula> listarPorUsuario(ObjectId usuarioId) {

        if (usuarioId == null) {
            throw new IllegalArgumentException(
                    "El usuario no es válido.");
        }

        return matriculaDAO.listarPorUsuario(usuarioId);
    }

    // NUEVO: listar matrículas (alumnos) de un curso
    public List<Matricula> listarPorCurso(ObjectId cursoId) {

        if (cursoId == null) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un curso.");
        }

        return matriculaDAO.listarPorCurso(cursoId);
    }

    // Eliminar matrícula (darse de baja)
    public boolean eliminar(ObjectId id) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "Selecciona una matrícula.");
        }

        return matriculaDAO.eliminar(id);
    }

    // Cerrar conexión
    public void cerrar() {
        matriculaDAO.cerrar();
    }
}
