import java.util.*;

public class AStar {
    
    // Lista ABIERTA: priorizamos menor f
    private PriorityQueue<Cell> openList;
    // Lista CERRADA: podemos guardarla como un Set para búsquedas rápidas
    private Set<Cell> closedList;
    
    public AStar() {
        // Comparador de celdas basado en f
        Comparator<Cell> comparator = Comparator.comparingDouble(Cell::getF);
        this.openList = new PriorityQueue<>(comparator);
        this.closedList = new HashSet<>();
    }
    //Hola
    /**
     * Ejecuta el algoritmo A* sobre la 'grid', partiendo de 'start' hasta 'goal'.
     * @return Una lista de celdas (camino) o null si no hay solución.
     */
    public List<Cell> search(Grid grid, Cell start, Cell goal) {
        // Inicialización
        start.setG(0);
        start.setH(heuristic(start, goal));
        start.setF(start.getG() + start.getH());
        
        // Insertar en ABIERTA
        openList.clear();
        openList.add(start);
        
        // Insertar obstáculos directamente en CERRADA (opcional, según se interprete)
        closedList.clear();
        markObstaclesAsClosed(grid);
        
        // Bucle principal
        while(!openList.isEmpty()) {
            // 3) Extraer celda con menor f
            Cell current = openList.poll();
            
            // Si es la meta, reconstruimos el camino
            if(current.equals(goal)) {
                return reconstructPath(current);
            }
            
            // Pasar la celda actual a CERRADA
            closedList.add(current);
            
            // Obtener sucesores
            List<Cell> neighbors = getNeighbors(current, grid);
            for(Cell neighbor : neighbors) {
                // Si el vecino está en CERRADA, lo saltamos
                if(closedList.contains(neighbor)) {
                    continue;
                }
                
                // Calcular coste tentativo
                double tentativeG = current.getG() + distance(current, neighbor);
                
                // Si el vecino no está en openList, es la 1ª vez que lo vemos
                if(!openList.contains(neighbor)) {
                    neighbor.setG(tentativeG);
                    neighbor.setH(heuristic(neighbor, goal));
                    neighbor.setF(neighbor.getG() + neighbor.getH());
                    neighbor.setParent(current);
                    openList.add(neighbor);
                }
                // Si ya está en openList, comprobamos si hemos encontrado un mejor camino
                else {
                    if(tentativeG < neighbor.getG()) {
                        // Actualizamos la información
                        openList.remove(neighbor); // hay que quitarlo para re-insertarlo
                        neighbor.setG(tentativeG);
                        neighbor.setF(neighbor.getG() + neighbor.getH());
                        neighbor.setParent(current);
                        openList.add(neighbor);
                    }
                }
            }
        }
        
        // Si vaciamos la openList sin encontrar meta, no hay camino
        return null;
    }
    
    /**
     * Dado un nodo final, se recorre la cadena de 'padres' para reconstruir el camino.
     */
    private List<Cell> reconstructPath(Cell goal) {
        List<Cell> path = new ArrayList<>();
        Cell current = goal;
        while(current != null) {
            path.add(current);
            current = current.getParent();
        }
        Collections.reverse(path);
        return path;
    }
    
    /**
     * Añadir los nodos que son obstáculos directamente a la lista CERRADA 
     * (según el enunciado, si lo deseamos).
     */
    private void markObstaclesAsClosed(Grid grid) {
        // Este método es opcional, ya que se podrían simplemente ignorar obstáculos
        // al expandir vecinos. Pero si se quiere meterlos en CERRADA:
        // for(int r = 0; r < grid.getRows(); r++) {
        //     for(int c = 0; c < grid.getCols(); c++) {
        //         if(grid.isObstacle(r, c)) {
        //             closedList.add(new Cell(r, c));
        //         }
        //     }
        // }
    }
    
    /**
     * Heurística: distancia euclídea entre el nodo actual y el objetivo.
     */
    private double heuristic(Cell current, Cell goal) {
        // Distancia euclídea
        int dx = goal.getRow() - current.getRow();
        int dy = goal.getCol() - current.getCol();
        return Math.sqrt(dx*dx + dy*dy);
    }
    
    /**
     * Distancia real entre dos celdas (permite diagonal).
     */
    private double distance(Cell c1, Cell c2) {
        // Diferencia en fila y columna
        int dx = c2.getRow() - c1.getRow();
        int dy = c2.getCol() - c1.getCol();
        return Math.sqrt(dx*dx + dy*dy); // Euclídea
    }
    
    /**
     * Retorna la lista de vecinos de una celda, considerando movimientos en 8 direcciones.
     */
    private List<Cell> getNeighbors(Cell current, Grid grid) {
        List<Cell> neighbors = new ArrayList<>();
        int row = current.getRow();
        int col = current.getCol();
        
        // Movimientos en 8 direcciones (horizontal, vertical, diagonal)
        for(int dr = -1; dr <= 1; dr++) {
            for(int dc = -1; dc <= 1; dc++) {
                // Evitar el (0,0) que es la celda actual
                if(dr == 0 && dc == 0) continue;
                
                int newRow = row + dr;
                int newCol = col + dc;
                
                // Comprobar límites y si NO es obstáculo
                if(isValid(newRow, newCol, grid) && !grid.isObstacle(newRow, newCol)) {
                    neighbors.add(new Cell(newRow, newCol));
                }
            }
        }
        return neighbors;
    }
    
    private boolean isValid(int r, int c, Grid grid) {
        return (r >= 0 && r < grid.getRows() && c >= 0 && c < grid.getCols());
    }
}
