import java.util.ArrayList;
import java.util.List;

public class cuadricula {
    private nodo[][] aNodos;
    private int fila, columna;
    
    public cuadricula(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
        this.setaNodos(new nodo[fila][columna]);
        
        for(int i=0; i<fila; i++){
            for (int j=0; j<columna; j++){
                getaNodos()[i][j] = new nodo(i, j, true);
            }
        }
    }
    
    public void Inaccesible(int x, int y) {
        if(x >= 0 && x < fila && y >= 0 && y < columna){
            getaNodos()[x][y].accesible = false;
        }
    }
    
    public List<nodo> obtenerAdyacentes(nodo nodo) {
        List<nodo> adyacentes = new ArrayList<>();
        for (int i = -1; i <= 1; i++){
            for (int j = -1; j <= 1; j++){
                if (i == 0 && j == 0) continue; // Saltar el nodo actual
                
                int x = nodo.x + i;
                int y = nodo.y + j;
                
                if (x >= 0 && x < fila && y >= 0 && y < columna && getaNodos()[x][y].accesible) {
                    adyacentes.add(getaNodos()[x][y]);
                }
            }
        }
        return adyacentes;
    }

    public nodo[][] getaNodos() {
        return aNodos;
    }

    public void setaNodos(nodo[][] aNodos) {
        this.aNodos = aNodos;
    }
}
