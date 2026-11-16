package submarineattack.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Modelo principal (la "M" de MVC). Contiene toda la lógica del juego y NO
 * conoce nada de la interfaz gráfica: solo avisa eventos a sus observadores.
 *
 * Responsabilidades: generar la serie de 12 barcos (máx. 3 a la vez), crear
 * cargas, resolver colisiones y explosiones, aplicar puntaje y daño, pasar de
 * nivel y gobernar la máquina de estados.
 */
public class Juego {

    private final Random random = new Random();
    private final List<JuegoListener> listeners = new ArrayList<>();
    private final MaquinaEstados maquina = new MaquinaEstados();
    private final Submarino submarino = new Submarino();
    private final List<Barco> barcos = new ArrayList<>();
    private final List<CargaProfundidad> cargas = new ArrayList<>();
    private final String jugador;

    private Nivel nivel;
    private int puntos;
    private int barcosLanzados;
    private double tiempoParaProximoBarco;

    public Juego(String jugador, Nivel nivelInicial) {
        this.jugador = jugador;
        this.nivel = nivelInicial;
        prepararNivel();
    }

    // ------------------------------------------------------------------
    // Observadores
    // ------------------------------------------------------------------

    public void agregarListener(JuegoListener listener) {
        listeners.add(listener);
    }

    /** Emite el estado inicial para que los observadores ya registrados se sincronicen. */
    public void iniciar() {
        notificarNivelIniciado();
        notificarPuntos();
        notificarVida();
    }

    // ------------------------------------------------------------------
    // Ciclo de juego
    // ------------------------------------------------------------------

    public void moverSubmarino(int dx, int dy) {
        submarino.setDireccion(dx, dy);
    }

    /** Avanza la simulación dt segundos. Solo actúa en el estado JUGANDO. */
    public void actualizar(double dt) {
        if (maquina.getActual() != EstadoJuego.JUGANDO) {
            return;
        }
        submarino.actualizar(dt);
        generarBarcos(dt);
        actualizarBarcos(dt);
        actualizarCargas(dt);
        if (maquina.getActual() == EstadoJuego.JUGANDO) {
            verificarFinDeNivel();
        }
    }

    private void generarBarcos(double dt) {
        tiempoParaProximoBarco -= dt;
        boolean quedanPorSalir = barcosLanzados < Constantes.BARCOS_POR_SERIE;
        boolean hayLugar = barcos.size() < Constantes.MAX_BARCOS_SIMULTANEOS;
        if (quedanPorSalir && hayLugar && tiempoParaProximoBarco <= 0) {
            barcos.add(new Barco(nivel, random));
            barcosLanzados++;
            tiempoParaProximoBarco = 1.0 + random.nextDouble() * 2.0;
        }
    }

    private void actualizarBarcos(double dt) {
        Iterator<Barco> it = barcos.iterator();
        while (it.hasNext()) {
            Barco barco = it.next();
            barco.actualizar(dt);
            if (barco.listoParaLanzar()) {
                cargas.add(new CargaProfundidad(barco.getX(), nivel, random));
                barco.reiniciarTemporizador();
            }
            if (!barco.isActivo()) {
                it.remove();
            }
        }
    }

    private void actualizarCargas(double dt) {
        Iterator<CargaProfundidad> it = cargas.iterator();
        while (it.hasNext() && maquina.getActual() == EstadoJuego.JUGANDO) {
            CargaProfundidad carga = it.next();
            boolean estabaCayendo = carga.getEstado() == CargaProfundidad.Estado.CAYENDO;

            carga.actualizar(dt);

            // Si tocamos una carga, explota
            if (estabaCayendo && carga.getEstado() == CargaProfundidad.Estado.CAYENDO
                    && carga.colisionaCon(submarino)) {
                carga.explotar();
            }
            // El daño se aplica una sola vez: justo en el instante en que explota
            if (estabaCayendo && carga.getEstado() == CargaProfundidad.Estado.EXPLOTANDO) {
                resolverExplosion(carga);
            }
            if (carga.getEstado() == CargaProfundidad.Estado.TERMINADA) {
                it.remove();
            }
        }
    }

