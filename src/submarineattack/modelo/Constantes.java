package submarineattack.modelo;

/**
 * Valores fijos del juego tomados de la consigna (profundidades, cantidades,
 * puntajes). Centralizarlos evita "números mágicos" repartidos por el código.
 *
 * Sistema de coordenadas: el mundo mide ANCHO_MUNDO x ALTO_MUNDO píxeles.
 * La superficie (profundidad 0 m) está en Y_SUPERFICIE y cada metro de
 * profundidad equivale a PX_POR_METRO píxeles.
 */
public final class Constantes {

    private Constantes() { }

    // Mundo
    public static final int ANCHO_MUNDO = 1000;
    public static final int ALTO_MUNDO = 700;
    public static final int Y_SUPERFICIE = 60;
    public static final double PX_POR_METRO = 0.75;

    // Profundidades (en metros) según la consigna
    public static final int PROF_MIN_SUBMARINO = 300;
    public static final int PROF_MAX_SUBMARINO = 800;
    public static final int PROF_MIN_DETONADOR = 300;
    public static final int PROF_MAX_DETONADOR = 700;

    // Barcos
    public static final int BARCOS_POR_SERIE = 12;
    public static final int MAX_BARCOS_SIMULTANEOS = 3;

    // Vidas y puntaje
    public static final int VIDAS_INICIALES = 3;
    public static final int SALUD_MAXIMA = 100;
    public static final int PUNTOS_POR_NIVEL = 200;
    public static final int PUNTOS_VIDA_EXTRA = 500;

    /** Convierte una profundidad en metros a la coordenada Y del mundo. */
    public static double profundidadAY(double metros) {
        return Y_SUPERFICIE + metros * PX_POR_METRO;
    }

    /** Convierte una coordenada Y del mundo a profundidad en metros. */
    public static double yAProfundidad(double y) {
        return (y - Y_SUPERFICIE) / PX_POR_METRO;
    }
}
