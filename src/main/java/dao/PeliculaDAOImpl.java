package dao;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import dao.Interface.IPeliculaDAO;
import dao.mongo.MongoDBConnection;
import modelo.Pelicula;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class PeliculaDAOImpl implements IPeliculaDAO {
    private final MongoCollection<Document> collection;

    public PeliculaDAOImpl() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        this.collection = database.getCollection("Pelicula");
    }

    @Override
    public void insertar(Pelicula pelicula) {
        try {
            Document doc = new Document("_id", pelicula.getId())
                    .append("titulo", pelicula.getTitulo())
                    .append("director", pelicula.getDirector())
                    .append("anio", pelicula.getAnio())
                    .append("genero", pelicula.getGenero())
                    .append("duracion", pelicula.getDuracion())
                    .append("sinopsis", pelicula.getSinopsis())
                    .append("rutaImagen", pelicula.getRutaImagen())
                    .append("precio", pelicula.getPrecio());

            collection.insertOne(doc);
            System.out.println("Película insertada correctamente con ID: " + pelicula.getId());
        } catch (Exception e) {
            throw new RuntimeException("Error al insertar película: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Pelicula> obtenerTodas() {
        List<Pelicula> peliculas = new ArrayList<>();
        try {
            for (Document doc : collection.find()) {
                Pelicula pelicula = documentToPelicula(doc);
                peliculas.add(pelicula);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener películas: " + e.getMessage(), e);
        }
        return peliculas;
    }

    @Override
    public Pelicula obtenerPorId(String id) {
        try {
            Document doc = collection.find(Filters.eq("_id", id)).first();
            return doc != null ? documentToPelicula(doc) : null;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener película por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(String id) {
        try {
            collection.deleteOne(Filters.eq("_id", id));
            System.out.println("Película eliminada correctamente: " + id);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar película: " + e.getMessage(), e);
        }
    }

    @Override
    public void modificar(Pelicula pelicula) {
        try {
            Document doc = new Document("_id", pelicula.getId())
                    .append("titulo", pelicula.getTitulo())
                    .append("director", pelicula.getDirector())
                    .append("anio", pelicula.getAnio())
                    .append("genero", pelicula.getGenero())
                    .append("duracion", pelicula.getDuracion())
                    .append("sinopsis", pelicula.getSinopsis())
                    .append("rutaImagen", pelicula.getRutaImagen())
                    .append("precio", pelicula.getPrecio());

            collection.replaceOne(Filters.eq("_id", pelicula.getId()), doc);
            System.out.println("Película modificada correctamente: " + pelicula.getId());
        } catch (MongoException e) {
            throw new RuntimeException("Error al modificar película: " + e.getMessage(), e);
        }
    }

    @Override
    public String generarSiguienteId() {
        try {
            int maxNum = 0;
            for (Document doc : collection.find()) {
                String id = doc.getString("_id");
                if (id.matches("PEL\\d{3}")) {
                    int num = Integer.parseInt(id.substring(3));
                    maxNum = Math.max(maxNum, num);
                }
            }
            return String.format("PEL%03d", maxNum + 1);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar siguiente ID: " + e.getMessage(), e);
        }
    }

    private Pelicula documentToPelicula(Document doc) {
        return new Pelicula(
                doc.getString("_id"),
                doc.getString("titulo"),
                doc.getString("director"),
                doc.getInteger("anio"),
                doc.getString("genero"),
                doc.getDouble("duracion"),
                doc.getString("sinopsis"),
                doc.getString("rutaImagen"),
                doc.getDouble("precio")
        );
    }
}