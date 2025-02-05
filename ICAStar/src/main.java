import java.util.List;

public class main {
    public static void main(String[] args) {
        cuadricula cuadricula = new cuadricula(10, 10);
        cuadricula.Inaccesible(2, 2);
        cuadricula.Inaccesible(3, 3);

        nodo inicio = cuadricula.getaNodos()[0][0];
        nodo objetivo = cuadricula.getaNodos()[9][9];

        aStar aEstrella = new aStar(cuadricula, inicio, objetivo);
        List<nodo> ruta = aEstrella.encontrarRuta();

        if (ruta.isEmpty()) {
            System.out.println("No se encontró ruta.");
        } else {
            for (nodo nodo : ruta) {
                System.out.println("(" + nodo.x + ", " + nodo.y + ")");
            }
        }
    }
}