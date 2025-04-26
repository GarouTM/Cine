package dao;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import dao.Interface.IUsuarioDAO;
import dao.mongo.MongoDBConnection;
import modelo.Usuario;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOImpl implements IUsuarioDAO {
    private final MongoCollection<Document> collection;

    public UsuarioDAOImpl() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        this.collection = database.getCollection("Usuario");
    }

    @Override
    public void crearUsuario(Usuario usuario) {
        try {
            // Verificar si ya existe un usuario con ese gmail
            if (existeUsuario(usuario.getGmail())) {
                throw new IllegalArgumentException("Ya existe una cuenta con ese Gmail: " + usuario.getGmail());
            }

            // Crear el documento para MongoDB
            Document doc = new Document("_id", usuario.getGmail())
                    .append("gmailPassword", usuario.getGmailPassword())
                    .append("nombre", usuario.getNombre())
                    .append("apellido", usuario.getApellido())
                    .append("saldo", usuario.getSaldo());

            // Insertar el documento
            collection.insertOne(doc);
            System.out.println("Cuenta creada correctamente para: " + usuario.getGmail());
        } catch (Exception e) {
            throw new RuntimeException("Error al crear la cuenta: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminarUsuario(String gmail) {
        try {
            DeleteResult resultado = collection.deleteOne(Filters.eq("_id", gmail));
            if (resultado.getDeletedCount() == 0) {
                throw new RuntimeException("No se encontró ninguna cuenta con el Gmail: " + gmail);
            }
            System.out.println("Cuenta eliminada correctamente: " + gmail);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la cuenta: " + e.getMessage(), e);
        }
    }

    @Override
    public Usuario login(String gmail, String password) {
        try {
            Document doc = collection.find(Filters.and(
                    Filters.eq("_id", gmail),
                    Filters.eq("gmailPassword", password)
            )).first();

            if (doc != null) {
                return new Usuario(
                        doc.getString("_id"),
                        doc.getString("gmailPassword"),
                        doc.getString("nombre"),
                        doc.getString("apellido"),
                        doc.getDouble("saldo")
                );
            }
            return null; // Retorna null si las credenciales son inválidas
        } catch (Exception e) {
            throw new RuntimeException("Error en el proceso de login: " + e.getMessage(), e);
        }
    }

    @Override
    public void actualizarSaldo(String gmail, double nuevoSaldo) {
        try {
            if (nuevoSaldo < 0) {
                throw new IllegalArgumentException("El saldo no puede ser negativo");
            }

            Document updateDoc = new Document("$set", new Document("saldo", nuevoSaldo));
            Document resultado = collection.findOneAndUpdate(
                    Filters.eq("_id", gmail),
                    updateDoc
            );

            if (resultado == null) {
                throw new RuntimeException("No se encontró la cuenta con Gmail: " + gmail);
            }

            System.out.println("Saldo actualizado correctamente para: " + gmail);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el saldo: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Usuario> obtenerTodosUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        try {
            for (Document doc : collection.find()) {
                Usuario usuario = new Usuario(
                        doc.getString("_id"),
                        doc.getString("gmailPassword"),
                        doc.getString("nombre"),
                        doc.getString("apellido"),
                        doc.getDouble("saldo")
                );
                usuarios.add(usuario);
            }
            return usuarios;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener la lista de usuarios: " + e.getMessage(), e);
        }
    }

    private boolean existeUsuario(String gmail) {
        try {
            return collection.find(Filters.eq("_id", gmail)).first() != null;
        } catch (Exception e) {
            System.err.println("Error al verificar existencia del usuario: " + e.getMessage());
            return false;
        }
    }
}