
package com.mycompany.reto1_erandio_academy.servicio;

import com.mycompany.reto1_erandio_academy.modelo.Usuario;

public class SesionUsuario {

    private static Usuario usuarioActual;

    // Guardar usuario que inicia sesión
    public static void iniciarSesion(Usuario usuario) {
        usuarioActual = usuario;
    }

    // Obtener usuario actual
    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    // Comprobar si hay una sesión iniciada
    public static boolean haySesion() {
        return usuarioActual != null;
    }

    // Comprobar rol
    public static boolean tieneRol(String rol) {

        if (usuarioActual == null || usuarioActual.getRol() == null) {
            return false;
        }

        return usuarioActual.getRol().equalsIgnoreCase(rol);
    }

    // Cerrar sesión
    public static void cerrarSesion() {
        usuarioActual = null;
    }
}
