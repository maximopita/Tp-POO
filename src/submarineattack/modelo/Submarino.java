package submarineattack.modelo;

/**
 * Submarino del jugador. Se mueve a izquierda/derecha y puede subir o bajar
 * entre los 300 y los 800 metros de profundidad.
 *
 * Maneja sus vidas y su salud: la salud (0..100) representa la vida actual;
 * cuando llega a 0 se pierde una vida y la salud se recarga al 100%.
 */
public class Submarino extends ObjetoJuego {

    private static final int ANCHO = 70;
    private static final int ALTO = 26;
    private static final double VELOCIDAD_X = 220;  // píxeles por segundo
    private static final double VELOCIDAD_Y = 160;

    private int direccionX;   // -1 izquierda, 0 quieto, 1 derecha
    private int direccionY;   // -1 sube, 0 quieto, 1 baja
    private int vidas = Constantes.VIDAS_INICIALES;
    private int salud = Constantes.SALUD_MAXIMA;

    public Submarino() {
        super(Constantes.ANCHO_MUNDO / 2.0,
                Constantes.profundidadAY(Constantes.PROF_MIN_SUBMARINO),
                ANCHO, ALTO);
    }

    public void setDireccion(int dx, int dy) {
        this.direccionX = Integer.signum(dx);
        this.direccionY = Integer.signum(dy);
    }

    @Override
    public void actualizar(double dt) {
        x += direccionX * VELOCIDAD_X * dt;
        y += direccionY * VELOCIDAD_Y * dt;

        // El submarino no puede salirse de la pantalla ni de su rango de profundidad
        x = Math.max(getAncho() / 2.0, Math.min(Constantes.ANCHO_MUNDO - getAncho() / 2.0, x));
        y = Math.max(Constantes.profundidadAY(Constantes.PROF_MIN_SUBMARINO),
                Math.min(Constantes.profundidadAY(Constantes.PROF_MAX_SUBMARINO), y));
    }

    public double getProfundidad() {
        return Constantes.yAProfundidad(y);
    }

    /**
     * Resta un porcentaje de salud; si se agota, se pierde una vida.
     *
     * Decisión de diseño: la consigna solo indica "pierde una vida" para el
     * golpe directo (menos de 10 m). Acá se extiende esa regla: el daño
     * acumulado de varios golpes de 30%/50% dentro de la misma vida también
     * puede llegar a 0 y hacer perder una vida completa.
     */
    public void recibirDanio(int porcentaje) {
        salud -= porcentaje;
        if (salud <= 0) {
            perderVida();
        }
    }

    public void perderVida() {
        vidas--;
        salud = Constantes.SALUD_MAXIMA;
    }

    public void ganarVida() {
        vidas++;
    }

    public boolean estaDestruido() {
        return vidas <= 0;
    }

    public int getVidas() {
        return vidas;
    }

    public int getSalud() {
        return salud;
    }
}
