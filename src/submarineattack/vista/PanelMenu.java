package submarineattack.vista;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import submarineattack.modelo.Nivel;
import submarineattack.persistencia.RegistroJugador;

/**
 * Menú principal: imagen de fondo, datos del jugador (nombre y récord) e
 * indicador de los 3 niveles. Los niveles aún no alcanzados aparecen
 * bloqueados (botón deshabilitado) y se van habilitando al superarlos.
 */
public class PanelMenu extends JPanel {

    private final BufferedImage fondo = Imagenes.cargar("fondo_menu.png");
    private final JLabel etiquetaJugador = new JLabel();
    private final JLabel etiquetaRecord = new JLabel();
    private final JButton[] botonesNivel = new JButton[Nivel.values().length];
    private final JButton botonCambiarJugador = new JButton("Cambiar jugador");

    public PanelMenu() {
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(1000, 750));

        JLabel titulo = new JLabel("SUBMARINE ATTACK");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 56));
        titulo.setForeground(Color.WHITE);

        configurarEtiqueta(etiquetaJugador);
        configurarEtiqueta(etiquetaRecord);

        JPanel filaNiveles = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        filaNiveles.setOpaque(false);
        for (Nivel nivel : Nivel.values()) {
            JButton boton = new JButton();
            boton.setActionCommand(String.valueOf(nivel.getNumero()));
            boton.setPreferredSize(new Dimension(220, 60));
            botonesNivel[nivel.ordinal()] = boton;
            filaNiveles.add(boton);
        }

        botonCambiarJugador.setFont(new Font("SansSerif", Font.PLAIN, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(14, 14, 14, 14);
        gbc.gridy = 0;
        add(titulo, gbc);
        gbc.gridy = 1;
        add(etiquetaJugador, gbc);
        gbc.gridy = 2;
        add(etiquetaRecord, gbc);
        gbc.gridy = 3;
        add(filaNiveles, gbc);
        gbc.gridy = 4;
        gbc.insets = new Insets(28, 14, 14, 14);
        add(botonCambiarJugador, gbc);
    }

    private void configurarEtiqueta(JLabel etiqueta) {
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 24));
        etiqueta.setForeground(Color.WHITE);
    }

    /** Refresca los datos del jugador y qué niveles se pueden jugar. */
    public void actualizar(RegistroJugador jugador) {
        etiquetaJugador.setText("Jugador: " + jugador.getNombre());
        etiquetaRecord.setText("Récord: " + jugador.getRecord());
        for (Nivel nivel : Nivel.values()) {
            JButton boton = botonesNivel[nivel.ordinal()];
            boolean desbloqueado = nivel.getNumero() <= jugador.getNivelDesbloqueado();
            boton.setEnabled(desbloqueado);
            if (desbloqueado) {
                boton.setText("Nivel " + nivel.getNumero());
                boton.setFont(new Font("SansSerif", Font.BOLD, 20));
            } else {
                boton.setText("Nivel " + nivel.getNumero() + " (bloqueado)");
                boton.setFont(new Font("SansSerif", Font.BOLD, 14));
            }
        }
    }

    public void addNivelListener(ActionListener listener) {
        for (JButton boton : botonesNivel) {
            boton.addActionListener(listener);
        }
    }

    /** Vuelve a la pantalla de login para ingresar con otro nombre de jugador. */
    public void addCambiarJugadorListener(ActionListener listener) {
        botonCambiarJugador.addActionListener(listener);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (fondo != null) {
            g2.drawImage(fondo, 0, 0, getWidth(), getHeight(), null);
        } else {
            g2.setPaint(new GradientPaint(0, 0, new Color(0, 90, 140), 0, getHeight(), new Color(0, 15, 45)));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
