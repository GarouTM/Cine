package modelo;

import java.io.Serializable;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    private String gmail;
    private String gmailPassword;
    private String nombre;
    private String apellido;
    private double saldo;

    // Constructor
    public Usuario(String gmail, String gmailPassword, String nombre, String apellido, double saldo) {
        this.gmail = gmail;
        this.gmailPassword = gmailPassword;
        this.nombre = nombre;
        this.apellido = apellido;
        this.saldo = saldo;
    }

    // Getters y Setters
    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getGmailPassword() {
        return gmailPassword;
    }

    public void setGmailPassword(String gmailPassword) {
        this.gmailPassword = gmailPassword;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    // Metodo para mostrar el nombre completo
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    // toString para depuración y logging
    @Override
    public String toString() {
        return "Usuario{" +
                "gmail='" + gmail + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", saldo=" + saldo +
                '}';
    }

    // Equals y HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return gmail.equals(usuario.gmail);
    }

    @Override
    public int hashCode() {
        return gmail.hashCode();
    }
}