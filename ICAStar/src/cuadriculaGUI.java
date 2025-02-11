import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class cuadriculaGUI extends JPanel {
    private cuadricula cuadricula;
    private List<nodo> ruta;          // Almacena la ruta calculada (final)
    private List<nodo> rutaRecorrida; // Almacena los nodos "visitados" en la animación
    private nodo actual;
    private nodo inicio;
    private nodo objetivo;

    // Lista de waypoints
    private List<nodo> waypoints = new ArrayList<>();

    private final int TAMANO_CELDA = 50;

    public cuadriculaGUI(cuadricula cuadricula) {
        this.cuadricula = cuadricula;
        this.ruta = new ArrayList<>();
        this.rutaRecorrida = new ArrayList<>();
        this.actual = null;
        this.inicio = null;
        this.objetivo = null;

        // Aumentamos el tamaño para dejar espacio a números y flechas sin que se tapen
        setPreferredSize(new Dimension(
            cuadricula.getaNodos().length * TAMANO_CELDA + 60,
            cuadricula.getaNodos()[0].length * TAMANO_CELDA + 100
        ));

        // Listener para clics
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int fila = e.getX() / TAMANO_CELDA;
                int columna = e.getY() / TAMANO_CELDA;

                // Verificar que esté dentro de la cuadrícula
                if (fila < 0 || fila >= cuadricula.getaNodos().length
                    || columna < 0 || columna >= cuadricula.getaNodos()[0].length) {
                    return;
                }

                nodo nodoSeleccionado = cuadricula.getaNodos()[fila][columna];

                // SHIFT + clic izquierdo -> marcar penalizado
                if (SwingUtilities.isLeftMouseButton(e) && e.isShiftDown()) {
                    nodoSeleccionado.penalizado = !nodoSeleccionado.penalizado;
                }
                // clic izquierdo normal -> obstáculo
                else if (SwingUtilities.isLeftMouseButton(e)) {
                    nodoSeleccionado.accesible = !nodoSeleccionado.accesible;
                }
                // clic derecho -> inicio / objetivo
                else if (SwingUtilities.isRightMouseButton(e)) {
                    if (inicio == null) {
                        inicio = nodoSeleccionado;
                    } else if (objetivo == null) {
                        objetivo = nodoSeleccionado;
                    } else {
                        objetivo = nodoSeleccionado;
                    }
                }
                repaint();
            }
        });
    }

    /**
     * Añadir un waypoint por coordenadas (x, y).
     */
    public void agregarWaypoint(int x, int y) {
        if (x < 0 || x >= cuadricula.getaNodos().length
            || y < 0 || y >= cuadricula.getaNodos()[0].length) {
            JOptionPane.showMessageDialog(this,
                "Coordenadas fuera de la cuadrícula.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        nodo nodoSeleccionado = cuadricula.getaNodos()[x][y];
        if (!nodoSeleccionado.accesible) {
            JOptionPane.showMessageDialog(this,
                "No se puede crear un waypoint en un obstáculo.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        waypoints.add(nodoSeleccionado);
        JOptionPane.showMessageDialog(this,
            "Waypoint agregado en (" + x + ", " + y + ").",
            "Info", JOptionPane.INFORMATION_MESSAGE);
        repaint();
    }

    /**
     * Ejecutar A* sin waypoints (directo).
     */
    public void ejecutarAStar() {
        if (inicio != null && objetivo != null) {
            aStar aEstrella = new aStar(cuadricula, inicio, objetivo);
            ruta = aEstrella.encontrarRuta();
            rutaRecorrida.clear();
            animarRuta();
        } else {
            JOptionPane.showMessageDialog(this,
                "Seleccione un inicio y un objetivo.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Ejecutar A* con waypoints en orden: inicio -> w1 -> w2 -> ... -> objetivo
     */
    public void ejecutarAStarConWaypoints() {
        if (inicio == null) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar un punto de inicio.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (waypoints.isEmpty() && objetivo == null) {
            JOptionPane.showMessageDialog(this,
                "Debe haber al menos un waypoint o un objetivo final.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        nodo puntoActual = inicio;
        List<nodo> rutaCompleta = new ArrayList<>();

        // 1) Cada waypoint
        for (nodo waypoint : waypoints) {
            aStar aEstrella = new aStar(cuadricula, puntoActual, waypoint);
            List<nodo> subruta = aEstrella.encontrarRuta();
            if (subruta.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No hay ruta hacia el waypoint ("+waypoint.x+", "+waypoint.y+").",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Evitar duplicar el nodo anterior
            if (!rutaCompleta.isEmpty()) {
                subruta.remove(0);
            }
            rutaCompleta.addAll(subruta);
            puntoActual = waypoint;
        }

        // 2) Tramo final al objetivo (si existe)
        if (objetivo != null) {
            aStar aEstrella = new aStar(cuadricula, puntoActual, objetivo);
            List<nodo> subruta = aEstrella.encontrarRuta();
            if (subruta.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No hay ruta hasta el objetivo.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!rutaCompleta.isEmpty()) {
                subruta.remove(0);
            }
            rutaCompleta.addAll(subruta);
        }

        ruta = rutaCompleta;
        rutaRecorrida.clear();
        animarRuta();
    }

    /**
     * Animar la ruta final paso a paso.
     */
    private void animarRuta() {
        SwingWorker<Void, nodo> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (nodo paso : ruta) {
                    publish(paso);
                    Thread.sleep(300); // velocidad de animación
                }
                return null;
            }

            @Override
            protected void process(List<nodo> chunks) {
                nodo ultimo = chunks.get(chunks.size() - 1);
                setActual(ultimo);
            }
        };
        worker.execute();
    }

    private void setActual(nodo actual) {
        this.actual = actual;
        if (actual != null) {
            rutaRecorrida.add(actual);
        }
        repaint();
    }

    /**
     * Dibuja la cuadrícula, los obstáculos, penalizaciones, la ruta, etc.
     * Además dibujamos:
     *  - flechas en la ruta
     *  - numeración de filas/columnas
     *  - y ahora, el valor de 'f' de cada nodo (con símbolo ∞ para infinito)
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        nodo[][] nodos = cuadricula.getaNodos();
        int filas = nodos.length;
        int columnas = nodos[0].length;

        // Para numerar abajo/derecha
        int anchoTotal = filas * TAMANO_CELDA;
        int altoTotal = columnas * TAMANO_CELDA;

        // (1) Dibujar cada celda
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                nodo n = nodos[i][j];

                // Color según estado
                if (!n.accesible) {
                    g.setColor(Color.BLACK);    // Obstáculo
                } else if (n == inicio) {
                    g.setColor(Color.BLUE);     // Inicio
                } else if (n == objetivo) {
                    g.setColor(Color.RED);      // Objetivo
                } else if (waypoints.contains(n)) {
                    g.setColor(Color.ORANGE);   // Waypoint
                } else if (n.penalizado) {
                    g.setColor(Color.PINK);     // Penalizado
                } else if (rutaRecorrida.contains(n)) {
                    g.setColor(Color.GREEN);    // Parte de la ruta
                } else {
                    g.setColor(Color.WHITE);    // Libre
                }

                int px = i * TAMANO_CELDA;
                int py = j * TAMANO_CELDA;
                g.fillRect(px, py, TAMANO_CELDA, TAMANO_CELDA);

                // Borde
                g.setColor(Color.GRAY);
                g.drawRect(px, py, TAMANO_CELDA, TAMANO_CELDA);

                // (1b) DIBUJAR VALOR DE 'f' un poco más abajo, 
                // usando ∞ si es Double.POSITIVE_INFINITY
                String valorF;
                if (Double.isInfinite(n.f)) {
                    valorF = "∞"; // Símbolo infinito
                } else {
                    valorF = String.format("%.2f", n.f);
                }
                // Coordenadas para dibujarlo un poco más abajo 
                // para que no lo tapen las flechas
                int centerX = px + (TAMANO_CELDA / 2) - 10;
                int centerY = py + (TAMANO_CELDA / 2) + 15; 
                
                g.setColor(Color.BLACK);
                g.drawString(valorF, centerX, centerY);
            }
        }

        // (2) Dibujar flechas en la ruta
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));

        for (int k = 0; k < rutaRecorrida.size() - 1; k++) {
            nodo origen = rutaRecorrida.get(k);
            nodo destino = rutaRecorrida.get(k + 1);

            int x1 = origen.x * TAMANO_CELDA + TAMANO_CELDA / 2;
            int y1 = origen.y * TAMANO_CELDA + TAMANO_CELDA / 2;
            int x2 = destino.x * TAMANO_CELDA + TAMANO_CELDA / 2;
            int y2 = destino.y * TAMANO_CELDA + TAMANO_CELDA / 2;

            dibujarFlecha(g2d, x1, y1, x2, y2);
        }
        g2d.dispose();

        // (3) Numerar filas ABAJO (1..filas) y columnas A LA DERECHA (0..columnas-1)

        // Filas abajo
        for (int f = 0; f < filas; f++) {
            String textoFila = String.valueOf(f); // desde 1
            int x = f * TAMANO_CELDA + (TAMANO_CELDA / 2) - 3;
            int y = altoTotal + 35;
            g.setColor(Color.BLACK);
            g.drawString(textoFila, x, y);
        }

        // Columnas a la derecha
        for (int c = 0; c < columnas; c++) {
            String textoColumna = String.valueOf(c); // desde 0
            int x = anchoTotal + 5;
            int y = c * TAMANO_CELDA + (TAMANO_CELDA / 2) + 5;
            g.setColor(Color.BLACK);
            g.drawString(textoColumna, x, y);
        }
    }

    /**
     * Dibuja una flecha negra entre (x1,y1) y (x2,y2).
     */
    private void dibujarFlecha(Graphics2D g2, int x1, int y1, int x2, int y2) {
        g2.drawLine(x1, y1, x2, y2);

        double phi = Math.toRadians(25);  // ángulo de apertura
        int barb = 6;                     // longitud de la "cabeza" de la flecha

        double dy = y2 - y1;
        double dx = x2 - x1;
        double theta = Math.atan2(dy, dx);

        double rho = theta + phi;
        int xBarb = (int)(x2 - barb * Math.cos(rho));
        int yBarb = (int)(y2 - barb * Math.sin(rho));
        g2.drawLine(x2, y2, xBarb, yBarb);

        rho = theta - phi;
        xBarb = (int)(x2 - barb * Math.cos(rho));
        yBarb = (int)(y2 - barb * Math.sin(rho));
        g2.drawLine(x2, y2, xBarb, yBarb);
    }
}
