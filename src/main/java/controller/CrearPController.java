package controller;

import Logica_de_Negocio.LN_Peliculas;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.regex.Pattern;

public class CrearPController {

    @FXML
    private TextField TextField_Titulo;

    @FXML
    private TextField TextField_Director;

    @FXML
    private TextField TextField_Año;

    @FXML
    private TextField TextField_Genero;

    @FXML
    private TextField TextField_Duracion;

    @FXML
    private TextArea Text_Area_Descripcion;

    @FXML
    private ImageView ImageView_1;

    @FXML
    private Button Btm_Imagen;

    @FXML
    private Button btm_confirmar;

    private final LN_Peliculas lnPeliculas = new LN_Peliculas();
    private File imagenSeleccionada;

    private HubUController hubController; // Referencia al controlador del HUB

    @FXML
    public void initialize() {
        btm_confirmar.setOnAction(null); // Evitar múltiples registros
        Btm_Imagen.setOnAction(event -> cargarImagen());
        btm_confirmar.setOnAction(event -> confirmarPelicula());
    }

    public void setHubController(HubUController hubController) {
        this.hubController = hubController;
    }

    private void cargarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        imagenSeleccionada = fileChooser.showOpenDialog(null);

        if (imagenSeleccionada != null) {
            Image imagen = new Image(imagenSeleccionada.toURI().toString());
            ImageView_1.setImage(imagen);
        } else {
            mostrarAlerta("Error", "No se seleccionó ninguna imagen.", Alert.AlertType.WARNING);
        }
    }

    private void confirmarPelicula() {
        // Validar los campos primero
        if (!validarCampos()) {
            return; // Si la validación falla, no continuar
        }

        try {
            // Recopilar datos del formulario
            String titulo = TextField_Titulo.getText();
            String director = TextField_Director.getText();
            String genero = TextField_Genero.getText();
            String descripcion = Text_Area_Descripcion.getText();
            String duracionTexto = TextField_Duracion.getText();

            // Validar y convertir el campo 'Año'
            int anio;
            try {
                anio = Integer.parseInt(TextField_Año.getText());
            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "El campo 'Año' debe contener un valor numérico válido.", Alert.AlertType.ERROR);
                return; // Salir si hay un error con el campo 'Año'
            }

            // Validar rango del año
            if (anio < 1800 || anio > 2100) {
                mostrarAlerta("Error", "El campo 'Año' debe estar entre 1800 y 2100.", Alert.AlertType.ERROR);
                return;
            }

            // Validar duración usando el método de LN_Peliculas
            if (!validarDuracion(duracionTexto)) {
                mostrarAlerta("Error", "El campo 'Duración' debe tener un formato válido, como '1H 20MIN', '1 hora y media', '1:30', '90 minutos'.", Alert.AlertType.ERROR);
                return;
            }

            // Delegar la creación de la película a la lógica de negocio
            lnPeliculas.crearPelicula(titulo, director, anio, genero, duracionTexto, descripcion, imagenSeleccionada);

            // Mostrar mensaje de éxito
            mostrarAlerta("Éxito", "Película creada correctamente.", Alert.AlertType.INFORMATION);

            // Actualizar el HUB
            if (hubController != null) {
                hubController.cargarPeliculas();
            }

            // Cerrar la ventana actual
            Stage stage = (Stage) btm_confirmar.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            mostrarAlerta("Error", "Error inesperado al crear la película: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean validarCampos() {
        // Validar el título
        if (TextField_Titulo.getText() == null || TextField_Titulo.getText().trim().isEmpty()) {
            mostrarAlerta("Error", "El campo 'Título' es obligatorio.", Alert.AlertType.ERROR);
            return false;
        }

        // Validar el director
        if (TextField_Director.getText() == null || TextField_Director.getText().trim().isEmpty()) {
            mostrarAlerta("Error", "El campo 'Director' es obligatorio.", Alert.AlertType.ERROR);
            return false;
        }

        // Validar el año
        try {
            int anio = Integer.parseInt(TextField_Año.getText());
            if (anio < 1800 || anio > 2100) { // Validación de rango
                mostrarAlerta("Error", "El campo 'Año' debe estar entre 1800 y 2100.", Alert.AlertType.ERROR);
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El campo 'Año' debe contener un valor numérico válido.", Alert.AlertType.ERROR);
            return false;
        }

        // Validar género
        if (TextField_Genero.getText() == null || TextField_Genero.getText().trim().isEmpty()) {
            mostrarAlerta("Error", "El campo 'Género' es obligatorio.", Alert.AlertType.ERROR);
            return false;
        }

        // Validar duración
        if (TextField_Duracion.getText() == null || TextField_Duracion.getText().trim().isEmpty()) {
            mostrarAlerta("Error", "El campo 'Duración' es obligatorio.", Alert.AlertType.ERROR);
            return false;
        }

        // Validar descripción
        if (Text_Area_Descripcion.getText() == null || Text_Area_Descripcion.getText().trim().isEmpty()) {
            mostrarAlerta("Error", "El campo 'Descripción' es obligatorio.", Alert.AlertType.ERROR);
            return false;
        }

        // Validar imagen seleccionada
        if (imagenSeleccionada == null) {
            mostrarAlerta("Error", "Debe seleccionar una imagen.", Alert.AlertType.ERROR);
            return false;
        }

        // Si todas las validaciones pasan
        return true;
    }

    private boolean validarDuracion(String duracionTexto) {
        // Patrones de duración válidos (basados en LN_Peliculas)
        Pattern formatoHMin = Pattern.compile("(\\d+)H\\s*(\\d+)?MIN?", Pattern.CASE_INSENSITIVE);
        Pattern formatoHoraYMedia = Pattern.compile("(\\d+)\\s*h(o)?r?a?\\s*y\\s*(media|1/2)", Pattern.CASE_INSENSITIVE);
        Pattern formatoHorasYMinutos = Pattern.compile("(\\d+):(\\d+)");
        Pattern formatoSoloHoras = Pattern.compile("(\\d+)\\s*h(o)?r?a?s?", Pattern.CASE_INSENSITIVE);
        Pattern formatoSoloMinutos = Pattern.compile("(\\d+)\\s*m(i)?n?(utos)?", Pattern.CASE_INSENSITIVE);

        // Validar el texto de duración contra los patrones
        return formatoHMin.matcher(duracionTexto).matches() ||
                formatoHoraYMedia.matcher(duracionTexto).matches() ||
                formatoHorasYMinutos.matcher(duracionTexto).matches() ||
                formatoSoloHoras.matcher(duracionTexto).matches() ||
                formatoSoloMinutos.matcher(duracionTexto).matches();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}