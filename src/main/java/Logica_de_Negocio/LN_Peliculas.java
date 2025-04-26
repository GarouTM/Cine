package Logica_de_Negocio;

import dao.PeliculaDAOImpl;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import modelo.Pelicula;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

public class LN_Peliculas {
    private final PeliculaDAOImpl peliculaDAO;
    private static final String IMAGENES_DIR = "src/main/resources/imagenes/peliculas/";

    public LN_Peliculas() {
        this.peliculaDAO = new PeliculaDAOImpl();
        crearDirectorioImagenes();
    }

    private void crearDirectorioImagenes() {
        try {
            Files.createDirectories(Paths.get(IMAGENES_DIR));
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo crear el directorio de imágenes: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void crearPelicula(String titulo, String director, int anio, String genero,
                              double duracion, String sinopsis, File imagenFile, double precio) {
        try {
            // Validar datos
            validarDatosPelicula(titulo, director, anio, duracion, precio);

            // Generar ID
            String id = peliculaDAO.generarSiguienteId();

            // Manejar la imagen
            String rutaImagen = manejarImagen(id, imagenFile);

            // Crear y guardar la película
            Pelicula pelicula = new Pelicula(id, titulo, director, anio, genero,
                    duracion, sinopsis, rutaImagen, precio);
            peliculaDAO.insertar(pelicula);
            mostrarAlerta("Éxito", "Película creada correctamente.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al crear la película: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private String manejarImagen(String id, File imagenFile) throws Exception {
        if (imagenFile == null) {
            return IMAGENES_DIR + "default.jpg"; // Imagen por defecto
        }

        String extension = getFileExtension(imagenFile.getName());
        String nombreArchivo = id + extension;
        Path destino = Paths.get(IMAGENES_DIR + nombreArchivo);

        Files.copy(imagenFile.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
        return destino.toString();
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".")).toLowerCase();
    }

    private void validarDatosPelicula(String titulo, String director, int anio, double duracion, double precio) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        if (director == null || director.trim().isEmpty()) {
            throw new IllegalArgumentException("El director no puede estar vacío");
        }
        if (anio < 1895 || anio > 2030) {
            throw new IllegalArgumentException("Año inválido");
        }
        if (duracion <= 0) {
            throw new IllegalArgumentException("La duración debe ser mayor que 0");
        }
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
    }

    public void eliminarPelicula(Pelicula pelicula) {
        if (pelicula == null) {
            mostrarAlerta("Error", "No se ha seleccionado ninguna película", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro de que desea eliminar la película " + pelicula.getTitulo() + "?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                // Eliminar la imagen si existe
                if (pelicula.getRutaImagen() != null) {
                    Files.deleteIfExists(Paths.get(pelicula.getRutaImagen()));
                }

                peliculaDAO.eliminar(pelicula.getId());
                mostrarAlerta("Éxito", "Película eliminada correctamente.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                mostrarAlerta("Error", "Error al eliminar la película: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    public List<Pelicula> obtenerTodasPeliculas() {
        try {
            return peliculaDAO.obtenerTodas();
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al obtener las películas: " + e.getMessage(), Alert.AlertType.ERROR);
            return null;
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