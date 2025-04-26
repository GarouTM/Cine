package dao.Interface;

import modelo.Pelicula;
import java.util.List;

public interface IPeliculaDAO {
    /**
     * Inserta una nueva película en la base de datos
     * @param pelicula La película a insertar
     */
    void insertar(Pelicula pelicula);

    /**
     * Obtiene todas las películas de la base de datos
     * @return Lista de todas las películas
     */
    List<Pelicula> obtenerTodas();

    /**
     * Obtiene una película por su ID
     * @param id ID de la película a buscar
     * @return La película encontrada o null si no existe
     */
    Pelicula obtenerPorId(String id);

    /**
     * Elimina una película de la base de datos
     * @param id ID de la película a eliminar
     */
    void eliminar(String id);

    /**
     * Modifica una película existente en la base de datos
     * @param pelicula La película con los datos actualizados
     */
    void modificar(Pelicula pelicula);

    /**
     * Genera el siguiente ID disponible para una nueva película
     * @return String con el formato PELxxx donde xxx es un número secuencial
     */
    String generarSiguienteId();
}