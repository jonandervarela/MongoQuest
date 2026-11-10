
package com.mycompany.reto1_erandio_academy.servicio;

import com.mycompany.reto1_erandio_academy.dao.EmpleadoDAO;
import com.mycompany.reto1_erandio_academy.modelo.Empleado;
import java.util.List;
import org.bson.types.ObjectId;

public class EmpleadoServicio {

    private final EmpleadoDAO empleadoDAO;

    public EmpleadoServicio() {
        empleadoDAO = new EmpleadoDAO();
    }

    // Registrar un empleado
    public void registrar(Empleado empleado) {

        validarEmpleado(empleado);

        empleadoDAO.insertar(empleado);
    }

    // Listar empleados
    public List<Empleado> listar() {
        return empleadoDAO.listar();
    }

    // Buscar empleado por ID
    public Empleado buscarPorId(ObjectId id) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "El ID del empleado es obligatorio.");
        }

        return empleadoDAO.buscarPorId(id);
    }

    // NUEVO: Modificar empleado
    public boolean modificar(Empleado empleado) {

        validarEmpleado(empleado);

        if (empleado.getId() == null) {
            throw new IllegalArgumentException(
                    "El empleado no tiene identificador.");
        }

        return empleadoDAO.modificar(empleado);
    }

    // Validar los datos
    private void validarEmpleado(Empleado empleado) {

        if (empleado == null) {
            throw new IllegalArgumentException(
                    "El empleado no puede estar vacío.");
        }

        if (empleado.getNombre() == null
                || empleado.getNombre().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre es obligatorio.");
        }

        if (empleado.getPuesto() == null
                || empleado.getPuesto().isBlank()) {
            throw new IllegalArgumentException(
                    "El puesto es obligatorio.");
        }

        if (empleado.getDepartamento() == null
                || empleado.getDepartamento().isBlank()) {
            throw new IllegalArgumentException(
                    "El departamento es obligatorio.");
        }
    }

    // Eliminar empleado
public boolean eliminar(ObjectId id) {

    if (id == null) {
        throw new IllegalArgumentException("Debes seleccionar un empleado.");
    }

    return empleadoDAO.eliminar(id);
}

    // Cerrar conexión
    public void cerrar() {
        empleadoDAO.cerrar();
    }
}
