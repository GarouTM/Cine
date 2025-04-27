package dao;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import dao.Interface.IUsuarioDAO;
import dao.mongo.MongoDBConnection;
import modelo.Usuario;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
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
            // La validación de existencia de usuario se delega a la lógica de negocio
            Document doc = crearDocumentoUsuario(usuario);
            collection.insertOne(doc);
            System.out.println("Cuenta creada correctamente para: " + usuario.getGmail());
        } catch (MongoException e) {
            throw new RuntimeException("Error de conexión con la base de datos: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear la cuenta: " + e.getMessage(), e);
        }
    }

    private Document crearDocumentoUsuario(Usuario usuario) {
        return new Document("_id", usuario.getGmail())
                .append("gmailPassword", usuario.getGmailPassword())
                .append("nombre", usuario.getNombre())
                .append("apellido", usuario.getApellido())
                .append("saldo", usuario.getSaldo())
                .append("fechaCreacion", new Date());
    }

    @Override
    public void eliminarUsuario(String gmail) {
        try {
            DeleteResult resultado = collection.deleteOne(Filters.eq("_id", gmail));
            if (resultado.getDeletedCount() == 0) {
                throw new RuntimeException("No se encontró ninguna cuenta con el Gmail: " + gmail);
            }
            System.out.println("Cuenta eliminada correctamente: " + gmail);
        } catch (MongoException e) {
            throw new RuntimeException("Error de conexión con la base de datos: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la cuenta: " + e.getMessage(), e);
        }
    }

    @Override
    public Usuario login(String gmail, String password) {
        try {
            Document doc = buscarUsuario(gmail, password);
            return doc != null ? documentToUsuario(doc) : null;
        } catch (MongoException e) {
            throw new RuntimeException("Error de conexión con la base de datos: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error en el proceso de login: " + e.getMessage(), e);
        }
    }

    private Document buscarUsuario(String gmail, String password) {
        if (password == null) {
            return collection.find(Filters.eq("_id", gmail)).first();
        }
        return collection.find(Filters.and(
                Filters.eq("_id", gmail),
                Filters.eq("gmailPassword", password)
        )).first();
    }

    @Override
    public void actualizarSaldo(String gmail, double nuevoSaldo) {
        try {
            // La validación del saldo negativo se delega a la lógica de negocio
            Document resultado = collection.findOneAndUpdate(
                    Filters.eq("_id", gmail),
                    Updates.set("saldo", nuevoSaldo)
            );

            if (resultado == null) {
                throw new RuntimeException("No se encontró la cuenta con Gmail: " + gmail);
            }

            System.out.println("Saldo actualizado correctamente para: " + gmail);
        } catch (MongoException e) {
            throw new RuntimeException("Error de conexión con la base de datos: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el saldo: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Usuario> obtenerTodosUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        try {
            collection.find().forEach(doc -> usuarios.add(documentToUsuario(doc)));
            return usuarios;
        } catch (MongoException e) {
            throw new RuntimeException("Error de conexión con la base de datos: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener la lista de usuarios: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existeUsuario(String gmail) {
        try {
            return collection.find(Filters.eq("_id", gmail)).first() != null;
        } catch (MongoException e) {
            throw new RuntimeException("Error de conexión al verificar existencia del usuario: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar existencia del usuario: " + e.getMessage(), e);
        }
    }

    private Usuario documentToUsuario(Document doc) {
        return new Usuario(
                doc.getString("_id"),
                doc.getString("gmailPassword"),
                doc.getString("nombre"),
                doc.getString("apellido"),
                doc.getDouble("saldo")
        );
    }
}