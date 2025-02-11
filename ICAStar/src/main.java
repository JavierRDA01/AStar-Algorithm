import javax.swing.*;

public class main {
    public static void main(String[] args) {
        // Pedir tamaño de la cuadrícula
    	int filas = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el número de filas de la cuadrícula:").trim());
    	int columnas = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el número de columnas de la cuadrícula:").trim());

        cuadricula cuadricula = new cuadricula(filas, columnas);
        cuadriculaGUI panel = new cuadriculaGUI(cuadricula);

        // Crear ventana
        JFrame frame = new JFrame("Visualización A* - Selecciona nodos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        // Botón para ejecutar A*
        JButton ejecutarBoton = new JButton("Ejecutar A*");
        ejecutarBoton.addActionListener(e -> panel.ejecutarAStar());

        // Panel de controles y leyenda
        JPanel controlPanel = new JPanel();
        controlPanel.add(ejecutarBoton);
        controlPanel.add(new JLabel("<html>"
                + "<b>Controles:</b><br>"
                + "🖱️ Click izquierdo: Marcar obstáculos (Negro)<br>"
                + "🖱️ Click derecho: Inicio (Azul) → Destino (Rojo)<br>"
                + "🟢 Verde: Ruta óptima<br>"
                + "</html>"));

        frame.add(panel);
        frame.add(controlPanel);
        frame.pack();
        frame.setVisible(true);
    }
}
