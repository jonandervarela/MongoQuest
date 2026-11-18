package com.mycompany.reto1_erandio_academy.controlador;

import com.mycompany.reto1_erandio_academy.modelo.Actividad;
import com.mycompany.reto1_erandio_academy.modelo.Curso;
import com.mycompany.reto1_erandio_academy.modelo.Ejercicio;
import com.mycompany.reto1_erandio_academy.modelo.Empleado;
import com.mycompany.reto1_erandio_academy.modelo.Empresa;
import com.mycompany.reto1_erandio_academy.modelo.Entrega;
import com.mycompany.reto1_erandio_academy.modelo.Matricula;
import com.mycompany.reto1_erandio_academy.modelo.RegistroLog;
import com.mycompany.reto1_erandio_academy.modelo.Tarea;
import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.modelo.Valoracion;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Clase controlador unica de la aplicacion: toda la logica de negocio
 * (validaciones + llamadas al DAO) que antes estaba repartida en 11
 * clases "XxxServicio" independientes, ahora reunida aqui.
 *
 * Usa una unica conexion a MongoDB (un unico DAOImpl) compartida por
 * todos los metodos, en vez de que cada "servicio" abriera la suya.
 *
 * Los metodos se nombran con el prefijo/sufijo de la entidad porque
 * varias entidades compartian el mismo nombre (registrar, listar,
 * eliminar...) y Java no permite sobrecargarlos solo por el retorno.
 */
public class Gestor {

    private final DAO dao;

    public Gestor() {
        dao = new DAOImpl();
    }

    // ===================== ACTIVIDAD =====================

    public void registrarActividad(Actividad actividad) {
        validarActividad(actividad);
        dao.insertarActividad(actividad);
    }

    public List<Actividad> listarActividades() {
        return dao.listarActividades();
    }

    public List<Actividad> listarActividadesPorCurso(ObjectId cursoId) {

        if (cursoId == null) {
            throw new IllegalArgumentException("Debes seleccionar un curso.");
        }

        return dao.listarActividadesPorCurso(cursoId);
    }

    public Actividad buscarActividadPorId(ObjectId id) {

        if (id == null) {
            throw new IllegalArgumentException("El ID es obligatorio.");
        }

        return dao.buscarActividadPorId(id);
    }

    public boolean modificarActividad(Actividad actividad) {

        validarActividad(actividad);

        if (actividad.getId() == null) {
            throw new IllegalArgumentException(
                    "El ID de la actividad es obligatorio.");
        }

        return dao.modificarActividad(actividad);
    }

    public boolean eliminarActividad(ObjectId id) {

        if (id == null) {
            throw new IllegalArgumentException("Selecciona una actividad.");
        }

        return dao.eliminarActividad(id);
    }

    private void validarActividad(Actividad actividad) {

        if (actividad == null) {
            throw new IllegalArgumentException(
                    "La actividad no puede estar vacía.");
        }

        if (actividad.getTitulo() == null || actividad.getTitulo().isBlank()) {
            throw new IllegalArgumentException("El título es obligatorio.");
        }

        if (actividad.getDescripcion() == null
                || actividad.getDescripcion().isBlank()) {
            throw new IllegalArgumentException("La descripción es obligatoria.");
        }

        if (actividad.getCursoId() == null) {
            throw new IllegalArgumentException("Debes seleccionar un curso.");
        }

        if (actividad.getEjercicios() != null) {

            for (Ejercicio ejercicio : actividad.getEjercicios()) {

                if (ejercicio.getPregunta() == null
                        || ejercicio.getPregunta().isBlank()) {

                    throw new IllegalArgumentException(
                            "Hay un ejercicio sin pregunta.");
                }
            }
        }
    }

    // ===================== ASIGNACION =====================

    public void asignarProfesor(ObjectId empleadoId, ObjectId cursoId) {

        if (empleadoId == null) {
            throw new IllegalArgumentException("Debes seleccionar un profesor.");
        }

        if (cursoId == null) {
            throw new IllegalArgumentException("Debes seleccionar un curso.");
        }

        List<Document> asignaciones = dao.listarAsignacionesPorCurso(cursoId);

        for (Document documento : asignaciones) {

            if (empleadoId.equals(documento.getObjectId("empleadoId"))) {

                throw new IllegalArgumentException(
                        "Este profesor ya está asignado al curso.");
            }
        }

        dao.asignarProfesor(empleadoId, cursoId);
    }

    public List<Document> listarAsignacionesPorCurso(ObjectId cursoId) {

        if (cursoId == null) {
            throw new IllegalArgumentException("Debes seleccionar un curso.");
        }

        return dao.listarAsignacionesPorCurso(cursoId);
    }

