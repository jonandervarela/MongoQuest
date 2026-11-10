
package com.mycompany.reto1_erandio_academy.servicio;

import com.mycompany.reto1_erandio_academy.dao.LogDAO;
import com.mycompany.reto1_erandio_academy.modelo.RegistroLog;

import java.util.Date;

public class LogServicio {

    private final LogDAO logDAO;

    public LogServicio() {
        logDAO = new LogDAO();
    }

    // Registrar una operación
    public void registrar(
            String operacion,
            String coleccion,
            String resultado,
            String error) {

        String nombreUsuario = "Sistema";

        if (SesionUsuario.getUsuarioActual() != null) {

            nombreUsuario =
                    SesionUsuario.getUsuarioActual().getNombre();
        }

        RegistroLog log = new RegistroLog(
                new Date(),
                nombreUsuario,
                operacion,
                coleccion,
                resultado,
                error
        );

        try {

            logDAO.insertar(log);

            System.out.println("LOG REGISTRADO CORRECTAMENTE");

        } catch (Exception e) {

            System.err.println(
                    "ERROR AL GUARDAR EL LOG: " + e.getMessage()
            );
        }
    }

    // Consultar registros
    public java.util.List<RegistroLog> listar() {

        return logDAO.listar();
    }

    // Cerrar conexión
    public void cerrar() {
        logDAO.cerrar();
    }
}
