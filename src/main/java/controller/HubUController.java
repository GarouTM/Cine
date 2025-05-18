package controller;

import Logica_de_Negocio.LN_Peliculas;
import Logica_de_Negocio.LN_Usuarios;
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
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import modelo.Usuario;

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

    @FXML
    private Button btm_modificar;

    @FXML
    private ImageView img_crear;

    @FXML
    private ImageView img_eliminar;

    @FXML
    private ImageView img_modificar;

    @FXML
    private Button Btm_opc;

    @FXML
    private Button btm_añadir_dinero;

    private final LN_Usuarios lnUsuarios = new LN_Usuarios();

    private Usuario usuarioActivo;

    private final LN_Peliculas lnPeliculas = new LN_Peliculas();
    private String emailUsuario; // Correo electrónico del usuario
    private double dineroUsuario;
    private Timer timer; // Timer para la actualización periódica del saldo

    private Pelicula peliculaSeleccionada;

    @FXML
    public void initialize() {
        configurarBotonesAdministrador();
        cargarPeliculas();
        btm_crear.setOnAction(event -> abrirCrearPelicula());
        btm_eliminar.setOnAction(event -> eliminarPS());
        btm_añadir_dinero.setOnAction(event -> abrirDinero());
        btm_modificar.setOnAction(event -> abrirModificarPelicula());
        aplicarEstiloCSS();
        iniciarActualizacionSaldo();

        configurarBotonOpciones();
    }

    public void setEmailUsuario(String emailUsuario) {
        if (emailUsuario == null || emailUsuario.isEmpty()) {
            mostrarError("El correo electrónico no puede estar vacío.");
            return;
        }
        this.emailUsuario = emailUsuario;
    }

    public void setUsuarioActivo(Usuario usuario) {
        if (usuario == null) {
            mostrarError("El usuario es inválido. Por favor, inicie sesión nuevamente.");
            return;
        }

        this.usuarioActivo = usuario;

        // Actualizar la interfaz de usuario con la información del usuario activo
        Label_Nombre.setText("Usuario: " + usuario.getNombreCompleto());
        Label_Dinero.setText(String.format("Dinero: $%.2f", usuario.getSaldo()));

        // Actualizar campos relacionados, si los hay
        this.emailUsuario = usuario.getGmail();
        this.dineroUsuario = usuario.getSaldo();


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
     * Abre la ventana para modificar una película seleccionada.
     */
    private void abrirModificarPelicula() {
        if (peliculaSeleccionada == null) {
            mostrarError("Por favor, seleccione una película para modificar.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/ModificarP.fxml"));
            Parent root = loader.load();

            ModificarPController controller = loader.getController();
            controller.configurarPelicula(peliculaSeleccionada, this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modificar Película");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarPeliculas(); // Recargar la lista de películas después de modificar
        } catch (IOException e) {
            mostrarError("No se pudo abrir la ventana de Modificar Película: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Metodo para guardar los cambios realizados en una película.
     *
     * @param pelicula Película con los datos actualizados.
     */
    public void guardarCambiosPelicula(Pelicula pelicula) {
        try {
            // Actualizar la película en la base de datos o lógica de negocio
            lnPeliculas.actualizarPelicula(pelicula);
            mostrarMensajeExito("Éxito", "La película ha sido modificada correctamente.");

        } catch (Exception e) {
            mostrarError("Error al guardar cambios en la película: " + e.getMessage());
        }
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

    /**
     * Configura el botón de opciones para mostrar un menú desplegable.
     */
    private void configurarBotonOpciones() {
        // Crear el menú contextual
        ContextMenu contextMenu = new ContextMenu();

        // Opción: Modo Oscuro
        CheckMenuItem modoOscuroItem = new CheckMenuItem("Modo Oscuro");
        modoOscuroItem.setOnAction(event -> {
            if (modoOscuroItem.isSelected()) {
                activarModoOscuro();
            } else {
                desactivarModoOscuro();
            }
        });

        // Opción: Volver a la pantalla de inicio de sesión
        MenuItem volverInicioSesionItem = new MenuItem("Volver a Inicio de Sesión");
        volverInicioSesionItem.setOnAction(event -> volverPantallaILogin());

        // Agregar las opciones al menú contextual
        contextMenu.getItems().addAll(modoOscuroItem, volverInicioSesionItem);

        // Asociar el menú contextual al botón de opciones
        Btm_opc.setOnMouseClicked(event -> {
            if (event.getButton().equals(javafx.scene.input.MouseButton.PRIMARY)) {
                contextMenu.show(Btm_opc, event.getScreenX(), event.getScreenY());
            }
        });
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
     * Activa el modo oscuro aplicando un estilo CSS.
     */
    private void activarModoOscuro() {
        Scene scene = Flow_Pelis.getScene();
        if (scene != null) {
            String darkModeCss = getClass().getResource("/CSS/DarkMode.css").toExternalForm();
            if (!scene.getStylesheets().contains(darkModeCss)) {
                scene.getStylesheets().add(darkModeCss);
            }
        }
    }

    /**
     * Desactiva el modo oscuro eliminando el estilo CSS.
     */
    private void desactivarModoOscuro() {
        Scene scene = Flow_Pelis.getScene();
        if (scene != null) {
            String darkModeCss = getClass().getResource("/CSS/DarkMode.css").toExternalForm();
            scene.getStylesheets().remove(darkModeCss);
        }
    }


    /**
     * Permite al usuario volver a la pantalla de inicio de sesión.
     */
    private void volverPantallaILogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) Btm_opc.getScene().getWindow();

            // Configura la escena con el tamaño deseado
            Scene scene = new Scene(root, 600.0, 450.0); // Ancho x Alto
            stage.setScene(scene);

            stage.setTitle("Inicio de Sesión");
            stage.setResizable(false); // Deshabilita redimensionamiento
            stage.setMaximized(false); // Asegura que no esté maximizado

            // Establece el tamaño específico de la ventana
            stage.setWidth(600.0);
            stage.setHeight(450.0);
        } catch (IOException e) {
            mostrarError("No se pudo volver a la pantalla de inicio de sesión.");
            e.printStackTrace();
        }
    }

    /**
     * Abre la pestaña para agregar dinero.
     */
    private void abrirDinero() {
        if (usuarioActivo == null) {
            mostrarError("El usuario no está configurado. Por favor, inicie sesión nuevamente.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Dinero.fxml"));
            Parent root = loader.load();

            // Obtener el controlador de la vista Dinero
            DineroController dineroController = loader.getController();

            // Pasar los datos del usuario al controlador
            dineroController.configurarDatos(usuarioActivo.getGmail(), usuarioActivo.getSaldo(), this);

            // Configurar y mostrar la nueva ventana
            Stage stage = new Stage();
            stage.setTitle("Ingresar Dinero");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            mostrarError("No se pudo abrir la ventana de Dinero: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //El metodo para verificar que si entra como admin los botones de crear, eliminar y modificar estan disponibles
    private void configurarBotonesAdministrador() {
        if (emailUsuario == null || emailUsuario.isEmpty()) {
            mostrarError("El correo electrónico no es válido.");
            return;
        }

        // Verificar si el usuario es administrador
        boolean esAdmin = emailUsuario.equalsIgnoreCase("admin@gmail.com");

        // Configurar visibilidad y funcionalidad de botones
        btm_crear.setVisible(esAdmin);
        btm_crear.setManaged(esAdmin);
        img_crear.setVisible(esAdmin);
        img_crear.setManaged(esAdmin);

        btm_eliminar.setVisible(esAdmin);
        btm_eliminar.setManaged(esAdmin);
        img_eliminar.setVisible(esAdmin);
        img_eliminar.setManaged(esAdmin);

        btm_modificar.setVisible(esAdmin);
        btm_modificar.setManaged(esAdmin);
        img_modificar.setVisible(esAdmin);
        img_modificar.setManaged(esAdmin);

        // Ajustar la altura de la ventana
        Platform.runLater(() -> {
            Stage stage = (Stage) Flow_Pelis.getScene().getWindow();
            if (esAdmin) {
                stage.setHeight(600); // Altura normal para admin
            } else {
                stage.setHeight(500); // Altura reducida para usuarios normales
            }
        });
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

    private void mostrarMensajeExito(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

}