package Logica_de_Negocio;

import dao.UsuarioDAOImpl;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import modelo.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class LN_Usuarios {
    private final UsuarioDAOImpl usuarioDAO;
    private static final Pattern GMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@gmail\\.com$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{8,}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-zÁáÉéÍíÓóÚúÑñ\\s]{2,}$");

    public LN_Usuarios() {
        this.usuarioDAO = new UsuarioDAOImpl();
    }

    public Usuario login(String gmail, String password) {
        // Validar formato del correo y contraseña
        validarCredenciales(gmail, password);

        try {
            Usuario usuario = usuarioDAO.login(gmail, password);
            if (usuario == null) {
                mostrarAlerta("Error", "Credenciales incorrectas.", Alert.AlertType.ERROR);
                return null;
            }
            mostrarAlerta("Éxito", "Bienvenido " + usuario.getNombreCompleto(), Alert.AlertType.INFORMATION);
            return usuario;
        } catch (Exception e) {
            mostrarAlerta("Error", "Error en el proceso de login: " + e.getMessage(), Alert.AlertType.ERROR);
            return null;
        }
    }

    public void crearUsuario(String gmail, String password, String nombre, String apellido) {
        // Validaciones
        validarDatosUsuario(gmail, password, nombre, apellido);

        try {
            Usuario nuevoUsuario = new Usuario(gmail, password, nombre, apellido, 0.0); // Saldo inicial 0
            usuarioDAO.crearUsuario(nuevoUsuario);
            mostrarAlerta("Éxito", "Cuenta creada correctamente.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al crear la cuenta: " + e.getMessage(), Alert.AlertType.ERROR);
            throw e;
        }
    }

    public void eliminarUsuario(Usuario usuarioSeleccionado) {
        if (usuarioSeleccionado == null) {
            mostrarAlerta("Error", "No se ha seleccionado ninguna cuenta.", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro de que desea eliminar la cuenta de " +
                usuarioSeleccionado.getNombreCompleto() + "?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                usuarioDAO.eliminarUsuario(usuarioSeleccionado.getGmail());
                mostrarAlerta("Éxito", "Cuenta eliminada correctamente.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                mostrarAlerta("Error", "Error al eliminar la cuenta: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    public void actualizarSaldo(String gmail, double cantidad) {
        try {
            Usuario usuario = usuarioDAO.login(gmail, null); // Solo para verificar si existe
            if (usuario == null) {
                mostrarAlerta("Error", "No se encontró la cuenta.", Alert.AlertType.ERROR);
                return;
            }

            double nuevoSaldo = usuario.getSaldo() + cantidad;
            if (nuevoSaldo < 0) {
                mostrarAlerta("Error", "El saldo no puede ser negativo.", Alert.AlertType.ERROR);
                return;
            }

            usuarioDAO.actualizarSaldo(gmail, nuevoSaldo);
            mostrarAlerta("Éxito", "Saldo actualizado correctamente.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al actualizar el saldo: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public List<Usuario> obtenerTodosUsuarios() {
        try {
            return usuarioDAO.obtenerTodosUsuarios();
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al obtener la lista de usuarios: " + e.getMessage(), Alert.AlertType.ERROR);
            return null;
        }
    }

    private void validarCredenciales(String gmail, String password) {
        if (gmail == null || !GMAIL_PATTERN.matcher(gmail).matches()) {
            throw new IllegalArgumentException("El correo debe ser una dirección de Gmail válida");
        }
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }
    }

    private void validarDatosUsuario(String gmail, String password, String nombre, String apellido) {
        validarCredenciales(gmail, password);

        if (nombre == null || !NAME_PATTERN.matcher(nombre).matches()) {
            throw new IllegalArgumentException("El nombre debe contener solo letras y tener al menos 2 caracteres");
        }
        if (apellido == null || !NAME_PATTERN.matcher(apellido).matches()) {
            throw new IllegalArgumentException("El apellido debe contener solo letras y tener al menos 2 caracteres");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}