    /** Aplica puntaje y daño según la distancia entre la explosión y el submarino. */
    private void resolverExplosion(CargaProfundidad carga) {
        double distancia = carga.distanciaEnMetros(submarino);
        ZonaImpacto zona = ZonaImpacto.desdeDistancia(distancia);

        if (zona.getPuntos() > 0) {
            sumarPuntos(zona.getPuntos());
        }
        if (zona.pierdeVida()) {
            submarino.perderVida();
        } else if (zona.getDanioPorcentaje() > 0) {
            submarino.recibirDanio(zona.getDanioPorcentaje());
        }
        if (zona.produceDanio()) {
            for (JuegoListener l : listeners) {
                l.submarinoDanado();
            }
            notificarVida();
        }
        if (submarino.estaDestruido()) {
            cambiarEstado(EstadoJuego.GAME_OVER);
        }
    }

    /** Suma puntos y otorga una vida extra por cada 500 puntos que se alcanzan. */
    private void sumarPuntos(int cantidad) {
        int vidasExtra = (puntos + cantidad) / Constantes.PUNTOS_VIDA_EXTRA
                - puntos / Constantes.PUNTOS_VIDA_EXTRA;
        puntos += cantidad;
        notificarPuntos();
        if (vidasExtra > 0) {
            for (int i = 0; i < vidasExtra; i++) {
                submarino.ganarVida();
            }
            notificarVida();
        }
    }

    // ------------------------------------------------------------------
    // Niveles
    // ------------------------------------------------------------------

    private void verificarFinDeNivel() {
        boolean serieTerminada = barcosLanzados == Constantes.BARCOS_POR_SERIE && barcos.isEmpty();
        if (serieTerminada && cargas.isEmpty()) {
            completarNivel();
        }
    }

    private void completarNivel() {
        Nivel completado = nivel;
        sumarPuntos(Constantes.PUNTOS_POR_NIVEL);
        for (JuegoListener l : listeners) {
            l.nivelCompletado(completado.getNumero(), puntos);   // guardado automático
        }
        if (completado.esUltimo()) {
            cambiarEstado(EstadoJuego.VICTORIA);
        } else {
            nivel = completado.siguiente();
            prepararNivel();
            notificarNivelIniciado();
        }
    }

    private void prepararNivel() {
        barcos.clear();
        cargas.clear();
        barcosLanzados = 0;
        tiempoParaProximoBarco = 0.5;
    }

    // ------------------------------------------------------------------
    // Máquina de estados
    // ------------------------------------------------------------------

    public void alternarPausa() {
        if (maquina.getActual() == EstadoJuego.JUGANDO) {
            cambiarEstado(EstadoJuego.PAUSA);
        } else if (maquina.getActual() == EstadoJuego.PAUSA) {
            cambiarEstado(EstadoJuego.JUGANDO);
        }
    }

    public void pausar() {
        cambiarEstado(EstadoJuego.PAUSA);
    }

    private void cambiarEstado(EstadoJuego destino) {
        if (maquina.transicionarA(destino)) {
            for (JuegoListener l : listeners) {
                l.estadoCambiado(destino);
            }
        }
    }

    // ------------------------------------------------------------------
    // Notificaciones y getters
    // ------------------------------------------------------------------

    private void notificarVida() {
        for (JuegoListener l : listeners) {
            l.vidaCambiada(submarino.getVidas(), submarino.getSalud());
        }
    }

    private void notificarPuntos() {
        for (JuegoListener l : listeners) {
            l.puntosCambiados(puntos);
        }
    }

    private void notificarNivelIniciado() {
        for (JuegoListener l : listeners) {
            l.nivelIniciado(nivel.getNumero());
        }
    }

    public EstadoJuego getEstado() {
        return maquina.getActual();
    }

    public boolean estaTerminado() {
        return maquina.estaTerminado();
    }

    public Submarino getSubmarino() {
        return submarino;
    }

    /** Vista de solo lectura: la UI puede dibujar pero no modificar la lista. */
    public List<Barco> getBarcos() {
        return Collections.unmodifiableList(barcos);
    }

    public List<CargaProfundidad> getCargas() {
        return Collections.unmodifiableList(cargas);
    }

    public int getPuntos() {
        return puntos;
    }

    public Nivel getNivel() {
        return nivel;
    }

    public String getJugador() {
        return jugador;
    }
}
