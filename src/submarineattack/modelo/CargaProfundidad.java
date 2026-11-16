package submarineattack.modelo;

import java.util.Random;

/**
 * Carga de profundidad lanzada por un barco.
 *
 * - No es guiada: se hunde en línea recta desde la posición del barco.
 * - Tiene un detonador con una profundidad aleatoria entre 300 y 700 m,
 *   definida en el momento en que se lanza.
 * - Estados: CAYENDO -> EXPLOTANDO (se muestra la imagen de explosión un
 *   instante) -> TERMINADA (el Juego la elimina).
 */
public class CargaProfundidad extends ObjetoJuego {

    public enum Estado { CAYENDO, EXPLOTANDO, TERMINADA }

    private static final int ANCHO = 14;
    private static final int ALTO = 18;
    private static final double VELOCIDAD_CAIDA_BASE = 90;   // px/s en nivel 1
    private static final double DURACION_EXPLOSION = 0.5;    // segundos

    private final double velocidadCaida;
    private final double yDetonacion;
    private Estado estado = Estado.CAYENDO;
    private double tiempoExplotando;

    public CargaProfundidad(double xBarco, Nivel nivel, Random random) {
        super(xBarco, Constantes.Y_SUPERFICIE, ANCHO, ALTO);
        this.velocidadCaida = VELOCIDAD_CAIDA_BASE * nivel.getFactorVelocidad();
        int profundidadDetonador = Constantes.PROF_MIN_DETONADOR
                + random.nextInt(Constantes.PROF_MAX_DETONADOR - Constantes.PROF_MIN_DETONADOR + 1);
        this.yDetonacion = Constantes.profundidadAY(profundidadDetonador);
    }

    @Override
    public void actualizar(double dt) {
        switch (estado) {
            case CAYENDO:
                y += velocidadCaida * dt;          // caída recta: x no cambia
                if (y >= yDetonacion) {
                    explotar();
                }
                break;
            case EXPLOTANDO:
                tiempoExplotando += dt;
                if (tiempoExplotando >= DURACION_EXPLOSION) {
                    estado = Estado.TERMINADA;
                }
                break;
            default:
                break;
        }
    }

    /** Detona la carga (por profundidad o porque el submarino la tocó). */
    public void explotar() {
        if (estado == Estado.CAYENDO) {
            estado = Estado.EXPLOTANDO;
            tiempoExplotando = 0;
        }
    }

    public Estado getEstado() {
        return estado;
    }
}
