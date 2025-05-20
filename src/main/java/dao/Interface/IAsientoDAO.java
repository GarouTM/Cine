package dao.Interface;

import java.util.List;

public interface IAsientoDAO {

    /**
     * Registra los asientos ocupados para una película y un horario específicos.
     *
     * @param pelicula        Nombre de la película.
     * @param horario         Horario de la película.
     * @param asientosOcupados Lista de códigos de los asientos ocupados.
     */
    void registrarAsientos(String pelicula, String horario, List<String> asientosOcupados);

    /**
     * Obtiene los asientos ocupados para una película y un horario específicos.
     *
     * @param pelicula Nombre de la película.
     * @param horario  Horario de la película.
     * @return Lista de códigos de los asientos ocupados.
     */
    List<String> obtenerAsientosOcupados(String pelicula, String horario);
}