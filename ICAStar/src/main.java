import javax.swing.*;

public class main {
    public static void main(String[] args) {
        // Pedir tamaño de la cuadrículaa
        int filas = Integer.parseInt(
            JOptionPane.showInputDialog("Ingrese el número de filas de la cuadrícula:").trim()
        );
        int columnas = Integer.parseInt(
            JOptionPane.showInputDialog("Ingrese el número de columnas de la cuadrícula:").trim()
        );

        cuadricula cuadricula = new cuadricula(filas, columnas);
        cuadriculaGUI panel = new cuadriculaGUI(cuadricula);

        // Crear ventana
        JFrame frame = new JFrame("Visualización A* - Selecciona nodos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        // Botón para ejecutar A* directo (sin waypoints)
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
                if (sx == null) return; // Usuario canceló
                String sy = JOptionPane.showInputDialog(frame, "Coordenada Y del waypoint:");
                if (sy == null) return; // Usuario canceló

                int x = Integer.parseInt(sx.trim());
                int y = Integer.parseInt(sy.trim());

                // Llamamos al método de cuadriculaGUI para añadir el waypoint
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
                + "<b>Controles con ratón:</b><br>"
                + "• Click izquierdo: Marcar obstáculos (Negro)<br>"
                + "• Click derecho: Seleccionar Inicio (Azul), luego Destino (Rojo)<br><br>"
                + "<b>Botones:</b><br>"
                + "• \"Añadir Waypoint\": Ingresa coordenadas X, Y<br>"
                + "• \"Ejecutar A* (Directo)\": Ir del inicio al fin (sin waypoints)<br>"
                + "• \"Ejecutar A* (Waypoints)\": Ir secuencialmente por los waypoints hasta el destino<br>"
                + "<br>"
                + "Blanco = Zona accesible<br>"
                + "Verde = Ruta recorrida en la animación<br>"
                + "Naranja = Waypoints<br>"
                + "</html>"));

        frame.add(panel);
        frame.add(controlPanel);
        frame.pack();
        frame.setVisible(true);
    }
}
