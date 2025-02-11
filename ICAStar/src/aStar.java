import java.util.*;

public class aStar {
    private cuadricula cuadricula;
    private nodo inicio, objetivo;

    public aStar(cuadricula cuadricula, nodo inicio, nodo objetivo) {
        this.cuadricula = cuadricula;
        this.inicio = inicio;
        this.objetivo = objetivo;
    }

    private double calcularHeuristica(nodo nodo) {
        // Distancia euclidiana como heurística
        return Math.sqrt(Math.pow(nodo.x - objetivo.x, 2) + Math.pow(nodo.y - objetivo.y, 2));
    }

    public List<nodo> encontrarRuta() {
        PriorityQueue<nodo> abiertos = new PriorityQueue<>(Comparator.comparingDouble(n -> n.f));
        Set<nodo> cerrados = new HashSet<>();
        inicio.g = 0;
        inicio.h = calcularHeuristica(inicio);
        inicio.f = inicio.g + inicio.h;
        abiertos.add(inicio);

        while (!abiertos.isEmpty()) {
            nodo actual = abiertos.poll();

            
            if (actual == objetivo) {
                return reconstruirRuta(actual);
            }

            cerrados.add(actual);

            for (nodo adyacente : cuadricula.obtenerAdyacentes(actual)) {
                if (cerrados.contains(adyacente)) continue;

                double gTentativo = actual.g + 1; // Costo de movimiento entre nodos adyacentes es 1

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

        return Collections.emptyList(); // No se encontró ruta
    }

    private List<nodo> reconstruirRuta(nodo nodo) {
        List<nodo> ruta = new ArrayList<>();
        while (nodo != null) {
            ruta.add(nodo);
            nodo = nodo.padre;
        }
        Collections.reverse(ruta);
        return ruta;
    }
}