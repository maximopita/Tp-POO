package submarineattack.controlador;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import submarineattack.modelo.EstadoJuego;
import submarineattack.modelo.Juego;
import submarineattack.modelo.JuegoListener;
import submarineattack.modelo.Nivel;
import submarineattack.persistencia.PersistenciaException;
import submarineattack.persistencia.RegistroJugador;
import submarineattack.persistencia.RepositorioJugadores;
import submarineattack.vista.VentanaPrincipal;

/**
 * Controlador (la "C" de MVC). Conecta la vista con el modelo:
 *  - maneja login, menú y el inicio de partidas;
 *  - corre el bucle del juego con un Timer y traduce las teclas en movimiento;
 *  - como observador del modelo, hace el guardado automático en los eventos
 *    de cambio de nivel y de fin de partida.
 */
public class ControladorJuego implements JuegoListener {

    private static final int INTERVALO_MS = 16;          // ~60 cuadros por segundo
    private static final double DT_MAXIMO = 0.05;        // evita saltos grandes si se traba un cuadro

    private final RepositorioJugadores repositorio = new RepositorioJugadores("records.txt");
    private final VentanaPrincipal ventana = new VentanaPrincipal();
    private final Set<Integer> teclasPresionadas = new HashSet<>();
    private final Timer timerJuego = new Timer(INTERVALO_MS, e -> tick());

    private RegistroJugador jugadorActual;
    private Juego juego;
    private long ultimoTick;

    public void iniciar() {
        ventana.getPanelLogin().addIngresarListener(e -> iniciarSesion());
        ventana.getPanelMenu().addNivelListener(e -> iniciarPartida(Integer.parseInt(e.getActionCommand())));
        ventana.getPanelJuego().agregarEscuchaTeclado(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                teclaPresionada(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                teclasPresionadas.remove(e.getKeyCode());
            }
        });
        ventana.mostrar(VentanaPrincipal.LOGIN);
        ventana.setVisible(true);
    }

    // ------------------------------------------------------------------
    // Login y menú
    // ------------------------------------------------------------------

    private void iniciarSesion() {
        String nombre = ventana.getPanelLogin().getNombre().trim().replace(";", "");
        if (nombre.isEmpty()) {
            ventana.mostrarMensaje("Ingresá un nombre de jugador.");
            return;
        }
        try {
            jugadorActual = repositorio.buscar(nombre);
        } catch (PersistenciaException ex) {
            ventana.mostrarMensaje(ex.getMessage() + ". Se continúa sin datos guardados.");
            jugadorActual = new RegistroJugador(nombre);
        }
        mostrarMenu();
    }

    private void mostrarMenu() {
        timerJuego.stop();
        ventana.getPanelMenu().actualizar(jugadorActual);
        ventana.mostrar(VentanaPrincipal.MENU);
    }

    // ------------------------------------------------------------------
    // Partida
    // ------------------------------------------------------------------

    private void iniciarPartida(int numeroNivel) {
        juego = new Juego(jugadorActual.getNombre(), Nivel.desdeNumero(numeroNivel));
        juego.agregarListener(this);
        ventana.getPanelJuego().conectar(juego);
        juego.iniciar();

        teclasPresionadas.clear();
        ventana.mostrar(VentanaPrincipal.JUEGO);
        SwingUtilities.invokeLater(() -> ventana.getPanelJuego().enfocar());

        ultimoTick = System.nanoTime();
        timerJuego.start();
    }

    private void tick() {
        long ahora = System.nanoTime();
        double dt = Math.min((ahora - ultimoTick) / 1_000_000_000.0, DT_MAXIMO);
        ultimoTick = ahora;

        aplicarTeclas();
        juego.actualizar(dt);
        ventana.getPanelJuego().redibujar();
    }

    private void teclaPresionada(int codigo) {
        if (juego == null) {
            return;
        }
        // add() devuelve false si la tecla ya estaba apretada (repetición automática)
        boolean esNueva = teclasPresionadas.add(codigo);
        if (codigo == KeyEvent.VK_P && esNueva) {
            juego.alternarPausa();
        } else if (codigo == KeyEvent.VK_ENTER && juego.estaTerminado()) {
            mostrarMenu();
        }
    }

    private void aplicarTeclas() {
        int dx = (teclasPresionadas.contains(KeyEvent.VK_RIGHT) ? 1 : 0)
                - (teclasPresionadas.contains(KeyEvent.VK_LEFT) ? 1 : 0);
        int dy = (teclasPresionadas.contains(KeyEvent.VK_DOWN) ? 1 : 0)
                - (teclasPresionadas.contains(KeyEvent.VK_UP) ? 1 : 0);
        juego.moverSubmarino(dx, dy);
    }

    // ------------------------------------------------------------------
    // Eventos del modelo: guardado automático
    // ------------------------------------------------------------------

    @Override
    public void nivelCompletado(int numeroNivel, int puntos) {
        jugadorActual.actualizarRecord(puntos);
        jugadorActual.desbloquearNivel(numeroNivel + 1);
        guardar();
    }

    @Override
    public void estadoCambiado(EstadoJuego nuevoEstado) {
        if (nuevoEstado == EstadoJuego.GAME_OVER || nuevoEstado == EstadoJuego.VICTORIA) {
            jugadorActual.actualizarRecord(juego.getPuntos());
            guardar();
        }
    }

    private void guardar() {
        try {
            repositorio.guardar(jugadorActual);
        } catch (PersistenciaException ex) {
            // Se muestra después, fuera del ciclo de actualización, para no
            // abrir un diálogo modal en medio del recorrido de las listas.
            SwingUtilities.invokeLater(() -> {
                juego.pausar();
                ventana.mostrarMensaje(ex.getMessage());
            });
        }
    }
}
