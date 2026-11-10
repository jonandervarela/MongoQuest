package com.mycompany.reto1_erandio_academy;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import org.bson.Document;

public class CrearBaseDatos {

    private static final String URI = "mongodb://localhost:27017";

    public static void main(String[] args) {

        try (MongoClient cliente = MongoClients.create(URI)) {

            MongoDatabase db = cliente.getDatabase("Academy");

            // 1. Usuarios: contiene un perfil embebido
            crearColeccion(db, "usuarios", """
                {
                  "$jsonSchema": {
                    "bsonType": "object",
                    "required": ["nombre", "email", "rol", "perfil"],
                    "properties": {
                      "nombre": {"bsonType": "string"},
                      "email": {"bsonType": "string"},
                      "rol": {
                        "bsonType": "string",
                        "enum": ["ADMIN", "EMPLEADO", "USUARIO"]
                      },
                      "perfil": {
                        "bsonType": "object",
                        "properties": {
                          "biografia": {"bsonType": "string"},
                          "ciudad": {"bsonType": "string"}
                        }
                      }
                    }
                  }
                }
                """);

            // 2. Empleados
            crearColeccion(db, "empleados", """
                {
                  "$jsonSchema": {
                    "bsonType": "object",
                    "required": ["nombre", "puesto", "departamento"],
                    "properties": {
                      "nombre": {"bsonType": "string"},
                      "puesto": {"bsonType": "string"},
                      "departamento": {"bsonType": "string"},
                      "activo": {"bsonType": "bool"}
                    }
                  }
                }
                """);

            // 3. Actividades: contiene ejercicios embebidos
            crearColeccion(db, "actividades", """
                {
                  "$jsonSchema": {
                    "bsonType": "object",
                    "required": ["titulo", "cursoId", "ejercicios"],
                    "properties": {
                      "titulo": {"bsonType": "string"},
                      "cursoId": {"bsonType": "objectId"},
                      "ejercicios": {
                        "bsonType": "array",
                        "items": {
                          "bsonType": "object",
                          "required": ["pregunta"],
                          "properties": {
                            "pregunta": {"bsonType": "string"}
                          }
                        }
                      }
                    }
                  }
                }
                """);

            // 4. Cursos: array de IDs de actividades
            crearColeccion(db, "cursos", """
                {
                  "$jsonSchema": {
                    "bsonType": "object",
                    "required": ["titulo", "actividadIds"],
                    "properties": {
                      "titulo": {"bsonType": "string"},
                      "descripcion": {"bsonType": "string"},
                      "actividadIds": {
                        "bsonType": "array",
                        "items": {"bsonType": "objectId"}
                      }
                    }
                  }
                }
                """);

            // 5. Matrículas: relaciona usuarios y cursos
            crearColeccion(db, "matriculas", """
                {
                  "$jsonSchema": {
                    "bsonType": "object",
                    "required": ["usuarioId", "cursoId", "progreso"],
                    "properties": {
                      "usuarioId": {"bsonType": "objectId"},
                      "cursoId": {"bsonType": "objectId"},
                      "progreso": {
                        "bsonType": "int",
                        "minimum": 0,
                        "maximum": 100
                      }
                    }
                  }
                }
                """);

            // 6. Tareas: relaciona tareas con empleados
            crearColeccion(db, "tareas", """
                {
                  "$jsonSchema": {
                    "bsonType": "object",
                    "required": ["titulo", "empleadoId", "estado"],
                    "properties": {
                      "titulo": {"bsonType": "string"},
                      "empleadoId": {"bsonType": "objectId"},
                      "estado": {
                        "bsonType": "string",
                        "enum": ["PENDIENTE", "EN_PROCESO", "COMPLETADA"]
                      },
                      "fechaLimite": {"bsonType": "string"}
                    }
                  }
                }
                """);

            // 7. Logs: registra las operaciones
            crearColeccion(db, "logs", """
                {
                  "$jsonSchema": {
                    "bsonType": "object",
                    "required": [
                      "fecha", "usuario", "tipoOperacion", "error"
                    ],
                    "properties": {
                      "fecha": {"bsonType": "date"},
                      "usuario": {"bsonType": "string"},
                      "tipoOperacion": {"bsonType": "string"},
                      "error": {
                        "bsonType": ["string", "null"]
                      }
                    }
                  }
                }
                """);

            System.out.println("Base de datos Academy preparada.");
            System.out.println("Colecciones y validaciones comprobadas:");

            for (String nombre : db.listCollectionNames()) {
                System.out.println("- " + nombre);
            }

        } catch (Exception e) {
            System.out.println("Error al preparar la base de datos:");
            e.printStackTrace();
        }
    }

   private static void crearColeccion(
        MongoDatabase db,
        String nombre,
        String esquema) {

    boolean existe = db.listCollectionNames()
            .into(new java.util.ArrayList<>())
            .contains(nombre);

    if (existe) {
        System.out.println("Ya existe: " + nombre);
        return;
    }

    Document opciones = new Document(
            "validator",
            Document.parse(esquema)
    );

    db.runCommand(
            new Document("create", nombre)
                    .append("validator", Document.parse(esquema))
    );

    System.out.println("Creada: " + nombre);
}
}