package submarineattack.modelo;

import java.awt.geom.Rectangle2D;

/**
 * Clase abstracta base de todo lo que "existe" en el mundo del juego
 * (submarino, barcos y cargas de profundidad).
 *
 * Concentra lo común: posición del centro (x, y), tamaño, rectángulo de
 * colisión y distancia a otro objeto. Cada subclase define cómo se mueve en
 * actualizar(dt): ese es el punto de polimorfismo.
 */
public abstract class ObjetoJuego {

    protected double x;
    protected double y;
    private final int ancho;
    private final int alto;

    protected ObjetoJuego(double x, double y, int ancho, int alto) {
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
    }

    /** Avanza el objeto dt segundos. Cada subclase implementa su movimiento. */
    public abstract void actualizar(double dt);

    public Rectangle2D.Double getLimites() {
        return new Rectangle2D.Double(x - ancho / 2.0, y - alto / 2.0, ancho, alto);
    }

    public boolean colisionaCon(ObjetoJuego otro) {
        return getLimites().intersects(otro.getLimites());
    }

    /**
     * Distancia entre centros expresada en metros (no en píxeles).
     *
     * Decisión de diseño: la consigna solo define una escala de píxeles por
     * metro para la profundidad (eje Y). Como no especifica una escala
     * horizontal, se reutiliza la misma (PX_POR_METRO) para el eje X, de
     * forma que la distancia se pueda medir con una sola fórmula euclídea.
     */
    public double distanciaEnMetros(ObjetoJuego otro) {
        double dx = x - otro.x;
        double dy = y - otro.y;
        return Math.hypot(dx, dy) / Constantes.PX_POR_METRO;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getAncho() {
        return ancho;
    }

    public int getAlto() {
        return alto;
    }
}
