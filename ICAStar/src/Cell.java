public class Cell {
    private int row;
    private int col;
    
    private double g;     // coste desde el nodo inicial
    private double h;     // estimación (heurística) hasta el objetivo
    private double f;     // f = g + h
    
    private Cell parent;  // para reconstruir el camino

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    // Getters y setters para g, h, f y parent
    public int getRow() { 
    	return row; }
    public int getCol() {
    	return col; }

    public double getG() { return g; }
    public void setG(double g) { this.g = g; }

    public double getH() { return h; }
    public void setH(double h) { this.h = h; }

    public double getF() { return f; }
    public void setF(double f) { this.f = f; }

    public Cell getParent() { return parent; }
    public void setParent(Cell parent) { this.parent = parent; }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        Cell other = (Cell) obj;
        return this.row == other.row && this.col == other.col;
    }

    @Override
    public int hashCode() {
        return 31 * row + col;
    }
}
