package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import modelo.Pelicula;

import java.io.File;

public class ModificarPController {

    @FXML
    private ImageView Imagen_V_AntiguaImagen;

    @FXML
    private Label Label_NombreAntiguo;

    @FXML
    private TextField Text_Field_tituloM;

    @FXML
    private TextField Text_Field_directorM;

    @FXML
    private TextField Text_Field_añoM;

    @FXML
    private TextField Text_Field_generoM;

    @FXML
    private TextArea Text_Area_descripM;

    @FXML
    private ImageView ImageView_1;

    @FXML
    private Button Btm_Imagen;

    @FXML
    private Button btm_confirmar;

    private Pelicula peliculaSeleccionada; // Película que se está modificando
    private File nuevaImagen; // Archivo de la nueva imagen seleccionada
    private HubUController hubController; // Controlador del Hub para actualizaciones

    /**
     * Configura los datos iniciales para la ventana de modificación.
     *
     * @param pelicula        Película seleccionada para modificar.
     * @param hubController   Controlador principal para actualizaciones.
     */
    public void configurarPelicula(Pelicula pelicula, HubUController hubController) {
        this.peliculaSeleccionada = pelicula;
        this.hubController = hubController;

        // Mostrar los datos actuales de la película
        Label_NombreAntiguo.setText("Pelicula Antigua: " + pelicula.getTitulo());
        if (pelicula.getRutaImagen() != null && !pelicula.getRutaImagen().isEmpty()) {
            Imagen_V_AntiguaImagen.setImage(new Image("file:" + pelicula.getRutaImagen()));
        }
        Text_Field_tituloM.setPromptText(pelicula.getTitulo());
        Text_Field_directorM.setPromptText(pelicula.getDirector());
        Text_Field_añoM.setPromptText(String.valueOf(pelicula.getAnio()));
        Text_Field_generoM.setPromptText(pelicula.getGenero());
        Text_Area_descripM.setPromptText(pelicula.getDescripcion());
    }

    /**
     * Acción para insertar una nueva imagen.
     */
    @FXML
    private void insertarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.jpg", "*.png", "*.jpeg")
        );

        File archivoSeleccionado = fileChooser.showOpenDialog(Btm_Imagen.getScene().getWindow());
        if (archivoSeleccionado != null) {
            nuevaImagen = archivoSeleccionado;
            ImageView_1.setImage(new Image("file:" + archivoSeleccionado.getAbsolutePath()));
        }
    }

    /**
     * Acción para confirmar las modificaciones realizadas.
     */
    @FXML
    private void confirmarModificacion() {
        try {
            // Verificar y aplicar cambios solo en los campos modificados
            String nuevoTitulo = Text_Field_tituloM.getText().trim().isEmpty() ? peliculaSeleccionada.getTitulo() : Text_Field_tituloM.getText().trim();
            String nuevoDirector = Text_Field_directorM.getText().trim().isEmpty() ? peliculaSeleccionada.getDirector() : Text_Field_directorM.getText().trim();

            // Validar y asignar el año
            int nuevoAño;
            if (Text_Field_añoM.getText().trim().isEmpty()) {
                nuevoAño = peliculaSeleccionada.getAnio();
            } else {
                nuevoAño = Integer.parseInt(Text_Field_añoM.getText().trim());
                if (nuevoAño < 1895 || nuevoAño > java.time.LocalDate.now().getYear()) {
                    throw new IllegalArgumentException("El año debe estar entre 1895 y el año actual.");
                }
            }

            String nuevoGenero = Text_Field_generoM.getText().trim().isEmpty() ? peliculaSeleccionada.getGenero() : Text_Field_generoM.getText().trim();
            String nuevaDescripcion = Text_Area_descripM.getText().trim().isEmpty() ? peliculaSeleccionada.getDescripcion() : Text_Area_descripM.getText().trim();

            // Manejar la nueva imagen
            String nuevaRutaImagen = nuevaImagen != null ? nuevaImagen.getAbsolutePath() : peliculaSeleccionada.getRutaImagen();

            // Actualizar la película con los nuevos valores
            peliculaSeleccionada.setTitulo(nuevoTitulo);
            peliculaSeleccionada.setDirector(nuevoDirector);
            peliculaSeleccionada.setAnio(nuevoAño);
            peliculaSeleccionada.setGenero(nuevoGenero);
            peliculaSeleccionada.setDescripcion(nuevaDescripcion);
            peliculaSeleccionada.setRutaImagen(nuevaRutaImagen);

            // Actualizar la base de datos o lógica de negocio
            hubController.guardarCambiosPelicula(peliculaSeleccionada);

            mostrarMensajeExito("Modificación Exitosa", "La película ha sido modificada correctamente.");
            cerrarVentana();

        } catch (NumberFormatException e) {
            mostrarMensajeError("Error en los Datos", "El año debe ser un número válido.");
        } catch (IllegalArgumentException e) {
            mostrarMensajeError("Error en los Datos", e.getMessage());
        } catch (Exception e) {
            mostrarMensajeError("Error", "Ocurrió un error al modificar la película: " + e.getMessage());
        }
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
     * Muestra un mensaje de error al usuario.
     *
     * @param titulo  Título del mensaje.
     * @param mensaje Contenido del mensaje.
     */
    private void mostrarMensajeError(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
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