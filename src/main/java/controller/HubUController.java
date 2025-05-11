package controller;

import Logica_de_Negocio.LN_Peliculas;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Pelicula;

import java.io.IOException;
import java.util.List;

public class HubUController {

    @FXML
    private FlowPane Flow_Pelis;

    @FXML
    private Label Label_Nombre;

    @FXML
    private Label Label_Dinero;

    @FXML
    private Button btm_crear; // Botón para crear película

    private final LN_Peliculas lnPeliculas = new LN_Peliculas();

    @FXML
    public void initialize() {
        cargarPeliculas();
        btm_crear.setOnAction(event -> abrirCrearPelicula());
        aplicarEstiloCSS();
    }

    /**
     * Configura al usuario para mostrar su nombre y saldo en la barra superior.
     *
     * @param nombre Nombre del usuario.
     * @param dinero Saldo del usuario.
     */
    public void configurarUsuario(String nombre, double dinero) {
        Label_Nombre.setText(nombre);
        Label_Dinero.setText(String.format("Dinero: $%.2f", dinero));
    }

    /**
     * Carga las películas y las muestra en el FlowPane.
     */
    public void cargarPeliculas() {
        Flow_Pelis.getChildren().clear();

        List<Pelicula> peliculas = lnPeliculas.obtenerTodasPeliculas();

        if (peliculas != null && !peliculas.isEmpty()) {
            for (Pelicula pelicula : peliculas) {
                VBox peliculaBox = crearElementoPelicula(pelicula);
                Flow_Pelis.getChildren().add(peliculaBox);
            }
        } else {
            Label noPeliculas = new Label("No hay películas disponibles.");
            noPeliculas.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
            Flow_Pelis.getChildren().add(noPeliculas);
        }
    }

    /**
     * Crea un VBox representando una película y lo hace clickeable.
     *
     * @param pelicula Objeto película con su información.
     * @return VBox con la información de la película.
     */
    private VBox crearElementoPelicula(Pelicula pelicula) {
        VBox peliculaBox = new VBox();
        peliculaBox.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-spacing: 10; -fx-border-color: Black; -fx-border-width: 1;");
        peliculaBox.setPrefHeight(346);
        peliculaBox.setPrefWidth(194);

        ImageView imagenPelicula = new ImageView();
        imagenPelicula.setFitWidth(171);
        imagenPelicula.setFitHeight(225);
        try {
            Image imagen = new Image("file:" + pelicula.getRutaImagen());
            imagenPelicula.setImage(imagen);
        } catch (Exception e) {
            imagenPelicula.setImage(new Image("file:src/main/resources/default.jpg"));
        }

        Label tituloPelicula = new Label("Título: " + pelicula.getTitulo());
        tituloPelicula.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Descripción en un ScrollPane
        ScrollPane scrollDescripcion = new ScrollPane();
        Label descripcionPelicula = new Label(pelicula.getDescripcion());
        descripcionPelicula.setWrapText(true);
        descripcionPelicula.setStyle("-fx-font-size: 12px;");
        scrollDescripcion.setContent(descripcionPelicula);
        scrollDescripcion.setFitToWidth(true);
        scrollDescripcion.setPrefHeight(100); // Altura del área de descripción

        Label generoPelicula = new Label("Género: " + pelicula.getGenero());
        generoPelicula.setStyle("-fx-font-size: 12px;");

        Label directorPelicula = new Label("Director: " + pelicula.getDirector());
        directorPelicula.setStyle("-fx-font-size: 12px;");

        Label duracionPelicula = new Label("Duración: " + pelicula.getDuracion() + " min");
        duracionPelicula.setStyle("-fx-font-size: 12px;");

        // Añadir todos los datos al VBox
        peliculaBox.getChildren().addAll(imagenPelicula, tituloPelicula, scrollDescripcion, generoPelicula, directorPelicula, duracionPelicula);

        // Hacer el VBox clickeable para abrir la ventana de horarios
        peliculaBox.setOnMouseClicked(event -> abrirSeleccionHorarios(pelicula));

        return peliculaBox;
    }

    /**
     * Abre la ventana de selección de horarios para la película seleccionada.
     *
     * @param pelicula Película seleccionada.
     */
    /**
     * Abre la ventana de selección de horarios para la película seleccionada.
     *
     * @param pelicula Película seleccionada.
     */
    private void abrirSeleccionHorarios(Pelicula pelicula) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Horarios.fxml"));
            Parent root = loader.load();

            HorariosController controller = loader.getController();

            // Convertir la duración de double a int y manejar el saldo correctamente
            controller.configurarPelicula(
                    pelicula.getRutaImagen(),
                    pelicula.getTitulo(),
                    (int) pelicula.getDuracion(), // Conversión de double a int
                    Label_Nombre.getText(),
                    Double.parseDouble(Label_Dinero.getText().replace("Dinero: $", "").replace(",", ".")), // Reemplazar ',' por '.'
                    pelicula.getPrecio()
            );

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Seleccionar Horarios");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Abre la ventana para crear una nueva película.
     */
    private void abrirCrearPelicula() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/CrearP.fxml"));
            AnchorPane root = loader.load();

            CrearPController crearPController = loader.getController();
            crearPController.setHubController(this);

            Stage stage = new Stage();
            stage.setTitle("Crear Película");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            cargarPeliculas();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("No se pudo abrir la ventana de Crear Película.");
        }
    }

    private void aplicarEstiloCSS() {
        try {
            // Obtener la escena principal
            Scene scene = Flow_Pelis.getScene();
            if (scene != null) {
                // Cargar el archivo CSS
                String cssPath = getClass().getResource("/CSS/Hub.css").toExternalForm();
                scene.getStylesheets().add(cssPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudo cargar el archivo CSS.");
        }
    }

    /**
     * Muestra un mensaje de error en la consola.
     *
     * @param mensaje Mensaje de error.
     */
    private void mostrarError(String mensaje) {
        System.err.println("Error: " + mensaje);
    }
}