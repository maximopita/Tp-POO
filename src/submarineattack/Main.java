package submarineattack;

import javax.swing.SwingUtilities;
import submarineattack.controlador.ControladorJuego;

/**
 * Punto de entrada de la aplicación. Arranca el controlador dentro del
 * hilo de eventos de Swing (EDT), como corresponde para cualquier UI Swing.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ControladorJuego().iniciar());
    }
}
