import java.util.Scanner;

public class utils {
    public static Grafo leerDatos() {
        //Inicializar el scanner y obtener la cantidad de nodos para la matriz
        Scanner sc = new Scanner(System.in);
        IO.println("Ingrese el numero de nodos: ");
        int V = Integer.parseInt(sc.nextLine());

        //Inicializar el grafo con el numero de nodos y declarar el String[] de partes con u, v, d, c
        Grafo grafo = new Grafo(V);
        IO.println("Ingrese edges y cantidad de victimas en formato $u, v, d, c");
        String linea;
        String[] partes;
        int u;
        int v;
        int d;
        int c;
        while (sc.hasNextLine()) {
            linea = sc.nextLine();
            partes = linea.split(",");
            u = Integer.parseInt(partes[0].trim());
            v = Integer.parseInt(partes[1].trim());
            d = Integer.parseInt(partes[2].trim());
            c = Integer.parseInt(partes[3].trim());

            grafo.addEdge(u, v, d);
            grafo.setVictimas(v, c);
        }
        sc.close();
        return grafo;
    }
}
