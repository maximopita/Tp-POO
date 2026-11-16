package submarineattack.modelo;

/**
 * Zonas de daño según la distancia (en metros) entre la explosión de una carga
 * y el submarino. Cada zona define puntos otorgados y daño producido, tal cual
 * lo indica la consigna:
 *
 *   más de 100 m   -> +30 puntos, sin daño
 *   50 a 100 m     -> +10 puntos, -30% de vida
 *   10 a 50 m      -> 0 puntos,  -50% de vida
 *   menos de 10 m  -> 0 puntos,  se pierde una vida completa
 */
public enum ZonaImpacto {
    LEJOS(30, 0, false),
    MEDIA(10, 30, false),
    CERCA(0, 50, false),
    DIRECTA(0, 0, true);

    private final int puntos;
    private final int danioPorcentaje;
    private final boolean pierdeVida;

    ZonaImpacto(int puntos, int danioPorcentaje, boolean pierdeVida) {
        this.puntos = puntos;
        this.danioPorcentaje = danioPorcentaje;
        this.pierdeVida = pierdeVida;
    }

    public int getPuntos() {
        return puntos;
    }

    public int getDanioPorcentaje() {
        return danioPorcentaje;
    }

    public boolean pierdeVida() {
        return pierdeVida;
    }

    public boolean produceDanio() {
        return pierdeVida || danioPorcentaje > 0;
    }

    public static ZonaImpacto desdeDistancia(double metros) {
        if (metros > 100) {
            return LEJOS;
        }
        if (metros > 50) {
            return MEDIA;
        }
        if (metros > 10) {
            return CERCA;
        }
        return DIRECTA;
    }
}
