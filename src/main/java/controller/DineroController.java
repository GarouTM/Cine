package controller;

import dao.UsuarioDAOImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DineroController {

    @FXML
    private Label Label_NombreUsuario;

    @FXML
    private Label Label_DineroUsuario;

    @FXML
    private TextField Text_field_Dinero_Ingresar;

    @FXML
    private PasswordField Text_field_Contraseña; // Campo para ingresar la contraseña

    @FXML
    private Button btm_confirmar;

    private String emailUsuario; // Correo electrónico del usuario
    private double dineroUsuario; // Saldo actual del usuario
    private HubUController hubController; // Controlador principal para actualizaciones

    /**
     * Configura los datos iniciales del usuario.
     *
     * @param emailUsuario  Correo electrónico del usuario.
     * @param dineroUsuario Saldo actual del usuario.
     * @param hubController Controlador principal para actualizaciones.
     */
    public void configurarDatos(String emailUsuario, double dineroUsuario, HubUController hubController) {
        this.emailUsuario = emailUsuario;
        this.dineroUsuario = dineroUsuario;
        this.hubController = hubController;

        // Mostrar la información inicial en la interfaz
        Label_NombreUsuario.setText("Usuario: " + emailUsuario);
        Label_DineroUsuario.setText(String.format("Dinero: $%.2f", dineroUsuario));
    }

    /**
     * Maneja la confirmación del ingreso de dinero.
     */
    @FXML
    private void confirmarIngresoDinero() {
        try {
            // Validar campo de ingreso
            String cantidadTexto = Text_field_Dinero_Ingresar.getText();
            if (cantidadTexto == null || cantidadTexto.trim().isEmpty()) {
                mostrarMensajeAdvertencia("Error", "Por favor, ingrese una cantidad de dinero.");
                return;
            }

            double cantidadIngresar = Double.parseDouble(cantidadTexto);
            if (cantidadIngresar <= 0) {
                mostrarMensajeAdvertencia("Error", "La cantidad a ingresar debe ser mayor a 0.");
                return;
            }

            // Validar la contraseña
            String contraseña = Text_field_Contraseña.getText();
            if (contraseña == null || contraseña.trim().isEmpty()) {
                mostrarMensajeAdvertencia("Error", "Por favor, ingrese su contraseña.");
                return;
            }

            if (!validarContraseña(emailUsuario, contraseña)) {
                mostrarMensajeAdvertencia("Error", "La contraseña ingresada no es correcta.");
                return;
            }

            // Actualizar el saldo del usuario
            dineroUsuario += cantidadIngresar;
            Label_DineroUsuario.setText(String.format("Dinero: $%.2f", dineroUsuario));

            // Actualizar la base de datos
            actualizarSaldoEnBaseDeDatos(emailUsuario, dineroUsuario);

            // Actualizar el saldo en el HubUController
            if (hubController != null) {
                hubController.actualizarSaldo(dineroUsuario);
            }

            // Mostrar mensaje de éxito y cerrar la ventana
            mostrarMensajeExito("Éxito", "El dinero ha sido ingresado correctamente.");
            cerrarVentana();

        } catch (NumberFormatException e) {
            mostrarMensajeAdvertencia("Error", "Por favor, ingrese una cantidad válida.");
        } catch (Exception e) {
            mostrarMensajeAdvertencia("Error", "Ocurrió un error al ingresar el dinero: " + e.getMessage());
        }
    }

    /**
     * Valida la contraseña ingresada con la contraseña almacenada para el usuario.
     *
     * @param emailUsuario Correo electrónico del usuario.
     * @param contraseña   Contraseña ingresada por el usuario.
     * @return true si la contraseña es correcta, false de lo contrario.
     */
    private boolean validarContraseña(String emailUsuario, String contraseña) {
        try {
            UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl();
            String contraseñaAlmacenada = usuarioDAO.obtenerContraseña(emailUsuario);
            return contraseñaAlmacenada != null && contraseñaAlmacenada.equals(contraseña);
        } catch (Exception e) {
            mostrarMensajeAdvertencia("Error", "No se pudo validar la contraseña.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualiza el saldo del usuario en la base de datos.
     *
     * @param emailUsuario Correo electrónico del usuario.
     * @param nuevoSaldo   Nuevo saldo del usuario.
     */
    private void actualizarSaldoEnBaseDeDatos(String emailUsuario, double nuevoSaldo) {
        try {
            UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl();
            usuarioDAO.actualizarSaldo(emailUsuario, nuevoSaldo);
        } catch (Exception e) {
            mostrarMensajeAdvertencia("Error", "No se pudo actualizar el saldo en la base de datos.");
            e.printStackTrace();
        }
    }

    /**
     * Muestra un mensaje de advertencia al usuario.
     *
     * @param titulo  Título del mensaje.
     * @param mensaje Contenido del mensaje.
     */
    private void mostrarMensajeAdvertencia(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Muestra un mensaje de éxito al usuario.
     *
     * @param titulo  Título del mensaje.
     * @param mensaje Contenido del mensaje.
     */
    private void mostrarMensajeExito(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Cierra la ventana actual.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) btm_confirmar.getScene().getWindow();
        stage.close();
    }
}