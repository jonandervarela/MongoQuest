
package com.mycompany.reto1_erandio_academy.servicio;

import com.mycompany.reto1_erandio_academy.dao.CursoDAO;
import com.mycompany.reto1_erandio_academy.modelo.Curso;
import java.util.List;
import org.bson.types.ObjectId;

public class CursoServicio {

    private final CursoDAO cursoDAO;

    public CursoServicio() {
        cursoDAO = new CursoDAO();
    }

    // Registrar curso
    public void registrar(Curso curso) {

        validarCurso(curso);

        cursoDAO.insertar(curso);
    }

    // Listar cursos
    public List<Curso> listar() {
        return cursoDAO.listar();
    }

    // Buscar curso por ID
    public Curso buscarPorId(ObjectId id) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "El ID del curso es obligatorio.");
        }

        return cursoDAO.buscarPorId(id);
    }

    // Modificar curso
    public boolean modificar(Curso curso) {

        validarCurso(curso);

        if (curso.getId() == null) {
            throw new IllegalArgumentException(
                    "El curso no tiene identificador.");
        }

        return cursoDAO.modificar(curso);
    }

    // Validar datos del curso
    private void validarCurso(Curso curso) {

        if (curso == null) {
            throw new IllegalArgumentException(
                    "El curso no puede estar vacío.");
        }

        if (curso.getNombre() == null
                || curso.getNombre().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del curso es obligatorio.");
        }

        if (curso.getDescripcion() == null
                || curso.getDescripcion().isBlank()) {
            throw new IllegalArgumentException(
                    "La descripción es obligatoria.");
        }

        if (curso.getDuracion() == null
                || curso.getDuracion().isBlank()) {
            throw new IllegalArgumentException(
                    "La duración es obligatoria.");
        }
    }

    // Eliminar curso
    public boolean eliminar(ObjectId id) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un curso.");
        }

        return cursoDAO.eliminar(id);
    }

    // Cerrar conexión
    public void cerrar() {
        cursoDAO.cerrar();
    }
}
