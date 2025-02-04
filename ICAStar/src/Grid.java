
public class Grid {
    private int rows;      // M
    private int cols;      // N
    private boolean[][] obstacles; // true si es obstáculo, false si es transitable

    public Grid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        obstacles = new boolean[rows][cols];
    }
//Hola
    // Marcar una celda como obstáculo
    public void setObstacle(int row, int col, boolean isObstacle) {
        obstacles[row][col] = isObstacle;
    }

    // Comprobar si una celda es un obstáculo
    public boolean isObstacle(int row, int col) {
        return obstacles[row][col];
    }

    // Métodos para devolver filas y columnas
    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
    
    // Otros métodos (por ejemplo, imprimir la rejilla, etc.)
}
