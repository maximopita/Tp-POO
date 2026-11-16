package submarineattack.vista;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.Timer;
import submarineattack.modelo.Barco;
import submarineattack.modelo.CargaProfundidad;
import submarineattack.modelo.Constantes;
import submarineattack.modelo.EstadoJuego;
import submarineattack.modelo.Juego;
import submarineattack.modelo.JuegoListener;
import submarineattack.modelo.Submarino;

/**
 * Zona donde se dibuja el escenario (la "V" de MVC): mar, barcos, cargas,
 * submarino y los textos de pausa / victoria / derrota. Solo LEE el modelo;
 * nunca lo modifica.
 *
 * Además implementa el efecto de fading: cuando el submarino recibe daño, un
 * javax.swing.Timer hace variar la transparencia del submarino durante un
 * segundo aproximadamente.
 */
public class LienzoJuego extends JPanel implements JuegoListener {

    private static final Color DORADO = new Color(255, 215, 0);
    private static final Color ROJO = new Color(220, 20, 20);
    private static final int TAMANIO_EXPLOSION = 70;
    private static final int PASOS_FADING = 25;

    private final BufferedImage imagenCarga = Imagenes.cargar("carga_normal.png");
    private final BufferedImage imagenExplosion = Imagenes.cargar("carga_explosion.png");
    private final Timer timerFading = new Timer(40, e -> avanzarFading());

    private Juego juego;
    private float alphaSubmarino = 1f;
    private int pasoFading;

    public LienzoJuego() {
        setPreferredSize(new Dimension(Constantes.ANCHO_MUNDO, Constantes.ALTO_MUNDO));
        setFocusable(true);
    }

    public void setJuego(Juego juego) {
        this.juego = juego;
        timerFading.stop();
        alphaSubmarino = 1f;
    }

    // ------------------------------------------------------------------
    // Efecto de fading (disparado por el evento submarinoDanado)
    // ------------------------------------------------------------------

    @Override
    public void submarinoDanado() {
        pasoFading = 0;
        timerFading.restart();
    }

    private void avanzarFading() {
        pasoFading++;
        if (pasoFading >= PASOS_FADING) {
            alphaSubmarino = 1f;
            timerFading.stop();
        } else {
            alphaSubmarino = 0.25f + 0.75f * (float) Math.abs(Math.cos(pasoFading * 0.5));
        }
        repaint();
    }

