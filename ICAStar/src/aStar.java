import java.util.*;

public class aStar {
    private cuadricula cuadricula;
    private nodo inicio;
    private nodo objetivo;

    public aStar(cuadricula cuadricula, nodo inicio, nodo objetivo) {
        this.cuadricula = cuadricula;
        this.inicio = inicio;
        this.objetivo = objetivo;
    }

    /**
     * Método para reiniciar los valores (g, h, f y padre) de todos los nodos de la cuadricula.
     * Así se evita que queden restos de una búsqueda A* anterior.
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
    private double calcularHeuristica(nodo nodo) {
        return Math.sqrt(Math.pow(nodo.x - objetivo.x, 2) + Math.pow(nodo.y - objetivo.y, 2));
    }

    /**
     * Ejecuta A* para hallar la ruta entre 'inicio' y 'objetivo'.
     * Devuelve la lista de nodos en la ruta o lista vacía si no hay camino.
     */
    public List<nodo> encontrarRuta() {
        // Antes de cada nueva búsqueda, reiniciamos todos los nodos
        reiniciarNodos();

        // Estructuras para abiertos/cerrados
        PriorityQueue<nodo> abiertos = new PriorityQueue<>(Comparator.comparingDouble(n -> n.f));
        Set<nodo> cerrados = new HashSet<>();

        // Configurar valores iniciales del nodo 'inicio'
        inicio.g = 0;
        inicio.h = calcularHeuristica(inicio);
        inicio.f = inicio.g + inicio.h;
        abiertos.add(inicio);

        // Bucle principal de A*
        while (!abiertos.isEmpty()) {
            nodo actual = abiertos.poll();

            // Si llegamos al objetivo, reconstruimos y retornamos la ruta
            if (actual == objetivo) {
                return reconstruirRuta(actual);
            }

            cerrados.add(actual);

            // Recorrer vecinos adyacentes
            for (nodo adyacente : cuadricula.obtenerAdyacentes(actual)) {
                if (cerrados.contains(adyacente)) {
                    continue;
                }
                double gTentativo = actual.g + 1;  // cost = 1 por cada paso

                if (gTentativo < adyacente.g) {
                    adyacente.padre = actual;
                    adyacente.g = gTentativo;
                    adyacente.h = calcularHeuristica(adyacente);
                    adyacente.f = adyacente.g + adyacente.h;

                    if (!abiertos.contains(adyacente)) {
                        abiertos.add(adyacente);
                    }
                }
            }
        }

        // Si se vacía 'abiertos' sin encontrar objetivo, no hay ruta
        return Collections.emptyList();
    }

    /**
     * Reconstruye la ruta desde un nodo final (objetivo) siguiendo 'padre' hacia atrás.
     */
    private List<nodo> reconstruirRuta(nodo nodoFinal) {
        List<nodo> ruta = new ArrayList<>();
        nodo current = nodoFinal;
        while (current != null) {
            ruta.add(current);
            current = current.padre;
        }
        Collections.reverse(ruta);
        return ruta;
    }
}
