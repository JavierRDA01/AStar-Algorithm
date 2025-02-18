import javax.swing.*;
import java.awt.*;

public class main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Pedir tamaño de la cuadrícula
            int filas = Integer.parseInt(
                JOptionPane.showInputDialog("Ingrese el número de filas de la cuadrícula:").trim()
            );
            int columnas = Integer.parseInt(
                JOptionPane.showInputDialog("Ingrese el número de columnas de la cuadrícula:").trim()
            );

            // Se crea la cuadricula y su panel gráfico
            // (Se asume que las clases 'cuadricula' y 'cuadriculaGUI' están implementadas y cuentan con el constructor adecuado)
            cuadricula cuad = new cuadricula(filas, columnas);
            cuadriculaGUI gridPanel = new cuadriculaGUI(cuad);

            // Crear ventana principal con BorderLayout
            JFrame frame = new JFrame("Visualización A* - Celdas penalizadas");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            // --- Panel Oeste: Leyenda de colores ---
            JPanel westPanel = new JPanel();
            westPanel.setPreferredSize(new Dimension(150, 0));
            westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.Y_AXIS));
            westPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            westPanel.add(createLegendEntry("Obstáculo", Color.BLACK));
            westPanel.add(createLegendEntry("Inicio", Color.BLUE));
            westPanel.add(createLegendEntry("Objetivo", Color.RED));
            westPanel.add(createLegendEntry("Waypoint", Color.ORANGE));
            westPanel.add(createLegendEntry("Penalizado", Color.PINK));
            westPanel.add(createLegendEntry("Ruta", Color.GREEN));

            // --- Panel Este: Leyenda de clicks (instrucciones) ---
            JPanel eastPanel = new JPanel();
            eastPanel.setPreferredSize(new Dimension(300, 0)); // Ajusta el ancho según necesites
            eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));
            eastPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel clickLegend = new JLabel("<html>"
                    + "<div align='left' style='font-size:16px;'>"
                    + "<b>Controles de la cuadrícula:</b><br>"
                    +"<br></br>"
                    + "• Click izquierdo: Obstáculo ON/OFF (Negro)<br>"
                    +"<br></br>"
                    + "• Shift + click izquierdo: Penalizado ON/OFF (Rosa)<br>"
                    +"<br></br>"
                    + "• Click derecho: Establece Inicio (Azul) y luego Objetivo (Rojo)<br>"
                    +"<br></br>"
                    + "</div></html>");
            // Aseguramos que el JLabel se alinee a la izquierda dentro del BoxLayout
            clickLegend.setAlignmentX(Component.LEFT_ALIGNMENT);

            eastPanel.add(clickLegend);

            // --- Panel Sur: Botones de acción ---
            JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            JButton btnEjecutarAStar = new JButton("Ejecutar A*");
            JButton btnEjecutarAStarWaypoints = new JButton("Ejecutar A* con Waypoints");
            JButton btnAgregarWaypoint = new JButton("Agregar Waypoint");
            southPanel.add(btnEjecutarAStar);
            southPanel.add(btnEjecutarAStarWaypoints);
            southPanel.add(btnAgregarWaypoint);

            // Agregar ActionListeners para los botones
            btnEjecutarAStar.addActionListener(e -> gridPanel.ejecutarAStar());
            btnEjecutarAStarWaypoints.addActionListener(e -> gridPanel.ejecutarAStarConWaypoints());
            btnAgregarWaypoint.addActionListener(e -> {
                String inputX = JOptionPane.showInputDialog(frame, "Ingrese coordenada X:");
                String inputY = JOptionPane.showInputDialog(frame, "Ingrese coordenada Y:");
                try {
                    int x = Integer.parseInt(inputX.trim());
                    int y = Integer.parseInt(inputY.trim());
                    gridPanel.agregarWaypoint(x, y);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Coordenadas inválidas", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            // --- Agregar los paneles al JFrame ---
            frame.add(westPanel, BorderLayout.WEST);
            JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            centerWrapper.add(gridPanel);
            frame.add(centerWrapper, BorderLayout.CENTER);
            frame.add(eastPanel, BorderLayout.EAST);
            frame.add(southPanel, BorderLayout.SOUTH);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    /**
     * Crea un pequeño panel para la leyenda con un recuadro de color y una etiqueta descriptiva.
     */
    private static JPanel createLegendEntry(String text, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        JLabel colorLabel = new JLabel("   ");
        colorLabel.setOpaque(true);
        colorLabel.setBackground(color);
        colorLabel.setPreferredSize(new Dimension(20, 20));
        JLabel textLabel = new JLabel(text);
        panel.add(colorLabel);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));
        panel.add(textLabel);
        return panel;
    }
}
