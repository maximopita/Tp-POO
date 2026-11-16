package submarineattack.vista;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Pantalla de inicio de sesión: pide el nombre del jugador. */
public class PanelLogin extends JPanel {

    private final JTextField campoNombre = new JTextField(18);
    private final JButton botonIngresar = new JButton("Ingresar");

    public PanelLogin() {
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(1000, 750));

        JLabel titulo = new JLabel("SUBMARINE ATTACK");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 56));
        titulo.setForeground(Color.WHITE);

        JLabel indicacion = new JLabel("Nombre del jugador:");
        indicacion.setFont(new Font("SansSerif", Font.PLAIN, 22));
        indicacion.setForeground(Color.WHITE);

        campoNombre.setFont(new Font("SansSerif", Font.PLAIN, 22));
        campoNombre.setHorizontalAlignment(JTextField.CENTER);
        botonIngresar.setFont(new Font("SansSerif", Font.BOLD, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.gridy = 0;
        add(titulo, gbc);
        gbc.gridy = 1;
        add(indicacion, gbc);
        gbc.gridy = 2;
        add(campoNombre, gbc);
        gbc.gridy = 3;
        add(botonIngresar, gbc);
    }

    /** El botón y la tecla Enter en el campo disparan la misma acción. */
    public void addIngresarListener(ActionListener listener) {
        botonIngresar.addActionListener(listener);
        campoNombre.addActionListener(listener);
    }

    public String getNombre() {
        return campoNombre.getText();
    }

    /** Limpia el campo para que el próximo jugador no vea el nombre anterior. */
    public void limpiar() {
        campoNombre.setText("");
        campoNombre.requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(new GradientPaint(0, 0, new Color(0, 90, 140), 0, getHeight(), new Color(0, 15, 45)));
        g2.fillRect(0, 0, getWidth(), getHeight());
    }
}
