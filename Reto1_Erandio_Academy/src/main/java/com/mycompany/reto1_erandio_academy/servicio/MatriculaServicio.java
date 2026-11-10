
package com.mycompany.reto1_erandio_academy.servicio;

import com.mycompany.reto1_erandio_academy.dao.MatriculaDAO;
import com.mycompany.reto1_erandio_academy.modelo.Matricula;

import java.time.LocalDate;
import java.util.List;

import org.bson.types.ObjectId;

public class MatriculaServicio {

    private final MatriculaDAO matriculaDAO;

    // Constructor
    public MatriculaServicio() {
        matriculaDAO = new MatriculaDAO();
    }

    // Registrar matrícula
    public void matricular(ObjectId usuarioId, ObjectId cursoId) {

        // Comprobar usuario
        if (usuarioId == null) {
            throw new IllegalArgumentException(
                    "El usuario es obligatorio.");
        }

        // Comprobar curso
        if (cursoId == null) {
            throw new IllegalArgumentException(
                    "El curso es obligatorio.");
        }

        // Evitar matrículas duplicadas
        if (matriculaDAO.existeMatricula(usuarioId, cursoId)) {
            throw new IllegalArgumentException(
                    "Ya estás matriculado en este curso.");
        }

        // Crear matrícula
        Matricula matricula = new Matricula(
                null,
                usuarioId,
                cursoId,
                LocalDate.now().toString(),
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
                    "El usuario es obligatorio.");
        }

        return matriculaDAO.listarPorUsuario(usuarioId);
    }

    // Dar de baja una matrícula
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
