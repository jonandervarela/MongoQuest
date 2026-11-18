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

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion unica del DAO: antes habia 11 clases XxxDAOImpl,
 * cada una abriendo su propia conexion a MongoDB. Ahora se abre
 * una unica conexion (MongoClient/MongoDatabase) compartida por
 * todas las colecciones, y se cierra con un unico metodo cerrar().
 */
public class DAOImpl implements DAO {

    private final MongoClient cliente;
    private final MongoDatabase baseDatos;

    private final MongoCollection<Document> coleccionActividades;
    private final MongoCollection<Document> coleccionAsignaciones;
    private final MongoCollection<Document> coleccionCursos;
    private final MongoCollection<Document> coleccionEmpleados;
    private final MongoCollection<Document> coleccionEmpresa;
    private final MongoCollection<Document> coleccionEntregas;
    private final MongoCollection<Document> coleccionLogs;
    private final MongoCollection<Document> coleccionMatriculas;
    private final MongoCollection<Document> coleccionTareas;
    private final MongoCollection<Document> coleccionUsuarios;
    private final MongoCollection<Document> coleccionValoraciones;

    public DAOImpl() {

        cliente = MongoClients.create("mongodb://localhost:27017");
        baseDatos = cliente.getDatabase("Academy");

        coleccionActividades = baseDatos.getCollection("actividades");
        coleccionAsignaciones = baseDatos.getCollection("asignaciones");
        coleccionCursos = baseDatos.getCollection("cursos");
        coleccionEmpleados = baseDatos.getCollection("empleados");
        coleccionEmpresa = baseDatos.getCollection("empresa");
        coleccionEntregas = baseDatos.getCollection("entregas");
        coleccionLogs = baseDatos.getCollection("logs");
        coleccionMatriculas = baseDatos.getCollection("matriculas");
        coleccionTareas = baseDatos.getCollection("tareas");
        coleccionUsuarios = baseDatos.getCollection("usuarios");
        coleccionValoraciones = baseDatos.getCollection("valoraciones");

        System.out.println("========== DIAGNOSTICO CURSOS ==========");
        System.out.println("BASE DE DATOS: " + baseDatos.getName());
        System.out.println("COLECCION: " + coleccionCursos.getNamespace());
        System.out.println("CURSOS EN MONGODB: " + coleccionCursos.countDocuments());
        System.out.println("========================================");
    }

    // ===================== ACTIVIDAD =====================

    public void insertarActividad(Actividad actividad) {

        Document documento = new Document()
                .append("titulo", actividad.getTitulo())
                .append("descripcion", actividad.getDescripcion())
                .append("cursoId", actividad.getCursoId())
                .append("ejercicios", ejerciciosADocumentos(actividad.getEjercicios()));

        coleccionActividades.insertOne(documento);

        actividad.setId(documento.getObjectId("_id"));

        System.out.println("ACTIVIDAD REGISTRADA CORRECTAMENTE");
    }

    public List<Actividad> listarActividades() {

        List<Actividad> actividades = new ArrayList<>();

        for (Document documento : coleccionActividades.find()) {
            actividades.add(convertirActividad(documento));
        }

        return actividades;
    }

    public List<Actividad> listarActividadesPorCurso(ObjectId cursoId) {

        List<Actividad> actividades = new ArrayList<>();

        for (Document documento : coleccionActividades.find(
                Filters.eq("cursoId", cursoId))) {

            actividades.add(convertirActividad(documento));
        }

        return actividades;
    }

    public Actividad buscarActividadPorId(ObjectId id) {

        Document documento = coleccionActividades.find(
                Filters.eq("_id", id)
        ).first();

        if (documento == null) {
            return null;
        }

        return convertirActividad(documento);
    }

    public boolean modificarActividad(Actividad actividad) {

        if (actividad == null || actividad.getId() == null) {
            return false;
        }

        return coleccionActividades.updateOne(
                Filters.eq("_id", actividad.getId()),
                Updates.combine(
                        Updates.set("titulo", actividad.getTitulo()),
                        Updates.set("descripcion", actividad.getDescripcion()),
                        Updates.set("cursoId", actividad.getCursoId()),
                        Updates.set("ejercicios",
                                ejerciciosADocumentos(actividad.getEjercicios()))
                )
        ).getModifiedCount() > 0;
    }

