import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class cuadriculaGUI extends JPanel {
    private cuadricula cuadricula;
    private List<nodo> ruta;          // Almacena la ruta calculada (final)
    private List<nodo> rutaRecorrida; // Almacena los nodos que se han "visitado" durante la animación
    private nodo actual;
    private nodo inicio;
    private nodo objetivo;

    // Lista de waypoints, en el orden en que se agregan
    private List<nodo> waypoints = new ArrayList<>();

    private final int TAMANO_CELDA = 50;

    public cuadriculaGUI(cuadricula cuadricula) {
        this.cuadricula = cuadricula;
        this.ruta = new ArrayList<>();
        this.rutaRecorrida = new ArrayList<>();
        this.actual = null;
        this.inicio = null;
        this.objetivo = null;

        // Ajustar tamaño del panel
        setPreferredSize(new Dimension(
                cuadricula.getaNodos().length * TAMANO_CELDA,
                cuadricula.getaNodos()[0].length * TAMANO_CELDA
        ));

        // Listener para gestionar clics (obstáculos, inicio, fin)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int fila = e.getX() / TAMANO_CELDA;
                int columna = e.getY() / TAMANO_CELDA;

                if (fila < 0 || fila >= cuadricula.getaNodos().length 
                    || columna < 0 || columna >= cuadricula.getaNodos()[0].length) {
                    return; // Evitar clics fuera de la cuadrícula
                }

                nodo nodoSeleccionado = cuadricula.getaNodos()[fila][columna];

                if (SwingUtilities.isLeftMouseButton(e)) {
                    // Click izquierdo -> marcar / desmarcar obstáculo
                    nodoSeleccionado.accesible = !nodoSeleccionado.accesible;

                } else if (SwingUtilities.isRightMouseButton(e)) {
                    // Click derecho -> definir inicio o destino
                    if (inicio == null) {
                        inicio = nodoSeleccionado;
                    } else if (objetivo == null) {
                        objetivo = nodoSeleccionado;
                    } else {
                        objetivo = nodoSeleccionado;
                    }
                }
                // Se omite el botón central para waypoints (no se usa en este ejemplo)

                repaint();
            }
        });
    }

    /**
     * Añadir un waypoint por coordenadas (x, y). 
     * El nodo debe ser accesible para poder añadirlo.
     */
    public void agregarWaypoint(int x, int y) {
        // Validar rango
        if (x < 0 || x >= cuadricula.getaNodos().length 
            || y < 0 || y >= cuadricula.getaNodos()[0].length) {
            JOptionPane.showMessageDialog(this, 
                "Las coordenadas (" + x + ", " + y + ") están fuera de la cuadrícula.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        nodo nodoSeleccionado = cuadricula.getaNodos()[x][y];

        // Verificar que sea accesible (no obstáculo)
        if (!nodoSeleccionado.accesible) {
            JOptionPane.showMessageDialog(this, 
                "No se puede crear un waypoint en un nodo inaccesible.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Añadir a la lista de waypoints
        waypoints.add(nodoSeleccionado);
        JOptionPane.showMessageDialog(this, 
            "Waypoint agregado en (" + x + ", " + y + ").",
            "Info", JOptionPane.INFORMATION_MESSAGE);

        repaint();
    }

    /**
     * Ejecutar A* sin waypoints: directamente del 'inicio' al 'objetivo'.
     */
    public void ejecutarAStar() {
        if (inicio != null && objetivo != null) {
            aStar aEstrella = new aStar(cuadricula, inicio, objetivo);
            ruta = aEstrella.encontrarRuta();
            rutaRecorrida.clear();
            animarRuta();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Debe seleccionar un punto de inicio y un objetivo.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Ejecutar A* pasando por todos los waypoints en orden:
     *   inicio -> waypoint1 -> waypoint2 -> ... -> ultimoWaypoint -> objetivo
     */
    public void ejecutarAStarConWaypoints() {
        if (inicio == null) {
            JOptionPane.showMessageDialog(this, 
                "Debe seleccionar un punto de inicio.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Si no hay waypoints y tampoco hay objetivo, no se puede hacer nada
        if (waypoints.isEmpty() && objetivo == null) {
            JOptionPane.showMessageDialog(this, 
                "Debe haber al menos un waypoint o un objetivo final.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Empezamos desde 'inicio'
        nodo puntoActual = inicio;
        List<nodo> rutaCompleta = new ArrayList<>();

        // 1) Visitar cada waypoint en orden
        for (nodo waypoint : waypoints) {
            aStar aEstrella = new aStar(cuadricula, puntoActual, waypoint);
            List<nodo> subruta = aEstrella.encontrarRuta();
            if (subruta.isEmpty()) {
                // No se encontró camino a este waypoint -> abortar
                JOptionPane.showMessageDialog(this, 
                    "No se encontró ruta hacia el waypoint (" + waypoint.x + ", " + waypoint.y + ").",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Evitar duplicar el primer nodo (puntoActual) en la concatenación
            if (!rutaCompleta.isEmpty()) {
                subruta.remove(0);
            }
            rutaCompleta.addAll(subruta);
            puntoActual = waypoint;
        }

        // 2) Si hay objetivo final, conectar el último waypoint (o el inicio si no había waypoints) con el objetivo
        if (objetivo != null) {
            aStar aEstrella = new aStar(cuadricula, puntoActual, objetivo);
            List<nodo> subruta = aEstrella.encontrarRuta();
            if (subruta.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No se encontró ruta hacia el objetivo final.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Evitar duplicar
            if (!rutaCompleta.isEmpty()) {
                subruta.remove(0);
            }
            rutaCompleta.addAll(subruta);
        }

        // Guardamos la ruta total
        ruta = rutaCompleta;
        rutaRecorrida.clear();
        animarRuta();
    }

    /**
     * Animación de la ruta (se va pintando la rutaRecorrida paso a paso).
     */
    private void animarRuta() {
        SwingWorker<Void, nodo> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (nodo paso : ruta) {
                    publish(paso);
                    Thread.sleep(300); // Velocidad de animación
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

    /**
     * Marca el nodo actual en la animación y lo añade a la ruta recorrida.
     */
    private void setActual(nodo actual) {
        this.actual = actual;
        if (actual != null) {
            rutaRecorrida.add(actual);
        }
        repaint();
    }

    /**
     * Pintamos la cuadrícula, con diferentes colores para
     * obstáculos, inicio, objetivo, waypoints, etc.
     * Luego, dibujamos flechas entre cada par consecutivo de nodos en 'rutaRecorrida'.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // --- DIBUJO DE CELDAS ---
        nodo[][] nodos = cuadricula.getaNodos();
        for (int i = 0; i < nodos.length; i++) {
            for (int j = 0; j < nodos[i].length; j++) {
                nodo actualNodo = nodos[i][j];

                if (!actualNodo.accesible) {
                    g.setColor(Color.BLACK);  // Obstáculo
                } else if (actualNodo == inicio) {
                    g.setColor(Color.BLUE);   // Inicio
                } else if (actualNodo == objetivo) {
                    g.setColor(Color.RED);    // Objetivo
                } else if (waypoints.contains(actualNodo)) {
                    g.setColor(Color.ORANGE); // Waypoint
                } else if (rutaRecorrida.contains(actualNodo)) {
                    // Casillas ya recorridas
                    g.setColor(Color.GREEN);  
                } else {
                    g.setColor(Color.WHITE);  // Zona libre
                }

                g.fillRect(i * TAMANO_CELDA, j * TAMANO_CELDA, TAMANO_CELDA, TAMANO_CELDA);

                g.setColor(Color.GRAY);
                g.drawRect(i * TAMANO_CELDA, j * TAMANO_CELDA, TAMANO_CELDA, TAMANO_CELDA);
            }
        }

        // --- DIBUJO DE FLECHAS EN LA RUTA ---
        // Convertimos a Graphics2D para dibujar flechas
        Graphics2D g2d = (Graphics2D) g.create();
        // Cambiamos el color de la flecha a NEGRO
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2)); // grosor de la línea

        // Recorremos la lista de nodos recorridos en parejas consecutivas
        for (int i = 0; i < rutaRecorrida.size() - 1; i++) {
            nodo origen = rutaRecorrida.get(i);
            nodo destino = rutaRecorrida.get(i + 1);

            // Centro de la celda de origen
            int x1 = origen.x * TAMANO_CELDA + TAMANO_CELDA / 2;
            int y1 = origen.y * TAMANO_CELDA + TAMANO_CELDA / 2;
            
            // Centro de la celda de destino
            int x2 = destino.x * TAMANO_CELDA + TAMANO_CELDA / 2;
            int y2 = destino.y * TAMANO_CELDA + TAMANO_CELDA / 2;

            // Dibujamos la flecha
            dibujarFlecha(g2d, x1, y1, x2, y2);
        }

        g2d.dispose(); // Liberar recursos
    }

    /**
     * Dibuja una flecha desde (x1, y1) hasta (x2, y2).
     * Se basa en dibujar una línea y luego dos segmentos que forman la "cabeza" de la flecha.
     */
    private void dibujarFlecha(Graphics2D g2, int x1, int y1, int x2, int y2) {
        // Dibujar línea principal
        g2.drawLine(x1, y1, x2, y2);

        // Parametrizar el tamaño de la "cabeza" de flecha
        double phi = Math.toRadians(25); // ángulo de apertura
        int barb = 10;                   // longitud de la cabeza

        // Calcular ángulo de la línea
        double dy = y2 - y1;
        double dx = x2 - x1;
        double theta = Math.atan2(dy, dx);

        // Primera línea de la cabeza
        double rho = theta + phi;
        int xBarb = (int)(x2 - barb * Math.cos(rho));
        int yBarb = (int)(y2 - barb * Math.sin(rho));
        g2.drawLine(x2, y2, xBarb, yBarb);

        // Segunda línea de la cabeza
        rho = theta - phi;
        xBarb = (int)(x2 - barb * Math.cos(rho));
        yBarb = (int)(y2 - barb * Math.sin(rho));
        g2.drawLine(x2, y2, xBarb, yBarb);
    }
}
