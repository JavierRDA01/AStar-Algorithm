import javax.swing.*;

public class main {
    public static void main(String[] args) {
        // Pedir tamaño de la cuadrícula
        int filas = Integer.parseInt(
            JOptionPane.showInputDialog("Ingrese el número de filas de la cuadrícula:").trim()
        );
        int columnas = Integer.parseInt(
            JOptionPane.showInputDialog("Ingrese el número de columnas de la cuadrícula:").trim()
        );

        cuadricula cuadricula = new cuadricula(filas, columnas);
        cuadriculaGUI panel = new cuadriculaGUI(cuadricula);

        // Crear ventana
        JFrame frame = new JFrame("Visualización A* - Celdas penalizadas");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        // Botón para ejecutar A* directo
        JButton ejecutarBoton = new JButton("Ejecutar A* (Directo)");
        ejecutarBoton.addActionListener(e -> panel.ejecutarAStar());

        // Botón para ejecutar A* con waypoints
        JButton ejecutarWaypointsBoton = new JButton("Ejecutar A* (Waypoints)");
        ejecutarWaypointsBoton.addActionListener(e -> panel.ejecutarAStarConWaypoints());

        // Botón para añadir waypoint
        JButton agregarWaypointBtn = new JButton("Añadir Waypoint");
        agregarWaypointBtn.addActionListener(e -> {
            try {
                String sx = JOptionPane.showInputDialog(frame, "Coordenada X del waypoint:");
                if (sx == null) return;
                String sy = JOptionPane.showInputDialog(frame, "Coordenada Y del waypoint:");
                if (sy == null) return;

                int x = Integer.parseInt(sx.trim());
                int y = Integer.parseInt(sy.trim());
                panel.agregarWaypoint(x, y);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame,
                    "Coordenadas inválidas. Debe ingresar números enteros.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Panel de controles y leyenda
        JPanel controlPanel = new JPanel();
        controlPanel.add(ejecutarBoton);
        controlPanel.add(ejecutarWaypointsBoton);
        controlPanel.add(agregarWaypointBtn);

        controlPanel.add(new JLabel("<html>"
                + "<b>Controles de la cuadrícula:</b><br>"
                + "• Click izquierdo: Obstáculo ON/OFF (Negro)<br>"
                + "• Shift + click izquierdo: Penalizado ON/OFF (Rosa)<br>"
                + "• Click derecho: Inicio (Azul) y luego Objetivo (Rojo)<br>"
                + "<br>"
                + "Waypoints (Naranja) se añaden con el botón \"Añadir Waypoint\"<br>"
                + "Celdas penalizadas tienen coste adicional = 0.07 * sqrt(filas²+columnas²)<br>"
                + "</html>"));

        frame.add(panel);
        frame.add(controlPanel);
        frame.pack();
        frame.setVisible(true);
    }
}