    public boolean eliminarActividad(ObjectId id) {

        if (id == null) {
            return false;
        }

        return coleccionActividades.deleteOne(
                Filters.eq("_id", id)
        ).getDeletedCount() > 0;
    }

    private List<Document> ejerciciosADocumentos(List<Ejercicio> ejercicios) {

        List<Document> documentos = new ArrayList<>();

        if (ejercicios == null) {
            return documentos;
        }

        for (Ejercicio ejercicio : ejercicios) {
            documentos.add(new Document("pregunta", ejercicio.getPregunta()));
        }

        return documentos;
    }

    @SuppressWarnings("unchecked")
    private List<Ejercicio> documentosAEjercicios(Document documento) {

        List<Ejercicio> ejercicios = new ArrayList<>();

        List<Document> lista = (List<Document>) documento.get("ejercicios");

        if (lista == null) {
            return ejercicios;
        }

        for (Document item : lista) {
            ejercicios.add(new Ejercicio(item.getString("pregunta")));
        }

        return ejercicios;
    }

    private Actividad convertirActividad(Document documento) {

        return new Actividad(
                documento.getObjectId("_id"),
                documento.getString("titulo"),
                documento.getString("descripcion"),
                documento.getObjectId("cursoId"),
                documentosAEjercicios(documento)
        );
    }

    // ===================== ASIGNACION =====================

    public void asignarProfesor(ObjectId empleadoId, ObjectId cursoId) {

        Document documento = new Document()
                .append("empleadoId", empleadoId)
                .append("cursoId", cursoId);

        coleccionAsignaciones.insertOne(documento);
    }

    public List<Document> listarAsignacionesPorCurso(ObjectId cursoId) {

        List<Document> asignaciones = new ArrayList<>();

        for (Document documento : coleccionAsignaciones.find(
                Filters.eq("cursoId", cursoId))) {

            asignaciones.add(documento);
        }

        return asignaciones;
    }

    public List<Document> listarAsignacionesPorEmpleado(ObjectId empleadoId) {

        List<Document> asignaciones = new ArrayList<>();

        for (Document documento : coleccionAsignaciones.find(
                Filters.eq("empleadoId", empleadoId))) {

            asignaciones.add(documento);
        }

        return asignaciones;
    }

    public boolean quitarProfesor(ObjectId empleadoId, ObjectId cursoId) {

        return coleccionAsignaciones.deleteOne(
                Filters.and(
                        Filters.eq("empleadoId", empleadoId),
                        Filters.eq("cursoId", cursoId)
                )
        ).getDeletedCount() > 0;
    }

    // ===================== CURSO =====================

    public void insertarCurso(Curso curso) {

        Document documento = new Document()
                .append("titulo", curso.getNombre())
                .append("descripcion", curso.getDescripcion())
                .append("duracion", curso.getDuracion())
                .append("activo", curso.isActivo())
                .append("actividadIds", new ArrayList<ObjectId>());

        coleccionCursos.insertOne(documento);

        curso.setId(documento.getObjectId("_id"));

        System.out.println("CURSO INSERTADO CORRECTAMENTE");
        System.out.println("ID DEL CURSO: " + curso.getId());

        comprobarCursos();
    }

    public void comprobarCursos() {

        System.out.println("========== CURSOS GUARDADOS ==========");

        for (Document documento : coleccionCursos.find()) {
            System.out.println(documento.toJson());
        }

        System.out.println("TOTAL DE CURSOS: " + coleccionCursos.countDocuments());
        System.out.println("======================================");
    }

    public List<Curso> listarCursos() {

        List<Curso> cursos = new ArrayList<>();

        for (Document documento : coleccionCursos.find()) {

            Curso curso = new Curso(
                    documento.getObjectId("_id"),
                    documento.getString("titulo"),
                    documento.getString("descripcion"),
                    documento.getString("duracion"),
                    Boolean.TRUE.equals(documento.getBoolean("activo"))
            );

            cursos.add(curso);
        }

        return cursos;
    }

    public Curso buscarCursoPorId(ObjectId id) {

        Document documento = coleccionCursos.find(
                Filters.eq("_id", id)
        ).first();

        if (documento == null) {
            return null;
        }

        return new Curso(
                documento.getObjectId("_id"),
                documento.getString("titulo"),
                documento.getString("descripcion"),
                documento.getString("duracion"),
                Boolean.TRUE.equals(documento.getBoolean("activo"))
        );
    }

