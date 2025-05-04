package modelo;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class Pelicula implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String titulo;
    private String director;
    private int anio;
    private String genero;
    private double duracion; // en minutos
    private String descripcion; // Nueva variable añadida
    private String rutaImagen; // Ruta donde se guarda la imagen

    // Constructor
    public Pelicula(String id, String titulo, String director, int anio,
                    String genero, double duracion, String descripcion, String rutaImagen) {
        this.id = id;
        this.titulo = titulo;
        this.director = director;
        this.anio = anio;
        this.genero = genero;
        this.duracion = duracion;
        this.descripcion = descripcion;
        this.rutaImagen = rutaImagen;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public double getDuracion() {
        return duracion;
    }

    public void setDuracion(double duracion) {
        this.duracion = duracion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    /**
     * Calcula el precio dinámicamente.
     * El precio es $7 siempre, salvo los miércoles que tiene un 50% de descuento.
     *
     * @return El precio de la película.
     */
    public double getPrecio() {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.WEDNESDAY) {
            return 7.0 * 0.5; // Descuento del 50% los miércoles
        }
        return 7.0; // Precio normal
    }

    @Override
    public String toString() {
        return "Pelicula{" +
                "id='" + id + '\'' +
                ", titulo='" + titulo + '\'' +
                ", director='" + director + '\'' +
                ", anio=" + anio +
                ", genero='" + genero + '\'' +
                ", duracion=" + duracion +
                ", descripcion='" + descripcion + '\'' +
                ", rutaImagen='" + rutaImagen + '\'' +
                ", precio=" + getPrecio() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pelicula pelicula = (Pelicula) o;
        return id.equals(pelicula.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}