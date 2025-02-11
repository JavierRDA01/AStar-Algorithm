import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class cuadriculaGUI extends JPanel {
    private cuadricula cuadricula;
    private List<nodo> ruta;
    private List<nodo> rutaRecorrida;
    private nodo actual;
    private nodo inicio;
    private nodo objetivo;
    private final int TAMANO_CELDA = 50;

    public cuadriculaGUI(cuadricula cuadricula) {
        this.cuadricula = cuadricula;
        this.ruta = new ArrayList<>();
        this.rutaRecorrida = new ArrayList<>();
        this.actual = null;
        this.inicio = null;
        this.objetivo = null;

        setPreferredSize(new Dimension(cuadricula.getaNodos().length * TAMANO_CELDA, cuadricula.getaNodos()[0].length * TAMANO_CELDA));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int fila = e.getX() / TAMANO_CELDA;
                int columna = e.getY() / TAMANO_CELDA;

                if (fila < 0 || fila >= cuadricula.getaNodos().length || columna < 0 || columna >= cuadricula.getaNodos()[0].length) {
                    return; // Evitar clics fuera de la cuadrícula
                }

                nodo nodoSeleccionado = cuadricula.getaNodos()[fila][columna];

                if (SwingUtilities.isLeftMouseButton(e)) { // Click izquierdo -> marcar inaccesible
                    nodoSeleccionado.accesible = !nodoSeleccionado.accesible;
                } else if (SwingUtilities.isRightMouseButton(e)) { // Click derecho -> definir inicio o destino
                    if (inicio == null) {
                        inicio = nodoSeleccionado;
                    } else if (objetivo == null) {
                        objetivo = nodoSeleccionado;
                    } else {
                        objetivo = nodoSeleccionado; // Permite cambiar el destino si ya hay uno seleccionado
                    }
                }
                repaint();
            }
        });
    }

    public void ejecutarAStar() {
        if (inicio != null && objetivo != null) {
            aStar aEstrella = new aStar(cuadricula, inicio, objetivo);
            ruta = aEstrella.encontrarRuta();
            animarRuta();
        } else {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un punto de inicio y un objetivo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void animarRuta() {
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
                setActual(chunks.get(chunks.size() - 1));
            }
        };
        worker.execute();
    }

    public void setActual(nodo actual) {
        this.actual = actual;
        if (actual != null) {
            rutaRecorrida.add(actual);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        nodo[][] nodos = cuadricula.getaNodos();

        for (int i = 0; i < nodos.length; i++) {
            for (int j = 0; j < nodos[i].length; j++) {
                if (!nodos[i][j].accesible) {
                    g.setColor(Color.BLACK); // Casilla inaccesible
                } else if (nodos[i][j] == inicio) {
                    g.setColor(Color.BLUE); // Punto de inicio
                } else if (nodos[i][j] == objetivo) {
                    g.setColor(Color.RED); // Punto de destino
                } else if (rutaRecorrida.contains(nodos[i][j])) {
                    g.setColor(Color.GREEN); // Ruta recorrida
                } else {
                    g.setColor(Color.WHITE); // Casilla accesible
                }
                g.fillRect(i * TAMANO_CELDA, j * TAMANO_CELDA, TAMANO_CELDA, TAMANO_CELDA);
                g.setColor(Color.GRAY);
                g.drawRect(i * TAMANO_CELDA, j * TAMANO_CELDA, TAMANO_CELDA, TAMANO_CELDA);
            }
        }
    }
}
