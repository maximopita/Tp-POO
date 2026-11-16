package submarineattack.modelo;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Máquina de estados finita (FSM) del juego.
 *
 * Define de forma explícita qué transiciones son válidas:
 *   JUGANDO -> PAUSA | GAME_OVER | VICTORIA
 *   PAUSA   -> JUGANDO
 *   GAME_OVER y VICTORIA son estados finales (no tienen salida).
 *
 * Se usa un EnumMap con EnumSet porque las claves son un enum: es más
 * rápido y liviano en memoria que un HashMap común.
 */
public class MaquinaEstados {

    private static final Map<EstadoJuego, Set<EstadoJuego>> TRANSICIONES =
            new EnumMap<>(EstadoJuego.class);

    static {
        TRANSICIONES.put(EstadoJuego.JUGANDO,
                EnumSet.of(EstadoJuego.PAUSA, EstadoJuego.GAME_OVER, EstadoJuego.VICTORIA));
        TRANSICIONES.put(EstadoJuego.PAUSA, EnumSet.of(EstadoJuego.JUGANDO));
        TRANSICIONES.put(EstadoJuego.GAME_OVER, EnumSet.noneOf(EstadoJuego.class));
        TRANSICIONES.put(EstadoJuego.VICTORIA, EnumSet.noneOf(EstadoJuego.class));
    }

    private EstadoJuego actual = EstadoJuego.JUGANDO;

    public EstadoJuego getActual() {
        return actual;
    }

    public boolean puedeIrA(EstadoJuego destino) {
        return TRANSICIONES.get(actual).contains(destino);
    }

    /** Cambia de estado solo si la transición es válida. Devuelve si lo hizo. */
    public boolean transicionarA(EstadoJuego destino) {
        if (!puedeIrA(destino)) {
            return false;
        }
        actual = destino;
        return true;
    }

    public boolean estaTerminado() {
        return actual == EstadoJuego.GAME_OVER || actual == EstadoJuego.VICTORIA;
    }
}