    public List<Document> listarAsignacionesPorEmpleado(ObjectId empleadoId) {

        if (empleadoId == null) {
            throw new IllegalArgumentException("El empleado no es válido.");
        }

        return dao.listarAsignacionesPorEmpleado(empleadoId);
    }

    public boolean quitarProfesor(ObjectId empleadoId, ObjectId cursoId) {

        if (empleadoId == null || cursoId == null) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un profesor y un curso.");
        }

        return dao.quitarProfesor(empleadoId, cursoId);
    }

    // ===================== CURSO =====================

    public void registrarCurso(Curso curso) {
        validarCurso(curso);
        dao.insertarCurso(curso);
    }

    public List<Curso> listarCursos() {
        return dao.listarCursos();
    }

    public Curso buscarCursoPorId(ObjectId id) {

        if (id == null) {
            throw new IllegalArgumentException("El ID del curso es obligatorio.");
        }

        return dao.buscarCursoPorId(id);
    }

    public boolean modificarCurso(Curso curso) {

        validarCurso(curso);

        if (curso.getId() == null) {
            throw new IllegalArgumentException("El curso no tiene identificador.");
        }

        return dao.modificarCurso(curso);
    }

    public boolean eliminarCurso(ObjectId id) {

        if (id == null) {
            throw new IllegalArgumentException("Debes seleccionar un curso.");
        }

        return dao.eliminarCurso(id);
    }

    private void validarCurso(Curso curso) {

        if (curso == null) {
            throw new IllegalArgumentException("El curso no puede estar vacío.");
        }

        if (curso.getNombre() == null || curso.getNombre().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del curso es obligatorio.");
        }

        if (curso.getDescripcion() == null || curso.getDescripcion().isBlank()) {
            throw new IllegalArgumentException("La descripción es obligatoria.");
        }

        if (curso.getDuracion() == null || curso.getDuracion().isBlank()) {
            throw new IllegalArgumentException("La duración es obligatoria.");
        }
    }

    // ===================== EMPLEADO =====================

    public void registrarEmpleado(Empleado empleado) {
        validarEmpleado(empleado);
        dao.insertarEmpleado(empleado);
    }

    public List<Empleado> listarEmpleados() {
        return dao.listarEmpleados();
    }

    public Empleado buscarEmpleadoPorId(ObjectId id) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "El ID del empleado es obligatorio.");
        }

        return dao.buscarEmpleadoPorId(id);
    }

    public boolean modificarEmpleado(Empleado empleado) {

        validarEmpleado(empleado);

        if (empleado.getId() == null) {
            throw new IllegalArgumentException(
                    "El empleado no tiene identificador.");
        }

        return dao.modificarEmpleado(empleado);
    }

    public boolean eliminarEmpleado(ObjectId id) {

        if (id == null) {
            throw new IllegalArgumentException("Debes seleccionar un empleado.");
        }

        return dao.eliminarEmpleado(id);
    }

    private void validarEmpleado(Empleado empleado) {

        if (empleado == null) {
            throw new IllegalArgumentException("El empleado no puede estar vacío.");
        }

        if (empleado.getNombre() == null || empleado.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }

        if (empleado.getPuesto() == null || empleado.getPuesto().isBlank()) {
            throw new IllegalArgumentException("El puesto es obligatorio.");
        }

        if (empleado.getDepartamento() == null
                || empleado.getDepartamento().isBlank()) {
            throw new IllegalArgumentException("El departamento es obligatorio.");
        }
    }

    // ===================== EMPRESA =====================

    public Empresa obtenerEmpresa() {
        return dao.obtenerEmpresa();
    }

    public void actualizarEmpresa(Empresa empresa) {

        if (empresa == null) {
            throw new IllegalArgumentException("La empresa no puede estar vacía.");
        }

        if (empresa.getNombre() == null || empresa.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre de la empresa es obligatorio.");
        }

        if (empresa.getId() == null) {
            throw new IllegalArgumentException(
                    "La empresa no tiene un identificador.");
        }

        dao.actualizarEmpresa(empresa);
    }

    // ===================== ENTREGA =====================

    public void crearEntrega(ObjectId tareaId, ObjectId usuarioId) {

        if (tareaId == null || usuarioId == null) {
            throw new IllegalArgumentException(
                    "La tarea y el alumno son obligatorios");
        }

        Entrega existente = dao.buscarEntregaPorTareaYUsuario(tareaId, usuarioId);

        if (existente != null) {
            return;
        }

        Entrega entrega = new Entrega(
                null, tareaId, usuarioId, "PENDIENTE", null, null, null);

        dao.insertarEntrega(entrega);
    }

    public void entregarArchivo(ObjectId tareaId, ObjectId usuarioId,
            String nombreArchivo, String rutaArchivo) {

        if (tareaId == null || usuarioId == null) {
            throw new IllegalArgumentException(
                    "La tarea y el alumno son obligatorios");
        }

        if (nombreArchivo == null || nombreArchivo.isBlank()
                || rutaArchivo == null || rutaArchivo.isBlank()) {
            throw new IllegalArgumentException("Debes seleccionar un archivo");
        }

        String nombre = nombreArchivo.toLowerCase();

        if (!nombre.endsWith(".pdf")
                && !nombre.endsWith(".doc")
                && !nombre.endsWith(".docx")) {
            throw new IllegalArgumentException("Solo se permiten archivos PDF y Word");
        }

        Entrega entrega = dao.buscarEntregaPorTareaYUsuario(tareaId, usuarioId);

        if (entrega == null) {
            throw new IllegalArgumentException(
                    "No existe una entrega pendiente para esta tarea");
        }

        dao.actualizarArchivoEntrega(
                entrega.getId(), nombreArchivo, rutaArchivo,
                LocalDate.now().toString());
    }

    public void cambiarEstado(ObjectId entregaId, String estado) {

        if (entregaId == null) {
            throw new IllegalArgumentException("La entrega no es válida");
        }

        if (!estado.equals("EN_REVISION")
                && !estado.equals("APROBADA")
                && !estado.equals("CORRECCION_SOLICITADA")) {
            throw new IllegalArgumentException("Estado no permitido");
        }

        dao.actualizarEstadoEntrega(entregaId, estado);
    }

    // ===================== LOG =====================

    public void registrarLog(String operacion, String coleccion,
            String resultado, String error) {

        String nombreUsuario = "Sistema";

        if (SesionUsuario.getUsuarioActual() != null) {
            nombreUsuario = SesionUsuario.getUsuarioActual().getNombre();
        }

        RegistroLog log = new RegistroLog(
                new Date(), nombreUsuario, operacion, coleccion, resultado, error);

        try {
            dao.insertarLog(log);
            System.out.println("LOG REGISTRADO CORRECTAMENTE");
        } catch (Exception e) {
            System.err.println("ERROR AL GUARDAR EL LOG: " + e.getMessage());
        }
    }

    public List<RegistroLog> listarLogs() {
        return dao.listarLogs();
    }

    // ===================== MATRICULA =====================

    public void matricular(ObjectId usuarioId, ObjectId cursoId) {

        if (usuarioId == null || cursoId == null) {
            throw new IllegalArgumentException("Faltan datos para matricularse.");
        }

        if (dao.existeMatricula(usuarioId, cursoId)) {
            throw new IllegalArgumentException("Ya estás matriculado en este curso.");
        }

        Matricula matricula = new Matricula(
                null, usuarioId, cursoId,
                LocalDate.now().toString(), "ACTIVA");

        dao.insertarMatricula(matricula);
    }

    public List<Matricula> listarMatriculas() {
        return dao.listarMatriculas();
    }

    public List<Matricula> listarMatriculasPorUsuario(ObjectId usuarioId) {

        if (usuarioId == null) {
            throw new IllegalArgumentException("El usuario no es válido.");
        }

        return dao.listarMatriculasPorUsuario(usuarioId);
    }

    public List<Matricula> listarMatriculasPorCurso(ObjectId cursoId) {

        if (cursoId == null) {
            throw new IllegalArgumentException("Debes seleccionar un curso.");
        }

        return dao.listarMatriculasPorCurso(cursoId);
    }

    public boolean eliminarMatricula(ObjectId id) {

        if (id == null) {
            throw new IllegalArgumentException("Selecciona una matrícula.");
        }

        return dao.eliminarMatricula(id);
    }

    // ===================== TAREA =====================

    public void registrarTarea(Tarea tarea) {
        validarTarea(tarea);
        dao.insertarTarea(tarea);
    }

    public List<Tarea> listarTareas() {
        return dao.listarTareas();
    }

    public List<Tarea> listarTareasPorCurso(ObjectId cursoId) {

        if (cursoId == null) {
            throw new IllegalArgumentException("Debes seleccionar un curso.");
        }

        return dao.listarTareasPorCurso(cursoId);
    }

    public List<Tarea> listarTareasPorProfesor(ObjectId profesorId) {

        if (profesorId == null) {
            throw new IllegalArgumentException("No se ha identificado al profesor.");
        }

        return dao.listarTareasPorProfesor(profesorId);
    }

    public void actualizarTarea(Tarea tarea) {

        if (tarea == null || tarea.getId() == null) {
            throw new IllegalArgumentException("Selecciona una tarea para editar.");
        }

        validarTarea(tarea);

        dao.actualizarTarea(tarea);
    }

    public void eliminarTarea(ObjectId id) {

        if (id == null) {
            throw new IllegalArgumentException("Selecciona una tarea para eliminar.");
        }

        dao.eliminarTarea(id);
    }

    private void validarTarea(Tarea tarea) {

        if (tarea == null) {
            throw new IllegalArgumentException(
                    "Los datos de la tarea son obligatorios.");
        }

        if (tarea.getTitulo() == null || tarea.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El título de la tarea es obligatorio.");
        }

        if (tarea.getProfesorId() == null) {
            throw new IllegalArgumentException("Debes seleccionar un profesor.");
        }

        if (tarea.getCursoId() == null) {
            throw new IllegalArgumentException("Debes seleccionar un curso.");
        }
    }

    // ===================== USUARIO =====================

    public void registrarUsuario(Usuario usuario) {

        validarUsuario(usuario);

        usuario.setRol("USUARIO");
        usuario.setEmpleadoId(null);

        Usuario existente = dao.buscarUsuarioPorEmail(usuario.getEmail().trim());

        if (existente != null) {
            throw new IllegalArgumentException("Este email ya está registrado.");
        }

        String passwordProtegida = PasswordUtil.generarHash(usuario.getPassword());
        usuario.setPassword(passwordProtegida);

        dao.insertarUsuario(usuario);
    }

    public void registrarCuentaEmpleado(Usuario usuario) {

        validarUsuario(usuario);

        if (usuario.getEmpleadoId() == null) {
            throw new IllegalArgumentException(
                    "Debes relacionar el profesor con un empleado.");
        }

        Usuario existente = dao.buscarUsuarioPorEmail(usuario.getEmail().trim());

        if (existente != null) {
            throw new IllegalArgumentException("Este email ya está registrado.");
        }

        usuario.setRol("EMPLEADO");

        String passwordProtegida = PasswordUtil.generarHash(usuario.getPassword());
        usuario.setPassword(passwordProtegida);

        dao.registrarCuentaEmpleado(usuario);
    }

    public Usuario iniciarSesion(String email, String password) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio.");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }

        Usuario usuario = dao.buscarUsuarioPorEmail(email.trim());

        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no existe.");
        }

        String passwordHash = PasswordUtil.generarHash(password);

        if (!passwordHash.equals(usuario.getPassword())) {
            throw new IllegalArgumentException("La contraseña es incorrecta.");
        }

        return usuario;
    }

    public List<Usuario> listarUsuarios() {
        return dao.listarUsuarios();
    }

    public Usuario buscarUsuarioPorId(ObjectId id) {

        if (id == null) {
            throw new IllegalArgumentException("El ID del usuario es obligatorio.");
        }

        return dao.buscarUsuarioPorId(id);
    }

    public boolean modificarUsuario(Usuario usuario) {

        if (usuario == null || usuario.getId() == null) {
            throw new IllegalArgumentException(
                    "El usuario y su ID son obligatorios.");
        }

        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }

        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio.");
        }

        return dao.modificarUsuario(usuario);
    }

    private void validarUsuario(Usuario usuario) {

        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede estar vacío.");
        }

        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }

        if (usuario.getEmail() == null
                || usuario.getEmail().isBlank()
                || !usuario.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("El email no es válido.");
        }

        if (usuario.getPassword() == null || usuario.getPassword().length() < 6) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos 6 caracteres.");
        }

        if (usuario.getTelefono() == null || usuario.getTelefono().isBlank()) {
            throw new IllegalArgumentException("El teléfono es obligatorio.");
        }
    }

    public boolean eliminarUsuario(ObjectId id) {

        if (id == null) {
            throw new IllegalArgumentException("Debes seleccionar un usuario.");
        }

        return dao.eliminarUsuario(id);
    }

    // ===================== VALORACION =====================

    public boolean registrarValoracion(Valoracion valoracion) {

        if (valoracion == null) {
            return false;
        }

        if (valoracion.getUsuarioId() == null || valoracion.getCursoId() == null) {
            return false;
        }

        if (valoracion.getPuntuacion() < 1 || valoracion.getPuntuacion() > 5) {
            return false;
        }

        if (valoracion.getComentario() == null) {
            valoracion.setComentario("");
        }

        dao.insertarValoracion(valoracion);

        return true;
    }

    public List<Valoracion> listarValoraciones() {
        return dao.listarValoraciones();
    }

    public List<Valoracion> listarValoracionesPorCurso(ObjectId cursoId) {
        return dao.listarValoracionesPorCurso(cursoId);
    }

    public List<Valoracion> listarValoracionesPorUsuario(ObjectId usuarioId) {
        return dao.listarValoracionesPorUsuario(usuarioId);
    }

    // ===================== COMPARTIDO =====================

    public void cerrar() {
        dao.cerrar();
    }
}
