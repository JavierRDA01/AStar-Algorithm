
public class main {

	public static void main(String[] args) {
        // Configuración de la grid (ejemplo 5x5)
        Grid grid = new Grid(5, 5);
        
        // Marcar algunos obstáculos
        grid.setObstacle(1, 2, true); // obstáculo en (1,2)
        grid.setObstacle(2, 2, true); // obstáculo en (2,2)
        
        // Definir celda inicial y meta
        Cell start = new Cell(0, 0); // (fila 0, col 0)
        Cell goal = new Cell(4, 4);  // (fila 4, col 4)
        
        // Instanciar A* y buscar
        AStar aStar = new AStar();
        java.util.List<Cell> path = aStar.search(grid, start, goal);
        
        // Mostrar resultado
        if(path == null) {
            System.out.println("No se encontró camino.");
        } else {
            System.out.println("Camino encontrado:");
            for(Cell c : path) {
                System.out.println("(" + c.getRow() + "," + c.getCol() + ")");
            }
        }
    }

}
