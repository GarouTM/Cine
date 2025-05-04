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
        try {
            // Recopilar datos del formulario
            String titulo = TextField_Titulo.getText();
            String director = TextField_Director.getText();
            String genero = TextField_Genero.getText();
            String descripcion = Text_Area_Descripcion.getText();
            String duracionTexto = TextField_Duracion.getText();
            int anio = Integer.parseInt(TextField_Año.getText());

            if (imagenSeleccionada == null) {
                throw new IllegalArgumentException("Debe seleccionar una imagen.");
            }

            // Delegar la creación de la película a la lógica de negocio
            lnPeliculas.crearPelicula(titulo, director, anio, genero, duracionTexto, descripcion, imagenSeleccionada);

            mostrarAlerta("Éxito", "Película creada correctamente.", Alert.AlertType.INFORMATION);

            // Actualizar el HUB
            if (hubController != null) {
                hubController.cargarPeliculas();
            }

            // Cerrar la ventana actual
            Stage stage = (Stage) btm_confirmar.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El campo 'Año' debe contener un valor numérico válido.", Alert.AlertType.ERROR);
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Error", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error inesperado al crear la película: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}