package submarineattack.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import submarineattack.modelo.Juego;
import submarineattack.modelo.JuegoListener;

/**
 * Pantalla de juego: barra superior (jugador, indicador de vida, puntos y
 * nivel) y el lienzo con el escenario. Se suscribe como observador del modelo
 * para refrescar puntos y nivel por eventos.
 */
public class PanelJuego extends JPanel implements JuegoListener {

    private final IndicadorVida indicadorVida = new IndicadorVida();
    private final LienzoJuego lienzo = new LienzoJuego();
    private final JLabel etiquetaJugador = crearEtiqueta();
    private final JLabel etiquetaPuntos = crearEtiqueta();
    private final JLabel etiquetaNivel = crearEtiqueta();

    public PanelJuego() {
        setLayout(new BorderLayout());

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 5));
        barra.setBackground(new Color(15, 25, 45));
        barra.add(etiquetaJugador);
        barra.add(indicadorVida);
        barra.add(etiquetaPuntos);
        barra.add(etiquetaNivel);

        add(barra, BorderLayout.NORTH);
        add(lienzo, BorderLayout.CENTER);
    }

    private JLabel crearEtiqueta() {
        JLabel etiqueta = new JLabel();
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 18));
        etiqueta.setForeground(Color.WHITE);
        return etiqueta;
    }

    /** Conecta la vista con un juego nuevo: registra los observadores. */
    public void conectar(Juego juego) {
        etiquetaJugador.setText("Jugador: " + juego.getJugador());
        lienzo.setJuego(juego);
        juego.agregarListener(indicadorVida);
        juego.agregarListener(lienzo);
        juego.agregarListener(this);
    }

    @Override
    public void puntosCambiados(int puntos) {
        etiquetaPuntos.setText("Puntos: " + puntos);
    }

    @Override
    public void nivelIniciado(int numeroNivel) {
        etiquetaNivel.setText("Nivel: " + numeroNivel);
    }

    public void agregarEscuchaTeclado(KeyListener listener) {
        lienzo.addKeyListener(listener);
    }

    public void enfocar() {
        lienzo.requestFocusInWindow();
    }

    public void redibujar() {
        lienzo.repaint();
    }
}
