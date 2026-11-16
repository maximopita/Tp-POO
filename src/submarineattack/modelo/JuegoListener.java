package submarineattack.modelo;

/**
 * Observador del modelo (patrón Observer). El modelo (Juego) avisa los eventos
 * importantes y quien esté interesado (UI, controlador) los recibe.
 *
 * Los métodos son "default" (vacíos) para que cada observador implemente
 * únicamente los eventos que le interesan (principio de segregación de
 * interfaces, la "I" de SOLID, sin obligar a escribir métodos vacíos).
 */
public interface JuegoListener {

    /** Cambió la cantidad de vidas o la salud de la vida actual (0..100). */
    default void vidaCambiada(int vidas, int salud) { }

    /** El submarino recibió daño (dispara el efecto de fading). */
    default void submarinoDanado() { }

    default void puntosCambiados(int puntos) { }

    default void nivelIniciado(int numeroNivel) { }

    /** Se completó un nivel (momento del guardado automático). */
    default void nivelCompletado(int numeroNivel, int puntos) { }

    /** Cambió el estado de la máquina de estados. */
    default void estadoCambiado(EstadoJuego nuevoEstado) { }
}
