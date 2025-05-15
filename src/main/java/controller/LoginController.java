package controller;

import Logica_de_Negocio.LN_Usuarios;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Usuario;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField TextField_Email;
    @FXML
    private TextField TextField_Contraseña;
    @FXML
    private Button btm_Login;
    @FXML
    private Button btm_CrearUsuario;
    @FXML
    private CheckBox Check_Recordar;

    private final LN_Usuarios lnUsuarios;
    private static final String CONFIG_FILE = "config.properties";
    private static final String GMAIL_SUFFIX = "@gmail.com";

    public LoginController() {
        this.lnUsuarios = new LN_Usuarios();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarUsuarioGuardado();
        configurarValidacionEmail();
        configurarNavegacionCampos();
    }

    private void configurarValidacionEmail() {
        TextField_Email.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean esValido = newValue.isEmpty() || newValue.endsWith(GMAIL_SUFFIX);
            TextField_Email.setStyle(esValido ? "" : "-fx-border-color: red;");
        });
    }

    private void configurarNavegacionCampos() {
        TextField_Email.setOnAction(e -> TextField_Contraseña.requestFocus());
        TextField_Contraseña.setOnAction(e -> entrar());
    }

    @FXML
    private void Email(ActionEvent event) {
        TextField_Contraseña.requestFocus();
    }

    @FXML
    private void Contraseña(ActionEvent event) {
        entrar();
    }

    @FXML
    private void entrar() {
        String email = TextField_Email.getText().trim();
        String password = TextField_Contraseña.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error", "Por favor, complete todos los campos", Alert.AlertType.ERROR);
            return;
        }

        try {
            Usuario usuario = lnUsuarios.login(email, password);
            if (usuario != null) {
                manejarLoginExitoso(usuario, email);
            } else {
                manejarLoginFallido();
                mostrarAlerta("Error", "Credenciales incorrectas", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al iniciar sesión: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void manejarLoginExitoso(Usuario usuario, String email) {
        gestionarRecordatorio(email);
        abrirPrincipal(usuario); // Pasamos el objeto Usuario al abrir el HUB
        cerrarVentanaLogin();
    }

    private void manejarLoginFallido() {
        TextField_Contraseña.clear();
        TextField_Contraseña.requestFocus();
    }

    private void gestionarRecordatorio(String email) {
        if (Check_Recordar.isSelected()) {
            guardarUsuario(email);
        } else {
            borrarUsuarioGuardado();
        }
    }

    private void cerrarVentanaLogin() {
        Stage stage = (Stage) btm_Login.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void CreaUsuario(ActionEvent event) {
        try {
            abrirVentanaModal("/FXML/CrearU.fxml", "Crear Nueva Cuenta");
        } catch (IOException e) {
            mostrarAlerta("Error",
                    "Error al abrir la ventana de registro: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void abrirVentanaModal(String fxmlPath, String titulo) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle(titulo);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(btm_CrearUsuario.getScene().getWindow());
        stage.setResizable(false);
        stage.showAndWait();
    }

    @FXML
    private void Recuerda_Usuario(ActionEvent event) {
        if (!Check_Recordar.isSelected()) {
            borrarUsuarioGuardado();
        }
    }

    private void abrirPrincipal(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Hub.fxml"));
            Parent root = loader.load();

            // Obtener el controlador del HUB
            HubUController hubController = loader.getController();

            // Pasar los datos del usuario al HUB
            hubController.configurarUsuario(usuario.getNombreCompleto(), usuario.getSaldo(), usuario.getGmail());

            // Mostrar el HUB
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("House Cinema");
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            mostrarAlerta("Error",
                    "Error al abrir la ventana principal: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void guardarUsuario(String email) {
        Properties prop = new Properties();
        prop.setProperty("email", email);

        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            prop.store(fos, "User Configuration");
        } catch (IOException e) {
            System.err.println("Error al guardar la configuración: " + e.getMessage());
        }
    }

    private void cargarUsuarioGuardado() {
        Properties prop = new Properties();
        File configFile = new File(CONFIG_FILE);

        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                prop.load(fis);
                String savedEmail = prop.getProperty("email");
                if (savedEmail != null && !savedEmail.isEmpty()) {
                    TextField_Email.setText(savedEmail);
                    Check_Recordar.setSelected(true);
                }
            } catch (IOException e) {
                System.err.println("Error al cargar la configuración: " + e.getMessage());
            }
        }
    }

    private void borrarUsuarioGuardado() {
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            configFile.delete();
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