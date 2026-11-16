package submarineattack.modelo;

import java.util.Random;

/**
 * Barco enemigo. Navega a profundidad cero cruzando la pantalla de lado a lado.
 *
 * - Empieza desde la izquierda o la derecha de forma aleatoria.
 * - Da una cantidad aleatoria de pasadas (1 a 3): al salir por un lado vuelve
 *   a cruzar en sentido contrario hasta agotar las pasadas.
 * - Mientras está en pantalla usa un temporizador aleatorio para lanzar cargas.
 *   El Barco solo avisa que está listo (listoParaLanzar); quien crea la carga
 *   es Juego, así el barco no necesita conocer a las cargas (bajo acoplamiento).
 */
public class Barco extends ObjetoJuego {

    private static final int ANCHO = 90;
    private static final int ALTO = 28;
    private static final double VELOCIDAD_BASE = 120;      // px/s en nivel 1
    private static final int MAX_PASADAS = 3;
    private static final double LANZAMIENTO_MIN = 2.0;     // segundos
    private static final double LANZAMIENTO_MAX = 4.5;

    private final Random random;
    private final double velocidad;
    private int direccion;               // 1: izquierda -> derecha, -1: derecha -> izquierda
    private int pasadasRestantes;
    private double tiempoParaLanzar;
    private boolean activo = true;

    public Barco(Nivel nivel, Random random) {
        super(0, Constantes.Y_SUPERFICIE, ANCHO, ALTO);
        this.random = random;
        this.velocidad = VELOCIDAD_BASE * nivel.getFactorVelocidad();
        this.pasadasRestantes = 1 + random.nextInt(MAX_PASADAS);
        this.direccion = random.nextBoolean() ? 1 : -1;
        this.x = (direccion == 1) ? -ANCHO / 2.0 : Constantes.ANCHO_MUNDO + ANCHO / 2.0;
        reiniciarTemporizador();
    }

    @Override
    public void actualizar(double dt) {
        x += direccion * velocidad * dt;
        if (estaEnPantalla()) {
            tiempoParaLanzar -= dt;
        }

        boolean salioDelTodo = (direccion == 1)
                ? x > Constantes.ANCHO_MUNDO + getAncho() / 2.0
                : x < -getAncho() / 2.0;
        if (salioDelTodo) {
            pasadasRestantes--;
            if (pasadasRestantes <= 0) {
                activo = false;
            } else {
                direccion = -direccion;   // vuelve a cruzar en sentido contrario
            }
        }
    }

    public boolean estaEnPantalla() {
        return x >= 0 && x <= Constantes.ANCHO_MUNDO;
    }

    public boolean listoParaLanzar() {
        return activo && estaEnPantalla() && tiempoParaLanzar <= 0;
    }

    public void reiniciarTemporizador() {
        tiempoParaLanzar = LANZAMIENTO_MIN + random.nextDouble() * (LANZAMIENTO_MAX - LANZAMIENTO_MIN);
    }

    public boolean isActivo() {
        return activo;
    }
}