    public boolean modificarCurso(Curso curso) {

        if (curso.getId() == null) {
            return false;
        }

        return coleccionCursos.updateOne(
                Filters.eq("_id", curso.getId()),
                Updates.combine(
                        Updates.set("titulo", curso.getNombre()),
                        Updates.set("descripcion", curso.getDescripcion()),
                        Updates.set("duracion", curso.getDuracion()),
                        Updates.set("activo", curso.isActivo())
                )
        ).getModifiedCount() > 0;
    }

    public boolean eliminarCurso(ObjectId id) {

        if (id == null) {
            return false;
        }

        return coleccionCursos.deleteOne(
                Filters.eq("_id", id)
        ).getDeletedCount() > 0;
    }

    // ===================== EMPLEADO =====================

    public void insertarEmpleado(Empleado empleado) {

        Document documento = new Document()
                .append("nombre", empleado.getNombre())
                .append("puesto", empleado.getPuesto())
                .append("departamento", empleado.getDepartamento())
                .append("activo", empleado.isActivo());

        coleccionEmpleados.insertOne(documento);
        empleado.setId(documento.getObjectId("_id"));
    }

    public List<Empleado> listarEmpleados() {

        List<Empleado> empleados = new ArrayList<>();

        for (Document documento : coleccionEmpleados.find()) {

            Empleado empleado = new Empleado(
                    documento.getObjectId("_id"),
                    documento.getString("nombre"),
                    documento.getString("puesto"),
                    documento.getString("departamento"),
                    Boolean.TRUE.equals(documento.getBoolean("activo"))
            );

            empleados.add(empleado);
        }

        return empleados;
    }

    public Empleado buscarEmpleadoPorId(ObjectId id) {

        Document documento = coleccionEmpleados.find(
                Filters.eq("_id", id)
        ).first();

        if (documento == null) {
            return null;
        }

        return new Empleado(
                documento.getObjectId("_id"),
                documento.getString("nombre"),
                documento.getString("puesto"),
                documento.getString("departamento"),
                Boolean.TRUE.equals(documento.getBoolean("activo"))
        );
    }

    public boolean modificarEmpleado(Empleado empleado) {

        if (empleado.getId() == null) {
            return false;
        }

        return coleccionEmpleados.updateOne(
                Filters.eq("_id", empleado.getId()),
                Updates.combine(
                        Updates.set("nombre", empleado.getNombre()),
                        Updates.set("puesto", empleado.getPuesto()),
                        Updates.set("departamento", empleado.getDepartamento()),
                        Updates.set("activo", empleado.isActivo())
                )
        ).getModifiedCount() > 0;
    }

    public boolean eliminarEmpleado(ObjectId id) {

        if (id == null) {
            return false;
        }

        return coleccionEmpleados.deleteOne(
                Filters.eq("_id", id)
        ).getDeletedCount() > 0;
    }

    // ===================== EMPRESA =====================

    public Empresa obtenerEmpresa() {

        Document documento = coleccionEmpresa.find().first();

        if (documento == null) {
            return null;
        }

        Empresa empresa = new Empresa();

        empresa.setId(documento.getObjectId("_id"));
        empresa.setNombre(documento.getString("nombre"));
        empresa.setSector(documento.getString("sector"));
        empresa.setActividad(documento.getString("actividad"));
        empresa.setDireccion(documento.getString("direccion"));
        empresa.setDescripcion(documento.getString("descripcion"));
        empresa.setEstado(documento.getString("estado"));

        return empresa;
    }

    public void actualizarEmpresa(Empresa empresa) {

        Document datos = new Document()
                .append("nombre", empresa.getNombre())
                .append("sector", empresa.getSector())
                .append("actividad", empresa.getActividad())
                .append("direccion", empresa.getDireccion())
                .append("descripcion", empresa.getDescripcion())
                .append("estado", empresa.getEstado());

        coleccionEmpresa.updateOne(
                new Document("_id", empresa.getId()),
                new Document("$set", datos)
        );
    }

    // ===================== ENTREGA =====================

    public void insertarEntrega(Entrega entrega) {

        Document documento = new Document()
                .append("tareaId", entrega.getTareaId())
                .append("usuarioId", entrega.getUsuarioId())
                .append("estado", entrega.getEstado())
                .append("fechaEntrega", entrega.getFechaEntrega())
                .append("nombreArchivo", entrega.getNombreArchivo())
                .append("rutaArchivo", entrega.getRutaArchivo());

        coleccionEntregas.insertOne(documento);

        entrega.setId(documento.getObjectId("_id"));
    }

