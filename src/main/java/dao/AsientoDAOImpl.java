package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import dao.mongo.MongoDBConnection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class AsientoDAOImpl {

    private final MongoCollection<Document> collection;

    public AsientoDAOImpl() {
        MongoDatabase database = MongoDBConnection.getDatabase(); // Conexión a la base de datos
        this.collection = database.getCollection("Asientos"); // Colección "Asientos"
    }

    /**
     * Registra los asientos ocupados para una película y un horario específicos.
     *
     * @param pelicula        Nombre de la película.
     * @param horario         Horario de la película.
     * @param asientosOcupados Lista de asientos ocupados.
     */
    public void registrarAsientos(String pelicula, String horario, List<String> asientosOcupados) {
        try {
            // Buscar si ya existe un registro para esta película y horario
            Document doc = collection.find(Filters.and(
                    Filters.eq("pelicula", pelicula),
                    Filters.eq("horario", horario)
            )).first();

            if (doc == null) {
                // Si no existe, crear un nuevo documento
                Document nuevoDoc = new Document("pelicula", pelicula)
                        .append("horario", horario)
                        .append("asientosOcupados", asientosOcupados)
                        .append("fechaActualizacion", System.currentTimeMillis());
                collection.insertOne(nuevoDoc);
            } else {
                // Si ya existe, actualizar los asientos ocupados
                List<String> asientosExistentes = doc.getList("asientosOcupados", String.class);
                asientosExistentes.addAll(asientosOcupados); // Combinar listas
                collection.updateOne(
                        Filters.and(
                                Filters.eq("pelicula", pelicula),
                                Filters.eq("horario", horario)
                        ),
                        Updates.combine(
                                Updates.set("asientosOcupados", asientosExistentes),
                                Updates.set("fechaActualizacion", System.currentTimeMillis())
                        )
                );
            }
        } catch (Exception e) {
            System.err.println("Error al registrar asientos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Obtiene los asientos ocupados para una película y un horario específicos.
     *
     * @param pelicula Nombre de la película.
     * @param horario  Horario de la película.
     * @return Lista de asientos ocupados.
     */
    public List<String> obtenerAsientosOcupados(String pelicula, String horario) {
        try {
            Document doc = collection.find(Filters.and(
                    Filters.eq("pelicula", pelicula),
                    Filters.eq("horario", horario)
            )).first();

            if (doc != null) {
                return doc.getList("asientosOcupados", String.class);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener asientos ocupados: " + e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>(); // Retornar lista vacía si no hay datos
    }
}