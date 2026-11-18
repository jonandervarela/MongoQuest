package com.mycompany.reto1_erandio_academy.controlador;

import com.mycompany.reto1_erandio_academy.modelo.Actividad;
import com.mycompany.reto1_erandio_academy.modelo.Curso;
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

import java.util.List;

/**
 * DAO unico de la aplicacion: agrupa el acceso a datos de todas las
 * entidades (antes repartido en 11 interfaces independientes).
 *
 * Los metodos se nombran con el prefijo/sufijo de la entidad
 * (p.ej. listarCursos, listarUsuarios) porque varias entidades
 * compartian el mismo nombre de metodo (listar, buscarPorId, eliminar...)
 * y Java no permite sobrecargarlos solo por el tipo de retorno.
 */
public interface DAO {

    // ---------- ACTIVIDAD ----------
    void insertarActividad(Actividad actividad);
    List<Actividad> listarActividades();
    List<Actividad> listarActividadesPorCurso(ObjectId cursoId);
    Actividad buscarActividadPorId(ObjectId id);
    boolean modificarActividad(Actividad actividad);
    boolean eliminarActividad(ObjectId id);

    // ---------- ASIGNACION (profesor <-> curso) ----------
    void asignarProfesor(ObjectId empleadoId, ObjectId cursoId);
    List<Document> listarAsignacionesPorCurso(ObjectId cursoId);
    List<Document> listarAsignacionesPorEmpleado(ObjectId empleadoId);
    boolean quitarProfesor(ObjectId empleadoId, ObjectId cursoId);

    // ---------- CURSO ----------
    void insertarCurso(Curso curso);
    void comprobarCursos();
    List<Curso> listarCursos();
    Curso buscarCursoPorId(ObjectId id);
    boolean modificarCurso(Curso curso);
    boolean eliminarCurso(ObjectId id);

    // ---------- EMPLEADO ----------
    void insertarEmpleado(Empleado empleado);
    List<Empleado> listarEmpleados();
    Empleado buscarEmpleadoPorId(ObjectId id);
    boolean modificarEmpleado(Empleado empleado);
    boolean eliminarEmpleado(ObjectId id);

    // ---------- EMPRESA ----------
    Empresa obtenerEmpresa();
    void actualizarEmpresa(Empresa empresa);

    // ---------- ENTREGA ----------
    void insertarEntrega(Entrega entrega);
    List<Entrega> listarEntregasPorTarea(ObjectId tareaId);
    Entrega buscarEntregaPorTareaYUsuario(ObjectId tareaId, ObjectId usuarioId);
    void actualizarEstadoEntrega(ObjectId id, String estado);
    void actualizarArchivoEntrega(ObjectId id, String nombreArchivo,
            String rutaArchivo, String fechaEntrega);

    // ---------- LOG ----------
    void insertarLog(RegistroLog log);
    List<RegistroLog> listarLogs();

    // ---------- MATRICULA ----------
    void insertarMatricula(Matricula matricula);
    List<Matricula> listarMatriculas();
    List<Matricula> listarMatriculasPorUsuario(ObjectId usuarioId);
    List<Matricula> listarMatriculasPorCurso(ObjectId cursoId);
    boolean existeMatricula(ObjectId usuarioId, ObjectId cursoId);
    boolean eliminarMatricula(ObjectId id);

    // ---------- TAREA ----------
    void insertarTarea(Tarea tarea);
    List<Tarea> listarTareas();
    List<Tarea> listarTareasPorCurso(ObjectId cursoId);
    List<Tarea> listarTareasPorProfesor(ObjectId profesorId);
    void actualizarTarea(Tarea tarea);
    void eliminarTarea(ObjectId id);

    // ---------- USUARIO ----------
    void insertarUsuario(Usuario usuario);
    void registrarCuentaEmpleado(Usuario usuario);
    List<Usuario> listarUsuarios();
    Usuario buscarUsuarioPorId(ObjectId id);
    Usuario buscarUsuarioPorEmail(String email);
    boolean modificarUsuario(Usuario usuario);
    boolean eliminarUsuario(ObjectId id);

    // ---------- VALORACION ----------
    void insertarValoracion(Valoracion valoracion);
    List<Valoracion> listarValoraciones();
    List<Valoracion> listarValoracionesPorCurso(ObjectId cursoId);
    List<Valoracion> listarValoracionesPorUsuario(ObjectId usuarioId);

    // ---------- COMPARTIDO ----------
    void cerrar();
}
