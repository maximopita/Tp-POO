package submarineattack.vista;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import submarineattack.modelo.Constantes;
import submarineattack.modelo.JuegoListener;

/**
 * Indicador de vida del submarino: cantidad de vidas y barra de salud.
 * Es un observador del modelo: NO consulta al juego en cada cuadro, solo se
 * redibuja cuando recibe el evento vidaCambiada.
 */
public class IndicadorVida extends JPanel implements JuegoListener {

    private int vidas = Constantes.VIDAS_INICIALES;
    private int salud = Constantes.SALUD_MAXIMA;

    public IndicadorVida() {
        setPreferredSize(new Dimension(300, 40));
        setOpaque(false);
    }

    @Override
    public void vidaCambiada(int vidas, int salud) {
        this.vidas = vidas;
        this.salud = salud;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2.setColor(Color.WHITE);
        g2.drawString("Vidas: " + Math.max(vidas, 0), 0, 26);

        int xBarra = 100;
        int anchoBarra = 180;
        int altoBarra = 18;
        int anchoRelleno = anchoBarra * Math.max(salud, 0) / Constantes.SALUD_MAXIMA;

        g2.setColor(new Color(60, 60, 60));
        g2.fillRect(xBarra, 10, anchoBarra, altoBarra);
        g2.setColor(salud > 60 ? new Color(60, 200, 80) : salud > 30 ? new Color(230, 200, 40) : new Color(220, 50, 50));
        g2.fillRect(xBarra, 10, anchoRelleno, altoBarra);
        g2.setColor(Color.WHITE);
        g2.drawRect(xBarra, 10, anchoBarra, altoBarra);
    }
}
