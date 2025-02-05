import javax.swing.*;
import java.awt.*;
import java.util.List;

public class cuadriculaGUI extends JPanel {
    private cuadricula cuadricula;
    private List<nodo> ruta;
    private nodo actual;
    private final int TAMANO_CELDA = 50;

    public cuadriculaGUI(cuadricula cuadricula, List<nodo> ruta) {
        this.cuadricula = cuadricula;
        this.ruta = ruta;
        this.actual = null;
        setPreferredSize(new Dimension(cuadricula.getaNodos().length * TAMANO_CELDA, cuadricula.getaNodos()[0].length * TAMANO_CELDA));
    }

    public void setActual(nodo actual) {
        this.actual = actual;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        nodo[][] nodos = cuadricula.getaNodos();

        for (int i = 0; i < nodos.length; i++) {
            for (int j = 0; j < nodos[i].length; j++) {
                if (!nodos[i][j].accesible) {
                    g.setColor(Color.BLACK);
                } else if (ruta.contains(nodos[i][j])) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(Color.WHITE);
                }
                g.fillRect(i * TAMANO_CELDA, j * TAMANO_CELDA, TAMANO_CELDA, TAMANO_CELDA);
                g.setColor(Color.GRAY);
                g.drawRect(i * TAMANO_CELDA, j * TAMANO_CELDA, TAMANO_CELDA, TAMANO_CELDA);
            }
        }

        if (actual != null) {
            g.setColor(Color.RED);
            g.fillOval(actual.x * TAMANO_CELDA + TAMANO_CELDA / 4, actual.y * TAMANO_CELDA + TAMANO_CELDA / 4, TAMANO_CELDA / 2, TAMANO_CELDA / 2);
        }
    }

    public static void main(String[] args) {
        cuadricula cuadricula = new cuadricula(10, 10);
        cuadricula.Inaccesible(2, 2);
        cuadricula.Inaccesible(3, 3);

        nodo inicio = cuadricula.getaNodos()[0][0];
        nodo objetivo = cuadricula.getaNodos()[9][9];

        aStar aEstrella = new aStar(cuadricula, inicio, objetivo);
        List<nodo> ruta = aEstrella.encontrarRuta();

        JFrame frame = new JFrame("Visualización A*");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cuadriculaGUI panel = new cuadriculaGUI(cuadricula, ruta);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);

        SwingWorker<Void, nodo> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (nodo paso : ruta) {
                    publish(paso);
                    Thread.sleep(500);
                }
                return null;
            }

            
            @Override
            protected void process(List<nodo> chunks) {
                panel.setActual(chunks.get(chunks.size() - 1));
            }
        };
        worker.execute();
    }
}