    public List<Entrega> listarEntregasPorTarea(ObjectId tareaId) {

        List<Entrega> entregas = new ArrayList<>();

        for (Document documento : coleccionEntregas.find(
                Filters.eq("tareaId", tareaId))) {

            entregas.add(convertirEntrega(documento));
        }

        return entregas;
    }

    public Entrega buscarEntregaPorTareaYUsuario(ObjectId tareaId, ObjectId usuarioId) {

        Document documento = coleccionEntregas.find(
                Filters.and(
                        Filters.eq("tareaId", tareaId),
                        Filters.eq("usuarioId", usuarioId)
                )
        ).first();

        if (documento == null) {
            return null;
        }

        return convertirEntrega(documento);
    }

    public void actualizarEstadoEntrega(ObjectId id, String estado) {

        coleccionEntregas.updateOne(
                Filters.eq("_id", id),
                new Document("$set",
                        new Document("estado", estado))
        );
    }

    public void actualizarArchivoEntrega(ObjectId id, String nombreArchivo,
            String rutaArchivo, String fechaEntrega) {

        Document cambios = new Document()
                .append("nombreArchivo", nombreArchivo)
                .append("rutaArchivo", rutaArchivo)
                .append("fechaEntrega", fechaEntrega)
                .append("estado", "ENTREGADA");

        coleccionEntregas.updateOne(
                Filters.eq("_id", id),
                new Document("$set", cambios)
        );
    }

    private Entrega convertirEntrega(Document documento) {

        return new Entrega(
                documento.getObjectId("_id"),
                documento.getObjectId("tareaId"),
                documento.getObjectId("usuarioId"),
                documento.getString("estado"),
                documento.getString("fechaEntrega"),
                documento.getString("nombreArchivo"),
                documento.getString("rutaArchivo")
        );
    }

    // ===================== LOG =====================

    public void insertarLog(RegistroLog log) {

        Document documento = new Document()
                .append("fecha", log.getFecha())
                .append("usuario", log.getUsuario())
                .append("tipoOperacion", log.getOperacion())
                .append("coleccion", log.getColeccion())
                .append("resultado", log.getResultado())
                .append("error", log.getError());

        coleccionLogs.insertOne(documento);

        log.setId(documento.getObjectId("_id"));
    }

    public List<RegistroLog> listarLogs() {

        List<RegistroLog> registros = new ArrayList<>();

        for (Document documento : coleccionLogs.find()) {

            RegistroLog log = new RegistroLog(
                    documento.getDate("fecha"),
                    documento.getString("usuario"),
                    documento.getString("tipoOperacion"),
                    documento.getString("coleccion"),
                    documento.getString("resultado"),
                    documento.getString("error")
            );

            log.setId(documento.getObjectId("_id"));

            registros.add(log);
        }

        return registros;
    }

    // ===================== MATRICULA =====================

    public void insertarMatricula(Matricula matricula) {

        Document documento = new Document()
                .append("usuarioId", matricula.getUsuarioId())
                .append("cursoId", matricula.getCursoId())
                .append("fecha", matricula.getFecha())
                .append("estado", matricula.getEstado())
                .append("progreso", 0);

        coleccionMatriculas.insertOne(documento);

        matricula.setId(documento.getObjectId("_id"));

        System.out.println("MATRÍCULA REGISTRADA CORRECTAMENTE");
    }

    public List<Matricula> listarMatriculas() {

        List<Matricula> matriculas = new ArrayList<>();

        for (Document documento : coleccionMatriculas.find()) {
            matriculas.add(convertirMatricula(documento));
        }

        return matriculas;
    }

    public List<Matricula> listarMatriculasPorUsuario(ObjectId usuarioId) {

        List<Matricula> matriculas = new ArrayList<>();

        for (Document documento : coleccionMatriculas.find(
                Filters.eq("usuarioId", usuarioId))) {

            matriculas.add(convertirMatricula(documento));
        }

        return matriculas;
    }

    public List<Matricula> listarMatriculasPorCurso(ObjectId cursoId) {

        List<Matricula> matriculas = new ArrayList<>();

        for (Document documento : coleccionMatriculas.find(
                Filters.eq("cursoId", cursoId))) {

            matriculas.add(convertirMatricula(documento));
        }

        return matriculas;
    }

