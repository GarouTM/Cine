package controller;

import Logica_de_Negocio.LN_Peliculas;
import dao.UsuarioDAOImpl;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import dao.UsuarioDAOImpl;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import java.util.Timer;
import java.util.TimerTask;

public class HubUController {

    @FXML
    private FlowPane Flow_Pelis;

    @FXML
    private Label Label_Nombre;

    @FXML
    private Label Label_Dinero;

    @FXML
    private Button btm_crear;

    @FXML
    private Button btm_eliminar;

    private final LN_Peliculas lnPeliculas = new LN_Peliculas();
    private String emailUsuario; // Correo electrónico del usuario
    private double dineroUsuario;
    private Timer timer; // Timer para la actualización periódica del saldo

    private Pelicula peliculaSeleccionada;

    @FXML
    public void initialize() {
        cargarPeliculas();
        btm_crear.setOnAction(event -> abrirCrearPelicula());
        btm_eliminar.setOnAction(event -> eliminarPS());
        aplicarEstiloCSS();
        iniciarActualizacionSaldo();
    }

    /**
     * Configura al usuario para mostrar su nombre, saldo y correo electrónico en la barra superior.
     *
     * @param nombre Nombre del usuario.
     * @param dinero Saldo del usuario.
     * @param email Correo electrónico del usuario.
     */
    public void configurarUsuario(String nombre, double dinero, String email) {
        Label_Nombre.setText(nombre);
        Label_Dinero.setText(String.format("Dinero: $%.2f", dinero));
        this.emailUsuario = email; // Guarda el correo electrónico
        this.dineroUsuario = dinero;
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

        // Configurar evento de clic para manejar selección y doble clic
        peliculaBox.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                // Seleccionar película con un solo clic
                peliculaSeleccionada = pelicula;
                Flow_Pelis.getChildren().forEach(node -> {
                    if (node instanceof VBox) {
                        node.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-spacing: 10; -fx-border-color: Black; -fx-border-width: 1;");
                    }
                });
                peliculaBox.setStyle("-fx-background-color: #d3d3d3; -fx-padding: 10; -fx-spacing: 10; -fx-border-color: Black; -fx-border-width: 1;");
            } else if (event.getClickCount() == 2) {
                // Abrir la ventana de horarios con un doble clic
                abrirSeleccionHorarios(pelicula);
            }
        });

        return peliculaBox;
    }

    private void iniciarActualizacionSaldo() {
        timer = new Timer(true); // "true" para que sea un hilo de demonio y no bloquee la aplicación al salir
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Actualizar el saldo en el hilo de la interfaz gráfica
                Platform.runLater(() -> {
                    actualizarSaldoBD();
                });
            }
        }, 0, 5000); // Actualizar cada 5 segundos
    }

    private void actualizarSaldoBD() {
        try {
            // Obtener el saldo actualizado desde la base de datos
            UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl(); // DAO o lógica para obtener datos del usuario
            double nuevoSaldo = usuarioDAO.obtenerSaldo(emailUsuario); // Metodo para obtener el saldo del usuario

            // Si el saldo es diferente, actualizar la interfaz
            if (nuevoSaldo != dineroUsuario) {
                dineroUsuario = nuevoSaldo;
                Label_Dinero.setText(String.format("Dinero: $%.2f", nuevoSaldo));
            }
        } catch (Exception e) {
            mostrarError("Error al actualizar el saldo: " + e.getMessage());
        }
    }

    /**
     * Elimina la película seleccionada.
     */
    private void eliminarPS() {
        if (peliculaSeleccionada == null) {
            mostrarError("Por favor, seleccione una película para eliminar.");
            return;
        }

        if (!confirmarEliminacion()) {
            return; // El usuario canceló la eliminación
        }

        try {
            lnPeliculas.eliminarPelicula(peliculaSeleccionada); // Llama al método de lógica para eliminar la película
            cargarPeliculas(); // Recarga la lista de películas
            peliculaSeleccionada = null; // Reinicia la selección
        } catch (Exception e) {
            mostrarError("Error al eliminar la película: " + e.getMessage());
        }
    }

    private boolean confirmarEliminacion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Estás seguro de que deseas eliminar esta película?");
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }



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

            // Pasar los datos necesarios al controlador
            controller.configurarDatosUsuario(
                    emailUsuario,
                    pelicula.getTitulo(),
                    pelicula.getPrecio(),
                    (int) pelicula.getDuracion(),
                    pelicula.getRutaImagen(), // Pasar la ruta de la imagen
                    dineroUsuario // Pasar el saldo del usuario
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

    public void actualizarSaldo(double nuevoSaldo) {
        this.dineroUsuario = nuevoSaldo;
        Label_Dinero.setText(String.format("Dinero: $%.2f", nuevoSaldo));
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
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }
}