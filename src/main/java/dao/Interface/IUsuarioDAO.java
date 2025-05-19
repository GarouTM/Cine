package dao.Interface;

import modelo.Usuario;
import java.util.List;

public interface IUsuarioDAO {

    void crearUsuario(Usuario usuario);

    void eliminarUsuario(String gmail);

    Usuario login(String gmail, String password);

    void actualizarSaldo(String gmail, double nuevoSaldo);

    double obtenerSaldo(String gmail);

    String obtenerContraseña(String gmail);

    List<Usuario> obtenerTodosUsuarios();

    boolean existeUsuario(String gmail);
}