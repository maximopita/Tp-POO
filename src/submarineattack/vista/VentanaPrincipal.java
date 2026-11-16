package submarineattack.vista;

import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Ventana única de la aplicación. Usa un CardLayout para mostrar de a una las
 * pantallas: login, menú principal y juego.
 */
public class VentanaPrincipal extends JFrame {

    public static final String LOGIN = "login";
    public static final String MENU = "menu";
    public static final String JUEGO = "juego";

    private final CardLayout cartas = new CardLayout();
    private final JPanel contenedor = new JPanel(cartas);
    private final PanelLogin panelLogin = new PanelLogin();
    private final PanelMenu panelMenu = new PanelMenu();
    private final PanelJuego panelJuego = new PanelJuego();

    public VentanaPrincipal() {
        super("Submarine Attack");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        contenedor.setPreferredSize(new Dimension(1000, 750));
        contenedor.add(panelLogin, LOGIN);
        contenedor.add(panelMenu, MENU);
        contenedor.add(panelJuego, JUEGO);
        add(contenedor);

        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    public void mostrar(String pantalla) {
        cartas.show(contenedor, pantalla);
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Submarine Attack", JOptionPane.INFORMATION_MESSAGE);
    }

    public PanelLogin getPanelLogin() {
        return panelLogin;
    }

    public PanelMenu getPanelMenu() {
        return panelMenu;
    }

    public PanelJuego getPanelJuego() {
        return panelJuego;
    }
}
