package Logica_de_Negocio;

import dao.Interface.IUsuarioDAO;
import dao.UsuarioDAOImpl;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import modelo.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class LN_Usuarios {
    private final IUsuarioDAO usuarioDAO;
    private static final Pattern GMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@gmail\\.com$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{8,}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-zÁáÉéÍíÓóÚúÑñ\\s]{2,}$");
    private static final double SALDO_MINIMO = 0.0;

    public LN_Usuarios() {
        this.usuarioDAO = new UsuarioDAOImpl();
    }

    public boolean existeUsuario(String gmail) {
        validarFormatoEmail(gmail);
        try {
            return usuarioDAO.existeUsuario(gmail);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al verificar existencia del usuario: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    public Usuario login(String gmail, String password) {
        try {
            validarCredenciales(gmail, password);

            Usuario usuario = usuarioDAO.login(gmail, password);
            if (usuario == null) {
                mostrarAlerta("Error", "Credenciales incorrectas.", Alert.AlertType.ERROR);
                return null;
            }
            mostrarAlerta("Éxito", "Bienvenido " + usuario.getNombreCompleto(), Alert.AlertType.INFORMATION);
            return usuario;
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Error", e.getMessage(), Alert.AlertType.ERROR);
            return null;
        } catch (Exception e) {
            mostrarAlerta("Error", "Error en el proceso de login: " + e.getMessage(), Alert.AlertType.ERROR);
            return null;
        }
    }

    public void crearUsuario(String gmail, String password, String nombre, String apellido, double saldo) {
        try {
            validarDatosUsuario(gmail, password, nombre, apellido);
            validarSaldo(saldo);

            if (existeUsuario(gmail)) {
                throw new IllegalArgumentException("Ya existe una cuenta con ese email");
            }

            Usuario nuevoUsuario = new Usuario(gmail, password, nombre, apellido, saldo);
            usuarioDAO.crearUsuario(nuevoUsuario);
            mostrarAlerta("Éxito", "Cuenta creada correctamente.", Alert.AlertType.INFORMATION);
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Error", e.getMessage(), Alert.AlertType.ERROR);
            throw e;
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al crear la cuenta: " + e.getMessage(), Alert.AlertType.ERROR);
            throw e;
        }
    }

    public void eliminarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("No se ha seleccionado ninguna cuenta");
        }

        if (confirmarEliminacion(usuario)) {
            try {
                usuarioDAO.eliminarUsuario(usuario.getGmail());
                mostrarAlerta("Éxito", "Cuenta eliminada correctamente.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                mostrarAlerta("Error", "Error al eliminar la cuenta: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    public void actualizarSaldo(String gmail, double cantidad) {
        try {
            validarFormatoEmail(gmail);

            Usuario usuario = usuarioDAO.login(gmail, null);
            if (usuario == null) {
                throw new IllegalArgumentException("No se encontró la cuenta");
            }

            double nuevoSaldo = usuario.getSaldo() + cantidad;
            validarSaldo(nuevoSaldo);

            usuarioDAO.actualizarSaldo(gmail, nuevoSaldo);
            mostrarAlerta("Éxito", "Saldo actualizado correctamente.", Alert.AlertType.INFORMATION);
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Error", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al actualizar el saldo: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public List<Usuario> obtenerTodosUsuarios() {
        try {
            return usuarioDAO.obtenerTodosUsuarios();
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al obtener la lista de usuarios: " + e.getMessage(), Alert.AlertType.ERROR);
            return List.of(); // Retornar lista vacía en lugar de null
        }
    }

    private void validarFormatoEmail(String gmail) {
        if (gmail == null || !GMAIL_PATTERN.matcher(gmail).matches()) {
            throw new IllegalArgumentException("El correo debe ser una dirección de Gmail válida");
        }
    }

    private void validarCredenciales(String gmail, String password) {
        validarFormatoEmail(gmail);
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }
    }

    private void validarDatosUsuario(String gmail, String password, String nombre, String apellido) {
        validarCredenciales(gmail, password);
        validarNombre(nombre, "nombre");
        validarNombre(apellido, "apellido");
    }

    private void validarNombre(String nombre, String campo) {
        if (nombre == null || !NAME_PATTERN.matcher(nombre).matches()) {
            throw new IllegalArgumentException("El " + campo + " debe contener solo letras y tener al menos 2 caracteres");
        }
    }

    private void validarSaldo(double saldo) {
        if (saldo < SALDO_MINIMO) {
            throw new IllegalArgumentException("El saldo no puede ser negativo");
        }
    }

    private boolean confirmarEliminacion(Usuario usuario) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro de que desea eliminar la cuenta de " +
                usuario.getNombreCompleto() + "?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }


    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}