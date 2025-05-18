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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                              String duracionTexto, String descripcion, File imagenFile) {
        try {
            // Validar datos
            double duracion = validarDatosPelicula(titulo, director, anio, duracionTexto);

            // Generar ID
            String id = peliculaDAO.generarSiguienteId();

            // Manejar la imagen
            String rutaImagen = manejarImagen(id, imagenFile);

            // Crear y guardar la película
            Pelicula pelicula = new Pelicula(id, titulo, director, anio, genero, duracion, descripcion, rutaImagen);
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

    private double validarDatosPelicula(String titulo, String director, int anio, String duracionTexto) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        if (director == null || director.trim().isEmpty()) {
            throw new IllegalArgumentException("El director no puede estar vacío");
        }
        if (anio < 1895 || anio > 2030) {
            throw new IllegalArgumentException("Año inválido");
        }
        if (duracionTexto == null || duracionTexto.trim().isEmpty()) {
            throw new IllegalArgumentException("La duración no puede estar vacía");
        }

        // Validar y convertir la duración a minutos
        return convertirDuracionAMinutos(duracionTexto);
    }

    private double convertirDuracionAMinutos(String duracionTexto) {
        // Expresiones regulares para varios formatos
        Pattern formatoHMin = Pattern.compile("(\\d+)H\\s*(\\d+)?MIN?", Pattern.CASE_INSENSITIVE);
        Pattern formatoHoraYMedia = Pattern.compile("(\\d+)\\s*h(o)?r?a?\\s*y\\s*(media|1/2)", Pattern.CASE_INSENSITIVE);
        Pattern formatoHorasYMinutos = Pattern.compile("(\\d+):(\\d+)");
        Pattern formatoSoloHoras = Pattern.compile("(\\d+)\\s*h(o)?r?a?s?", Pattern.CASE_INSENSITIVE);
        Pattern formatoSoloMinutos = Pattern.compile("(\\d+)\\s*m(i)?n?(utos)?", Pattern.CASE_INSENSITIVE);

        Matcher matcher;

        // 1. Formato: "1H 20MIN"
        matcher = formatoHMin.matcher(duracionTexto);
        if (matcher.matches()) {
            int horas = Integer.parseInt(matcher.group(1));
            int minutos = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;
            return horas * 60 + minutos;
        }

        // 2. Formato: "1 hora y media"
        matcher = formatoHoraYMedia.matcher(duracionTexto);
        if (matcher.matches()) {
            int horas = Integer.parseInt(matcher.group(1));
            return horas * 60 + 30; // Media hora equivale a 30 minutos
        }

        // 3. Formato: "1:30" (1 hora y 30 minutos)
        matcher = formatoHorasYMinutos.matcher(duracionTexto);
        if (matcher.matches()) {
            int horas = Integer.parseInt(matcher.group(1));
            int minutos = Integer.parseInt(matcher.group(2));
            return horas * 60 + minutos;
        }

        // 4. Formato: "2 horas"
        matcher = formatoSoloHoras.matcher(duracionTexto);
        if (matcher.matches()) {
            int horas = Integer.parseInt(matcher.group(1));
            return horas * 60;
        }

        // 5. Formato: "90 minutos"
        matcher = formatoSoloMinutos.matcher(duracionTexto);
        if (matcher.matches()) {
            return Double.parseDouble(matcher.group(1));
        }

        throw new IllegalArgumentException("El formato de 'Duración' no es válido. Ejemplos: '1H 20MIN', '1 hora y media', '1:30', '90 minutos'.");
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

    public void actualizarPelicula(Pelicula peliculaActualizada) {
        try {
            if (peliculaActualizada == null) {
                throw new IllegalArgumentException("La película actualizada no puede ser nula.");
            }

            // Obtener la película original desde la base de datos
            Pelicula peliculaOriginal = peliculaDAO.obtenerPorId(peliculaActualizada.getId());
            if (peliculaOriginal == null) {
                throw new IllegalArgumentException("La película no existe en la base de datos.");
            }

            // Usar los valores originales si los nuevos valores no están disponibles
            String titulo = (peliculaActualizada.getTitulo() == null || peliculaActualizada.getTitulo().trim().isEmpty())
                    ? peliculaOriginal.getTitulo() : peliculaActualizada.getTitulo();

            String director = (peliculaActualizada.getDirector() == null || peliculaActualizada.getDirector().trim().isEmpty())
                    ? peliculaOriginal.getDirector() : peliculaActualizada.getDirector();

            int anio = peliculaActualizada.getAnio() == 0
                    ? peliculaOriginal.getAnio() : peliculaActualizada.getAnio();

            String genero = (peliculaActualizada.getGenero() == null || peliculaActualizada.getGenero().trim().isEmpty())
                    ? peliculaOriginal.getGenero() : peliculaActualizada.getGenero();

            double duracion = peliculaActualizada.getDuracion() == 0
                    ? peliculaOriginal.getDuracion() : peliculaActualizada.getDuracion();

            String descripcion = (peliculaActualizada.getDescripcion() == null || peliculaActualizada.getDescripcion().trim().isEmpty())
                    ? peliculaOriginal.getDescripcion() : peliculaActualizada.getDescripcion();

            String rutaImagen = (peliculaActualizada.getRutaImagen() == null || peliculaActualizada.getRutaImagen().trim().isEmpty())
                    ? peliculaOriginal.getRutaImagen() : peliculaActualizada.getRutaImagen();

            // Crear un nuevo objeto Pelicula con los valores finales
            Pelicula peliculaFinal = new Pelicula(
                    peliculaOriginal.getId(), // ID no cambia
                    titulo,
                    director,
                    anio,
                    genero,
                    duracion,
                    descripcion,
                    rutaImagen
            );

            // Llamar al método modificar del DAO
            peliculaDAO.modificar(peliculaFinal);
            mostrarAlerta("Éxito", "Película actualizada correctamente.", Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            mostrarAlerta("Error", "Error al actualizar la película: " + e.getMessage(), Alert.AlertType.ERROR);
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