    public boolean existeMatricula(ObjectId usuarioId, ObjectId cursoId) {

        Document documento = coleccionMatriculas.find(
                Filters.and(
                        Filters.eq("usuarioId", usuarioId),
                        Filters.eq("cursoId", cursoId),
                        Filters.eq("estado", "ACTIVA")
                )
        ).first();

        return documento != null;
    }

    public boolean eliminarMatricula(ObjectId id) {

        if (id == null) {
            return false;
        }

        return coleccionMatriculas.deleteOne(
                Filters.eq("_id", id)
        ).getDeletedCount() > 0;
    }

    private Matricula convertirMatricula(Document documento) {

        return new Matricula(
                documento.getObjectId("_id"),
                documento.getObjectId("usuarioId"),
                documento.getObjectId("cursoId"),
                documento.getString("fecha"),
                documento.getString("estado"),
                documento.getInteger("progreso", 0)
        );
    }

    // ===================== TAREA =====================

    public void insertarTarea(Tarea tarea) {

        Document documento = new Document()
                .append("titulo", tarea.getTitulo())
                .append("empleadoId", tarea.getProfesorId())
                .append("cursoId", tarea.getCursoId())
                .append("fechaLimite", tarea.getFechaLimite());

        coleccionTareas.insertOne(documento);

        tarea.setId(documento.getObjectId("_id"));
    }

    public List<Tarea> listarTareas() {

        List<Tarea> tareas = new ArrayList<>();

        for (Document documento : coleccionTareas.find()) {
            tareas.add(convertirTarea(documento));
        }

        return tareas;
    }

    public List<Tarea> listarTareasPorCurso(ObjectId cursoId) {

        List<Tarea> tareas = new ArrayList<>();

        for (Document documento :
                coleccionTareas.find(Filters.eq("cursoId", cursoId))) {

            tareas.add(convertirTarea(documento));
        }

        return tareas;
    }

    public List<Tarea> listarTareasPorProfesor(ObjectId profesorId) {

        List<Tarea> tareas = new ArrayList<>();

        Bson filtro = Filters.or(
                Filters.eq("empleadoId", profesorId),
                Filters.eq("profesorId", profesorId)
        );

        for (Document documento : coleccionTareas.find(filtro)) {
            tareas.add(convertirTarea(documento));
        }

        return tareas;
    }

    private Tarea convertirTarea(Document documento) {

        ObjectId empleadoId = documento.getObjectId("empleadoId");

        if (empleadoId == null) {
            empleadoId = documento.getObjectId("profesorId");
        }

        return new Tarea(
                documento.getObjectId("_id"),
                documento.getString("titulo"),
                empleadoId,
                documento.getObjectId("cursoId"),
                documento.getString("fechaLimite")
        );
    }

    public void actualizarTarea(Tarea tarea) {

        Document cambios = new Document()
                .append("titulo", tarea.getTitulo())
                .append("empleadoId", tarea.getProfesorId())
                .append("cursoId", tarea.getCursoId())
                .append("fechaLimite", tarea.getFechaLimite());

        coleccionTareas.updateOne(
                Filters.eq("_id", tarea.getId()),
                new Document("$set", cambios)
        );
    }

    public void eliminarTarea(ObjectId id) {

        coleccionTareas.deleteOne(
                Filters.eq("_id", id)
        );
    }

    // ===================== USUARIO =====================

    public void insertarUsuario(Usuario usuario) {

        Document documento = new Document()
                .append("nombre", usuario.getNombre())
                .append("email", usuario.getEmail())
                .append("password", usuario.getPassword())
                .append("telefono", usuario.getTelefono())
                .append("rol", "USUARIO")
                .append("empleadoId", null)
                .append("perfil", new Document());

        coleccionUsuarios.insertOne(documento);

        usuario.setId(documento.getObjectId("_id"));
        usuario.setRol("USUARIO");
        usuario.setEmpleadoId(null);

        System.out.println("ALUMNO REGISTRADO CORRECTAMENTE");
    }

