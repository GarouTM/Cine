package controller;

import Logica_de_Negocio.LN_Peliculas;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    }

    public void configurarUsuario(String nombre, double dinero) {
        Label_Nombre.setText(nombre);
        Label_Dinero.setText(String.format("Dinero: $%.2f", dinero));
    }

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

        Label descripcionPelicula = new Label("Descripción: " + pelicula.getDescripcion());
        descripcionPelicula.setWrapText(true);
        descripcionPelicula.setStyle("-fx-font-size: 12px;");

        Label generoPelicula = new Label("Género: " + pelicula.getGenero());
        generoPelicula.setStyle("-fx-font-size: 12px;");

        Label directorPelicula = new Label("Director: " + pelicula.getDirector());
        directorPelicula.setStyle("-fx-font-size: 12px;");

        Label duracionPelicula = new Label("Duración: " + pelicula.getDuracion() + " min");
        duracionPelicula.setStyle("-fx-font-size: 12px;");

        peliculaBox.getChildren().addAll(imagenPelicula, tituloPelicula, descripcionPelicula, generoPelicula, directorPelicula, duracionPelicula);
        return peliculaBox;
    }

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

    private void mostrarError(String mensaje) {
        System.err.println("Error: " + mensaje);
    }
}