package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class HorariosController {

    @FXML
    private ImageView Imagen_Peli;

    @FXML
    private Label TituloPeli;

    @FXML
    private Label horaActual;

    @FXML
    private Button btm_h1;

    @FXML
    private Button btm_h2;

    @FXML
    private Button btm_h3;

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private Timer timer;

    private String emailUsuario; // Correo electrónico del usuario
    private double dineroUsuario; // Saldo del usuario
    private String nombrePelicula;
    private double precioPelicula;

    /**
     * Configura la información del usuario y la película.
     *
     * @param emailUsuario    Correo electrónico del usuario.
     * @param titulo          Título de la película.
     * @param precioPelicula  Precio de la película.
     * @param duracionMinutos Duración de la película en minutos.
     * @param rutaImagen      Ruta de la imagen de la película.
     * @param dineroUsuario   Dinero disponible del usuario.
     */
    public void configurarDatosUsuario(String emailUsuario, String titulo, double precioPelicula, int duracionMinutos, String rutaImagen, double dineroUsuario) {
        this.emailUsuario = emailUsuario; // Guarda el correo electrónico
        this.nombrePelicula = titulo;
        this.precioPelicula = precioPelicula;
        this.dineroUsuario = dineroUsuario;

        // Configurar la imagen y el título
        try {
            Image imagen = new Image("file:" + rutaImagen);
            Imagen_Peli.setImage(imagen);
        } catch (Exception e) {
            Imagen_Peli.setImage(new Image("file:src/main/resources/default.jpg")); // Imagen por defecto si falla
        }
        TituloPeli.setText(titulo);

        // Iniciar el reloj con la hora actual
        iniciarReloj();

        // Generar horarios basados en la duración de la película
        generarHorarios(duracionMinutos);
    }

    /**
     * Inicia un reloj que muestra la hora actual en España.
     */
    private void iniciarReloj() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    LocalTime ahora = LocalTime.now(ZoneId.of("Europe/Madrid"));
                    horaActual.setText("Hora actual: " + ahora.format(timeFormatter));
                });
            }
        }, 0, 1000); // Actualizar cada segundo
    }

    /**
     * Genera tres horarios disponibles con intervalos basados en la duración de la película.
     *
     * @param duracionMinutos Duración de la película en minutos.
     */
    private void generarHorarios(int duracionMinutos) {
        // La hora base siempre será 16:00
        LocalTime baseHora = LocalTime.of(16, 0);

        // Generar horarios con intervalos de duración + 20 minutos
        btm_h1.setText(baseHora.format(timeFormatter));
        btm_h2.setText(baseHora.plusMinutes(duracionMinutos + 20).format(timeFormatter));
        btm_h3.setText(baseHora.plusMinutes((duracionMinutos + 20) * 2).format(timeFormatter));
    }

    /**
     * Evento al seleccionar el primer horario.
     */
    @FXML
    private void horaPeli1() {
        verificarHorario(btm_h1.getText());
    }

    /**
     * Evento al seleccionar el segundo horario.
     */
    @FXML
    private void horaPeli2() {
        verificarHorario(btm_h2.getText());
    }

    /**
     * Evento al seleccionar el tercer horario.
     */
    @FXML
    private void horaPeli3() {
        verificarHorario(btm_h3.getText());
    }

    /**
     * Verifica si el horario seleccionado es válido en comparación con la hora actual.
     * Si no es válido, muestra un mensaje de advertencia.
     * Si es válido, abre la ventana de selección de asientos.
     *
     * @param horarioTexto Texto del horario seleccionado.
     */
    private void verificarHorario(String horarioTexto) {
        try {
            LocalTime horarioSeleccionado = LocalTime.parse(horarioTexto, timeFormatter);
            LocalTime ahora = LocalTime.now(ZoneId.of("Europe/Madrid"));

            if (ahora.isAfter(horarioSeleccionado)) {
                mostrarMensajeAdvertencia("Lo sentimos", "La función ya ha comenzado o ha terminado. Seleccione otro horario o vuelva mañana.");
            } else {
                abrirSeleccionAsientos(horarioTexto);
            }
        } catch (Exception e) {
            mostrarMensajeAdvertencia("Error", "El formato del horario no es válido.");
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
     * Abre la ventana de selección de asientos y cierra la ventana actual.
     *
     * @param horarioSeleccionado Horario seleccionado por el usuario.
     */
    private void abrirSeleccionAsientos(String horarioSeleccionado) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Asientos.fxml"));
            Parent root = loader.load();

            // Obtener el controlador asociado al archivo FXML
            AsientosController controller = loader.getController();

            // Pasar los datos al controlador
            controller.configurarAsientos(emailUsuario, dineroUsuario, nombrePelicula, precioPelicula, horarioSeleccionado);

            // Configurar y mostrar la nueva ventana
            Stage stage = new Stage();
            stage.setTitle("Seleccionar Asientos");
            Scene scene = new Scene(root, 589, 372); // Tamaño fijo definido
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();

            Stage thisStage = (Stage) horaActual.getScene().getWindow();
            thisStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Detiene el reloj cuando se cierra la ventana.
     */
    public void detenerReloj() {
        if (timer != null) {
            timer.cancel();
        }
    }
}