    public void registrarCuentaEmpleado(Usuario usuario) {

        if (usuario == null || usuario.getEmpleadoId() == null) {
            throw new IllegalArgumentException(
                    "El profesor debe tener una ficha de empleado.");
        }

        Document documento = new Document()
                .append("nombre", usuario.getNombre())
                .append("email", usuario.getEmail())
                .append("password", usuario.getPassword())
                .append("telefono", usuario.getTelefono())
                .append("rol", "EMPLEADO")
                .append("empleadoId", usuario.getEmpleadoId())
                .append("perfil", new Document());

        coleccionUsuarios.insertOne(documento);

        usuario.setId(documento.getObjectId("_id"));
        usuario.setRol("EMPLEADO");

        System.out.println("PROFESOR REGISTRADO CORRECTAMENTE");
    }

    public List<Usuario> listarUsuarios() {

        List<Usuario> usuarios = new ArrayList<>();

        for (Document documento : coleccionUsuarios.find()) {
            usuarios.add(convertirUsuario(documento));
        }

        return usuarios;
    }

    public Usuario buscarUsuarioPorId(ObjectId id) {

        Document documento = coleccionUsuarios.find(
                Filters.eq("_id", id)
        ).first();

        if (documento == null) {
            return null;
        }

        return convertirUsuario(documento);
    }

    public Usuario buscarUsuarioPorEmail(String email) {

        Document documento = coleccionUsuarios.find(
                Filters.eq("email", email)
        ).first();

        if (documento == null) {
            return null;
        }

        return convertirUsuario(documento);
    }

    private Usuario convertirUsuario(Document documento) {

        Usuario usuario = new Usuario(
                documento.getObjectId("_id"),
                documento.getString("nombre"),
                documento.getString("email"),
                documento.getString("password"),
                documento.getString("telefono")
        );

        usuario.setRol(documento.getString("rol"));

        usuario.setEmpleadoId(
                documento.getObjectId("empleadoId")
        );

        return usuario;
    }

    public boolean modificarUsuario(Usuario usuario) {

        if (usuario == null || usuario.getId() == null) {
            return false;
        }

        Document cambios = new Document()
                .append("nombre", usuario.getNombre())
                .append("email", usuario.getEmail())
                .append("telefono", usuario.getTelefono());

        return coleccionUsuarios.updateOne(
                Filters.eq("_id", usuario.getId()),
                new Document("$set", cambios)
        ).getModifiedCount() > 0;
    }

    public boolean eliminarUsuario(ObjectId id) {

        if (id == null) {
            return false;
        }

        return coleccionUsuarios.deleteOne(
                Filters.eq("_id", id)
        ).getDeletedCount() > 0;
    }

    // ===================== VALORACION =====================

    public void insertarValoracion(Valoracion valoracion) {

        Document documento = new Document()
                .append("usuarioId", valoracion.getUsuarioId())
                .append("cursoId", valoracion.getCursoId())
                .append("puntuacion", valoracion.getPuntuacion())
                .append("comentario", valoracion.getComentario());

        coleccionValoraciones.insertOne(documento);

        valoracion.setId(documento.getObjectId("_id"));

        System.out.println("VALORACIÓN REGISTRADA CORRECTAMENTE");
    }

    public List<Valoracion> listarValoraciones() {

        List<Valoracion> valoraciones = new ArrayList<>();

        for (Document documento : coleccionValoraciones.find()) {
            valoraciones.add(convertirValoracion(documento));
        }

        return valoraciones;
    }

    public List<Valoracion> listarValoracionesPorCurso(ObjectId cursoId) {

        List<Valoracion> valoraciones = new ArrayList<>();

        for (Document documento : coleccionValoraciones.find(
                Filters.eq("cursoId", cursoId))) {

            valoraciones.add(convertirValoracion(documento));
        }

        return valoraciones;
    }

    public List<Valoracion> listarValoracionesPorUsuario(ObjectId usuarioId) {

        List<Valoracion> valoraciones = new ArrayList<>();

        for (Document documento : coleccionValoraciones.find(
                Filters.eq("usuarioId", usuarioId))) {

            valoraciones.add(convertirValoracion(documento));
        }

        return valoraciones;
    }

    private Valoracion convertirValoracion(Document documento) {

        return new Valoracion(
                documento.getObjectId("_id"),
                documento.getObjectId("usuarioId"),
                documento.getObjectId("cursoId"),
                documento.getInteger("puntuacion"),
                documento.getString("comentario")
        );
    }

    // ===================== COMPARTIDO =====================

    public void cerrar() {
        cliente.close();
    }
}