    // ------------------------------------------------------------------
    // Dibujo
    // ------------------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        dibujarEscenario(g2);
        if (juego != null) {
            for (Barco barco : juego.getBarcos()) {
                dibujarBarco(g2, barco);
            }
            for (CargaProfundidad carga : juego.getCargas()) {
                dibujarCarga(g2, carga);
            }
            dibujarSubmarino(g2, juego.getSubmarino());
            dibujarTextoDeEstado(g2, juego.getEstado());
        }
        g2.dispose();
    }

    private void dibujarEscenario(Graphics2D g2) {
        g2.setColor(new Color(135, 206, 235));
        g2.fillRect(0, 0, getWidth(), Constantes.Y_SUPERFICIE);
        g2.setPaint(new GradientPaint(0, Constantes.Y_SUPERFICIE, new Color(0, 105, 148),
                0, getHeight(), new Color(0, 15, 50)));
        g2.fillRect(0, Constantes.Y_SUPERFICIE, getWidth(), getHeight() - Constantes.Y_SUPERFICIE);
    }

    private void dibujarBarco(Graphics2D g2, Barco barco) {
        int x = (int) (barco.getX() - barco.getAncho() / 2.0);
        int y = (int) (barco.getY() - barco.getAlto() / 2.0);
        int ancho = barco.getAncho();
        int alto = barco.getAlto();

        Polygon casco = new Polygon(
                new int[]{x, x + ancho, x + ancho - 12, x + 12},
                new int[]{y + 8, y + 8, y + alto, y + alto}, 4);
        g2.setColor(new Color(90, 90, 100));
        g2.fillPolygon(casco);
        g2.setColor(new Color(160, 160, 170));
        g2.fillRect(x + ancho / 2 - 14, y - 2, 28, 10);
        g2.fillRect(x + ancho / 2 + 6, y - 12, 4, 12);
    }

    private void dibujarCarga(Graphics2D g2, CargaProfundidad carga) {
        int cx = (int) carga.getX();
        int cy = (int) carga.getY();
        if (carga.getEstado() == CargaProfundidad.Estado.CAYENDO) {
            if (imagenCarga != null) {
                g2.drawImage(imagenCarga, cx - carga.getAncho() / 2, cy - carga.getAlto() / 2,
                        carga.getAncho(), carga.getAlto(), null);
            } else {
                g2.setColor(Color.DARK_GRAY);
                g2.fillOval(cx - carga.getAncho() / 2, cy - carga.getAlto() / 2, carga.getAncho(), carga.getAlto());
            }
        } else if (carga.getEstado() == CargaProfundidad.Estado.EXPLOTANDO) {
            if (imagenExplosion != null) {
                g2.drawImage(imagenExplosion, cx - TAMANIO_EXPLOSION / 2, cy - TAMANIO_EXPLOSION / 2,
                        TAMANIO_EXPLOSION, TAMANIO_EXPLOSION, null);
            } else {
                g2.setColor(Color.ORANGE);
                g2.fillOval(cx - TAMANIO_EXPLOSION / 2, cy - TAMANIO_EXPLOSION / 2,
                        TAMANIO_EXPLOSION, TAMANIO_EXPLOSION);
            }
        }
    }

    private void dibujarSubmarino(Graphics2D g2, Submarino sub) {
        int x = (int) (sub.getX() - sub.getAncho() / 2.0);
        int y = (int) (sub.getY() - sub.getAlto() / 2.0);
        int ancho = sub.getAncho();
        int alto = sub.getAlto();

        Composite original = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaSubmarino));
        g2.setColor(new Color(240, 200, 40));
        g2.fillOval(x, y + 6, ancho, alto - 6);                 // casco
        g2.fillRoundRect(x + ancho / 2 - 10, y, 20, 14, 6, 6);  // torre
        g2.fillRect(x + ancho / 2 + 4, y - 6, 3, 8);            // periscopio
        g2.setColor(new Color(60, 60, 60));
        g2.fillOval(x + ancho - 26, y + 12, 8, 8);              // ventanilla
        g2.setComposite(original);
    }

    private void dibujarTextoDeEstado(Graphics2D g2, EstadoJuego estado) {
        switch (estado) {
            case PAUSA:
                dibujarTextoCentrado(g2, "PAUSA", Color.WHITE, 72, Constantes.ALTO_MUNDO / 2);
                dibujarTextoCentrado(g2, "Presioná P para continuar", Color.WHITE, 24, Constantes.ALTO_MUNDO / 2 + 50);
                break;
            case GAME_OVER:
                dibujarTextoCentrado(g2, "GAME OVER", ROJO, 96, Constantes.ALTO_MUNDO / 2);
                dibujarTextoCentrado(g2, "Presioná ENTER para volver al menú", Color.WHITE, 24, Constantes.ALTO_MUNDO / 2 + 50);
                break;
            case VICTORIA:
                dibujarTextoCentrado(g2, "¡VICTORIA!", DORADO, 96, Constantes.ALTO_MUNDO / 2);
                dibujarTextoCentrado(g2, "Presioná ENTER para volver al menú", Color.WHITE, 24, Constantes.ALTO_MUNDO / 2 + 50);
                break;
            default:
                break;
        }
    }

    /** Texto procedimental: se dibuja por código (no es una imagen). */
    private void dibujarTextoCentrado(Graphics2D g2, String texto, Color color, int tamanio, int y) {
        g2.setFont(new Font("SansSerif", Font.BOLD, tamanio));
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(texto)) / 2;
        g2.setColor(new Color(0, 0, 0, 160));
        g2.drawString(texto, x + 3, y + 3);   // sombra para que se lea sobre el mar
        g2.setColor(color);
        g2.drawString(texto, x, y);
    }
}
