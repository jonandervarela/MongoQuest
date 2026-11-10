
package com.mycompany.reto1_erandio_academy.servicio;

import com.mycompany.reto1_erandio_academy.dao.UsuarioDAO;
import com.mycompany.reto1_erandio_academy.modelo.Usuario;

import java.util.List;
import org.bson.types.ObjectId;

public class UsuarioServicio {

    private final UsuarioDAO usuarioDAO;

    // Constructor
    public UsuarioServicio() {
        usuarioDAO = new UsuarioDAO();
    }

    // Registrar alumno
    public void registrar(Usuario usuario) {

        validarUsuario(usuario);

        // El registro público siempre crea alumnos
        usuario.setRol("USUARIO");
        usuario.setEmpleadoId(null);

        // Comprobar si el email ya existe
        Usuario existente =
                usuarioDAO.buscarPorEmail(usuario.getEmail().trim());

        if (existente != null) {
            throw new IllegalArgumentException(
                    "Este email ya está registrado.");
        }

        // Proteger contraseña
        String passwordProtegida =
                PasswordUtil.generarHash(usuario.getPassword());

        usuario.setPassword(passwordProtegida);

        // Guardar alumno en MongoDB
        usuarioDAO.insertar(usuario);
    }

    // Registrar profesor desde el panel del administrador
    public void registrarEmpleado(Usuario usuario) {

        validarUsuario(usuario);

        if (usuario.getEmpleadoId() == null) {
            throw new IllegalArgumentException(
                    "Debes relacionar el profesor con un empleado.");
        }

        // Comprobar si el email ya existe
        Usuario existente =
                usuarioDAO.buscarPorEmail(usuario.getEmail().trim());

        if (existente != null) {
            throw new IllegalArgumentException(
                    "Este email ya está registrado.");
        }

        // Asignar rol de profesor
        usuario.setRol("EMPLEADO");

        // Proteger contraseña
        String passwordProtegida =
                PasswordUtil.generarHash(usuario.getPassword());

        usuario.setPassword(passwordProtegida);

        // Guardar cuenta del profesor
        usuarioDAO.insertarEmpleado(usuario);
    }

    // Iniciar sesión
    public Usuario iniciarSesion(String email, String password) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "El email es obligatorio.");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "La contraseña es obligatoria.");
        }

        // Buscar usuario por email
        Usuario usuario =
                usuarioDAO.buscarPorEmail(email.trim());

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "El usuario no existe.");
        }

        // Generar hash de la contraseña introducida
        String passwordHash =
                PasswordUtil.generarHash(password);

        // Comparar contraseñas
        if (!passwordHash.equals(usuario.getPassword())) {
            throw new IllegalArgumentException(
                    "La contraseña es incorrecta.");
        }

        return usuario;
    }

    // Listar usuarios
    public List<Usuario> listar() {
        return usuarioDAO.listar();
    }

    // Buscar usuario por ID
    public Usuario buscarPorId(ObjectId id) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "El ID del usuario es obligatorio.");
        }

        return usuarioDAO.buscarPorId(id);
    }

    // Modificar usuario
    public boolean modificar(Usuario usuario) {

        if (usuario == null || usuario.getId() == null) {
            throw new IllegalArgumentException(
                    "El usuario y su ID son obligatorios.");
        }

        if (usuario.getNombre() == null
                || usuario.getNombre().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre es obligatorio.");
        }

        if (usuario.getEmail() == null
                || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException(
                    "El email es obligatorio.");
        }

        return usuarioDAO.modificar(usuario);
    }

    // Validar datos del usuario
    private void validarUsuario(Usuario usuario) {

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "El usuario no puede estar vacío.");
        }

        if (usuario.getNombre() == null
                || usuario.getNombre().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre es obligatorio.");
        }

        if (usuario.getEmail() == null
                || usuario.getEmail().isBlank()
                || !usuario.getEmail().matches(
                        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException(
                    "El email no es válido.");
        }

        if (usuario.getPassword() == null
                || usuario.getPassword().length() < 6) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos 6 caracteres.");
        }

        if (usuario.getTelefono() == null
                || usuario.getTelefono().isBlank()) {
            throw new IllegalArgumentException(
                    "El teléfono es obligatorio.");
        }
    }

    // Eliminar usuario
    public boolean eliminar(ObjectId id) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un usuario.");
        }

        return usuarioDAO.eliminar(id);
    }

    // Cerrar conexión
    public void cerrar() {
        usuarioDAO.cerrar();
    }
}
