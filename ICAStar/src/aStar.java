import java.util.*;

public class aStar {
    private cuadricula cuadricula;
    private nodo inicio;
    private nodo objetivo;

    // Penalización global = 0.07 * sqrt(filas^2 + columnas^2)
    private double penaltyValue;

    public aStar(cuadricula cuadricula, nodo inicio, nodo objetivo) {
        this.cuadricula = cuadricula;
        this.inicio = inicio;
        this.objetivo = objetivo;

        // Calcular penalización en base al tamaño de la cuadrícula
        int filas = cuadricula.getaNodos().length;
        int columnas = cuadricula.getaNodos()[0].length;
        this.penaltyValue = 0.07 * Math.sqrt(filas * filas + columnas * columnas);
    }

    /**
     * Reinicia g, h, f y padre de todos los nodos, para no arrastrar búsquedas previas.
     */
    private void reiniciarNodos() {
        nodo[][] nodos = cuadricula.getaNodos();
        for (int i = 0; i < nodos.length; i++) {
            for (int j = 0; j < nodos[i].length; j++) {
                nodo n = nodos[i][j];
                n.g = Double.POSITIVE_INFINITY;
                n.h = 0;
                n.f = Double.POSITIVE_INFINITY;
                n.padre = null;
            }
        }
    }

    /**
     * Heurística: distancia euclidiana hasta el objetivo.
     */
    private double calcularHeuristica(nodo n) {
        return Math.sqrt(Math.pow(n.x - objetivo.x, 2) + Math.pow(n.y - objetivo.y, 2));
    }

    /**
     * Encuentra la ruta A* desde 'inicio' hasta 'objetivo'.
     */
    public List<nodo> encontrarRuta() {
        // Reiniciar antes de cada búsqueda
        reiniciarNodos();

        PriorityQueue<nodo> abiertos = new PriorityQueue<>(Comparator.comparingDouble(n -> n.f));
        Set<nodo> cerrados = new HashSet<>();

        // Nodo inicio
        inicio.g = 0;
        inicio.h = calcularHeuristica(inicio);
        double penaltyInicio = inicio.penalizado ? penaltyValue : 0.0;
        inicio.f = inicio.g + inicio.h + penaltyInicio;
        abiertos.add(inicio);

        // Bucle principal
        while (!abiertos.isEmpty()) {
            nodo actual = abiertos.poll();

            // Si llegamos al objetivo
            if (actual == objetivo) {
                return reconstruirRuta(actual);
            }

            cerrados.add(actual);

            // Revisar vecinos
            for (nodo adyacente : cuadricula.obtenerAdyacentes(actual)) {
                if (cerrados.contains(adyacente)) continue;

                // Penalización si el vecino es penalizado
                double penalty = adyacente.penalizado ? penaltyValue : 0.0;
                // Costo de paso: 1 + penalty
                double gTentativo = actual.g + 1 + penalty;

                if (gTentativo < adyacente.g) {
                    adyacente.padre = actual;
                    adyacente.g = gTentativo;
                    adyacente.h = calcularHeuristica(adyacente);
                    // f = g + h + penalty (si penalizado)
                    adyacente.f = adyacente.g + adyacente.h
                                  + (adyacente.penalizado ? penaltyValue : 0.0);

                    if (!abiertos.contains(adyacente)) {
                        abiertos.add(adyacente);
                    }
                }
            }
        }

        // No se encontró ruta
        return Collections.emptyList();
    }

    /**
     * Reconstruir la ruta desde el objetivo hasta el inicio.
     */
    private List<nodo> reconstruirRuta(nodo nodoFinal) {
        List<nodo> ruta = new ArrayList<>();
        nodo actual = nodoFinal;
        while (actual != null) {
            ruta.add(actual);
            actual = actual.padre;
        }
        Collections.reverse(ruta);
        return ruta;
    }
}
