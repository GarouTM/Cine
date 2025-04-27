package controller;

import Logica_de_Negocio.LN_Usuarios;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CrearUController {

    @FXML
    private TextField TextField_Nombre;

    @FXML
    private TextField TextField_Apellido;

    @FXML
    private TextField TextField_Email;

    @FXML
    private TextField TextField_Contraseña;

    @FXML
    private TextField TextField_VContraseña;

    @FXML
    private TextField TextField_Saldo;

    @FXML
    private Button btm_ConfirmaCuenta;

    private final LN_Usuarios lnUsuarios;

    public CrearUController() {
        this.lnUsuarios = new LN_Usuarios();
    }

    @FXML
    private void Nombre() {
        TextField_Apellido.requestFocus();
    }

    @FXML
    private void Apellido() {
        TextField_Email.requestFocus();
    }

    @FXML
    private void Email() {
        TextField_Contraseña.requestFocus();
    }

    @FXML
    private void Contraseña() {
        TextField_VContraseña.requestFocus();
    }

    @FXML
    private void Verifica() {
        TextField_Saldo.requestFocus();
    }

    @FXML
    private void Saldo() {
        Cuenta();
    }

    @FXML
    private void Cuenta() {
        if (validarCamposVacios() && validarContraseñasCoinciden()) {
            try {
                String nombre = TextField_Nombre.getText().trim();
                String apellido = TextField_Apellido.getText().trim();
                String email = TextField_Email.getText().trim();
                String password = TextField_Contraseña.getText().trim();
                double saldo = Double.parseDouble(TextField_Saldo.getText().trim());

                // Las validaciones de formato y lógica de negocio se hacen en LN_Usuarios
                lnUsuarios.crearUsuario(email, password, nombre, apellido, saldo);
                cerrarVentana();

            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "El saldo debe ser un número válido", Alert.AlertType.ERROR);
            } catch (IllegalArgumentException e) {
                mostrarAlerta("Error", e.getMessage(), Alert.AlertType.ERROR);
            } catch (Exception e) {
                mostrarAlerta("Error", "Error al crear la cuenta: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private boolean validarCamposVacios() {
        if (TextField_Nombre.getText().trim().isEmpty() ||
                TextField_Apellido.getText().trim().isEmpty() ||
                TextField_Email.getText().trim().isEmpty() ||
                TextField_Contraseña.getText().trim().isEmpty() ||
                TextField_VContraseña.getText().trim().isEmpty() ||
                TextField_Saldo.getText().trim().isEmpty()) {

            mostrarAlerta("Error", "Todos los campos son obligatorios", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private boolean validarContraseñasCoinciden() {
        if (!TextField_Contraseña.getText().equals(TextField_VContraseña.getText())) {
            mostrarAlerta("Error", "Las contraseñas no coinciden", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btm_ConfirmaCuenta.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}