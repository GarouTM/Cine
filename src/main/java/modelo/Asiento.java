package modelo;

import java.util.List;

public class Asiento {
    private String pelicula; // Nombre de la película
    private String horario; // Horario de la película
    private List<String> asientosOcupados; // Lista de códigos de asientos ocupados (e.g. A1, B2)
    private long fechaActualizacion; // Timestamp de la última actualización

    // Constructor
    public Asiento(String pelicula, String horario, List<String> asientosOcupados, long fechaActualizacion) {
        this.pelicula = pelicula;
        this.horario = horario;
        this.asientosOcupados = asientosOcupados;
        this.fechaActualizacion = fechaActualizacion;
    }

    // Getters y Setters
    public String getPelicula() {
        return pelicula;
    }

    public void setPelicula(String pelicula) {
        this.pelicula = pelicula;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public List<String> getAsientosOcupados() {
        return asientosOcupados;
    }

    public void setAsientosOcupados(List<String> asientosOcupados) {
        this.asientosOcupados = asientosOcupados;
    }

    public long getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(long fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    // Método toString para depuración
    @Override
    public String toString() {
        return "Asiento{" +
                "pelicula='" + pelicula + '\'' +
                ", horario='" + horario + '\'' +
                ", asientosOcupados=" + asientosOcupados +
                ", fechaActualizacion=" + fechaActualizacion +
                '}';
    }
}