package controller;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import dao.UsuarioDAOImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsientosController {

    @FXML
    private GridPane gridAsientos;

    @FXML
    private Label Label_NombreUsuario;

    @FXML
    private Label Label_DineroUsuario;

    @FXML
    private Label Label_NombrePelicula;

    @FXML
    private Label Label_horario;

    @FXML
    private Label Label_Coste;

    @FXML
    private Label estadoLabel;

    @FXML
    private Button confirmarButton;

    private Map<Button, Boolean> estadoAsientos; // Mapa para guardar el estado de cada asiento (seleccionado o no)
    private double costePorAsiento; // Precio por asiento basado en el precio de la película
    private double costeTotal = 0.0; // Coste acumulado de los asientos seleccionados

    private String nombreUsuario;
    private double dineroUsuario;
    private String nombrePelicula;
    private String horarioSeleccionado;

    @FXML
    public void initialize() {
        estadoAsientos = new HashMap<>(); // Inicializar el mapa

        // Crear un botón para cada asiento en la cuadrícula (5x5)
        for (int fila = 0; fila < 5; fila++) {
            for (int columna = 0; columna < 5; columna++) {
                Button asiento = new Button("A");
                asiento.getStyleClass().add("asiento-libre"); // Estilo inicial
                asiento.setOnAction(event -> cambiarEstadoAsiento(asiento)); // Configurar acción al hacer clic
                gridAsientos.add(asiento, columna, fila); // Añadir el asiento al GridPane
                estadoAsientos.put(asiento, false); // Guardar el asiento como "no seleccionado"
            }
        }

        // Inicializar etiquetas de coste
        Label_Coste.setText("$0.00");
    }

    /**
     * Confirma la selección de los asientos y realiza las acciones necesarias.
     */
    @FXML
    private void confirmarSeleccion() {
        if (costeTotal > dineroUsuario) {
            estadoLabel.setText("Estado: Saldo insuficiente para confirmar la selección.");
            return;
        }

        if (!estadoAsientos.containsValue(true)) {
            estadoLabel.setText("Estado: Por favor, seleccione al menos un asiento.");
            return;
        }

        // Descontar el saldo del usuario
        dineroUsuario -= costeTotal;
        Label_DineroUsuario.setText(String.format("Dinero: $%.2f", dineroUsuario));

        // Actualizar la base de datos
        actualizarSaldoEnBaseDeDatos(nombreUsuario, dineroUsuario);

        // Generar la factura en PDF
        try {
            generarFacturaPDF();
            mostrarMensajeGracias();
        } catch (Exception e) {
            estadoLabel.setText("Error al generar la factura.");
            e.printStackTrace();
        }
    }

    /**
     * Muestra un mensaje de agradecimiento y cierra la ventana.
     */
    private void mostrarMensajeGracias() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Compra Exitosa");
        alert.setHeaderText(null);
        alert.setContentText("¡Gracias por comprar!");

        alert.showAndWait();

        // Cerrar la ventana
        Stage stage = (Stage) confirmarButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Actualiza el saldo del usuario en la base de datos.
     *
     * @param emailUsuario Nombre del gmail
     * @param nuevoSaldo Nuevo saldo del usuario.
     */
    private void actualizarSaldoEnBaseDeDatos(String emailUsuario, double nuevoSaldo) {
        try {
            // Verificar datos recibidos
            System.out.println("Intentando actualizar saldo para el usuario: " + emailUsuario);
            System.out.println("Nuevo saldo calculado: " + nuevoSaldo);

            // Crear una instancia del DAO
            UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl();

            // Actualizar el saldo del usuario
            usuarioDAO.actualizarSaldo(emailUsuario, nuevoSaldo);

            System.out.println("Saldo actualizado correctamente en la base de datos.");
        } catch (Exception e) {
            estadoLabel.setText("Error al actualizar el saldo en la base de datos.");
            System.err.println("Error al actualizar el saldo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Genera un archivo PDF con la factura de la compra.
     */
    private void generarFacturaPDF() throws Exception {
        // Crear directorio para facturas si no existe
        String facturasPath = System.getProperty("user.home") + "/Facturas";
        File facturasDir = new File(facturasPath);
        if (!facturasDir.exists()) {
            facturasDir.mkdirs();
        }

        // Ruta del archivo PDF
        String filePath = facturasPath + "/Factura_" + nombreUsuario + ".pdf";

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));

        document.open();
        document.add(new Paragraph("Factura de Compra"));
        document.add(new Paragraph("Usuario: " + nombreUsuario));
        document.add(new Paragraph("Película: " + nombrePelicula));
        document.add(new Paragraph("Horario: " + horarioSeleccionado));
        document.add(new Paragraph(String.format("Precio Total: $%.2f", costeTotal)));

        // Lista de asientos seleccionados
        document.add(new Paragraph("Asientos seleccionados:"));
        List<String> asientosSeleccionados = obtenerAsientosSeleccionados();
        for (String asiento : asientosSeleccionados) {
            document.add(new Paragraph(asiento));
        }

        document.close();

        System.out.println("Factura generada en: " + filePath);
    }

    /**
     * Cambia el estado de un asiento (seleccionado o no seleccionado).
     *
     * @param asiento El botón del asiento que fue pulsado.
     */
    private void cambiarEstadoAsiento(Button asiento) {
        boolean seleccionado = estadoAsientos.get(asiento); // Obtener el estado actual del asiento
        if (seleccionado) {
            // Si estaba seleccionado, desmarcarlo
            asiento.getStyleClass().remove("asiento-seleccionado");
            asiento.getStyleClass().add("asiento-libre");
            estadoAsientos.put(asiento, false); // Actualizar el estado
            costeTotal -= costePorAsiento; // Restar el coste
        } else {
            // Si no estaba seleccionado, marcarlo
            asiento.getStyleClass().remove("asiento-libre");
            asiento.getStyleClass().add("asiento-seleccionado");
            estadoAsientos.put(asiento, true); // Actualizar el estado
            costeTotal += costePorAsiento; // Sumar el coste
        }

        // Actualizar etiqueta de coste
        Label_Coste.setText(String.format("$%.2f", costeTotal));
    }

    /**
     * Configura los datos del usuario y la película.
     *
     * @param emailUsuario Nombre del gmail.
     * @param dineroUsuario Dinero disponible del usuario.
     * @param nombrePelicula Nombre de la película.
     * @param precioPelicula Precio por asiento basado en la película.
     * @param horarioSeleccionado Horario seleccionado de la película.
     */
    public void configurarAsientos(String emailUsuario, double dineroUsuario, String nombrePelicula, double precioPelicula, String horarioSeleccionado) {
        this.nombreUsuario = emailUsuario; // Usa el correo electrónico como identificador
        this.dineroUsuario = dineroUsuario;
        this.nombrePelicula = nombrePelicula;
        this.horarioSeleccionado = horarioSeleccionado;
        this.costePorAsiento = precioPelicula;

        // Actualizar las etiquetas con los datos proporcionados
        Label_NombreUsuario.setText("Usuario: " + emailUsuario);
        Label_DineroUsuario.setText(String.format("Dinero: $%.2f", dineroUsuario));
        Label_NombrePelicula.setText("Película: " + nombrePelicula);
        Label_horario.setText("Horario: " + horarioSeleccionado);
    }

    /**
     * Obtiene la lista de asientos seleccionados en formato A1, B2, etc.
     *
     * @return Lista de asientos seleccionados.
     */
    private List<String> obtenerAsientosSeleccionados() {
        List<String> asientos = new ArrayList<>();
        estadoAsientos.forEach((asiento, seleccionado) -> {
            if (seleccionado) {
                int fila = GridPane.getRowIndex(asiento) + 1; // +1 para que sea 1-indexado
                int columna = GridPane.getColumnIndex(asiento) + 1;
                String asientoCodigo = (char) ('A' + fila - 1) + String.valueOf(columna); // Convierte fila a letra
                asientos.add(asientoCodigo);
            }
        });
        return asientos;
    }
}