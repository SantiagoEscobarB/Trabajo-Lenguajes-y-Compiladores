import java.util.*;

public class Grafo {
    //Declarar atributos de numero de nodos, matriz de adyacencia, arreglo de victimas
    //(El indice del arreglo representa el nodo y victimas[indice] las victimas en ese nodo
    int V;
    int[][] matrizAdj;
    private int[] victimas;
    private static final Integer INF = 100_000_000;
    //Constructor del grafo y estimar las distancias entre nodos como INF
    public Grafo(int V) {
        this.V = V;
        matrizAdj = new int[V][V];
        victimas = new int[V];
        Arrays.fill(victimas, 0);
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                if (i == j) matrizAdj[i][j] = 0;
                else {
                    matrizAdj[i][j] = INF;
                }
            }
        }
    }

    //En el nodo numero node hay c victimas
    public void setVictimas(int node, int c) {
        victimas[node] = c;
    }

    //En la posicion [u][v] de la matriz de adyacencia hay un edge con distancia c
    //Al ser un grafo no dirigido, tambien hay que cambiar [v][u]
    public void addEdge(int u, int v, int d) {
        matrizAdj[u][v] = d;
        matrizAdj[v][u] = d;
    }

    //Un metodo de bfs para probar la construccion del grafo
    public void bfsCheck(int start) {
        //Inicializar en arreglo de los visitados y la queue
        boolean[] visited = new boolean[V];
        Queue<Integer> q = new LinkedList<>();

        //Marcamos el nodo incial como visitado y lo añadimos a la queue
        visited[start] = true;
        q.add(start);

        System.out.println("BFS desde nodo " + start + ":");

        //Mientras la queue no este vacia, vamos sacando de queue y para vecino del nodo que sacamos
        //que no hayamos visitado lo marcamos como visitado y agregamos a las queue
        while (!q.isEmpty()) {
            int node = q.poll();

            System.out.print(node + " ");

            for (int i = 0; i < V; i++) {
                if(matrizAdj[node][i] != INF && node != i && !visited[i]) {
                    visited[i] = true;
                    q.add(i);
                }
            }
        }
    }

    //Metodo auxiliar para Dijkstra que encuentra el nodo con menor distancia desde el origen
    public int minDistancia(int[] distances, boolean[] visited) {
        //Inicializar el minimo como INF y el index como -1
        int min = INF;
        int index = -1;
        //Para cada nodo, si no lo hemos visitado y la distancia a ese nodo es menor al min
        //actual, el min actual sera la distancia a ese nodo y el index el indice de ese nodo
        for (int i = 0; i < V ; i++) {
            if (!visited[i] && distances[i] < min) {
                min = distances[i];
                index = i;
            }
        }
        //Retornar el indice con el nodo a menor distancia
        return index;
    }

    public List<Integer> dijkstra(int guarida, int origen, int[][] adj) {
        //Inicializar arreglos de distancias, visitados y previos y llenarlos con valores base
        int[] distances = new int[V];
        boolean[] visited = new boolean[V];
        int[] prev = new int[V];

        Arrays.fill(distances, INF);
        Arrays.fill(visited, false);
        Arrays.fill(prev, -1);
        //La distancia del origen al origen siempre es 0
        distances[origen] = 0;
        //Mientras no se encuetre distancia menor a INF o se llegue a la guarida
        while(true) {
            //V es el indice del nodo con menor distancia desde el origen
            int v = minDistancia(distances, visited);
            if (v == -1) break; //No encontro ninguna distancia menor a INF
            if (v == guarida) break; //Si se encontro guarida, ya se construyo el camino
            //Marcar el nodo v como visited
            visited[v] = true;
            //Para cada vecino de v, ver si la distancia actual + la distancia desde V a ese vecino
            //es menor a la distancia actual a ese vecino, si si, cambiar la distancia
            // y cambiar el prev para reconstruir el camino
            for (int w = 0; w < V; w++) {
                if(distances[v] + adj[v][w] < distances[w]) {
                    distances[w] = distances[v] + adj[v][w];
                    prev[w] = v;
                }
            }
        }
        //Inicializar el camino, la guarida y reconstruir el camino con menor distancia
        List<Integer> camino = new ArrayList<>();
        int at = guarida;
        while (at != -1) {
            camino.add(at);
            at = prev[at];
        }
        Collections.reverse(camino);
        return camino;
    }

    public List<Integer> bellmanFordMaxVictimas(int origen, int guarida) {
        int[] maxVictimas = new int[V];
        int[] prev = new int[V];
        boolean[] visitado = new boolean[V]; // Para recordar qué nodos ya usamos en nuestro camino

        Arrays.fill(maxVictimas, -INF);
        Arrays.fill(prev, -1);

        // Empezamos desde el origen
        maxVictimas[origen] = victimas[origen];
        visitado[origen] = true; // Marcamos el origen como ya usado

        // Variable para saber si encontramos mejoras
        boolean cambioEnIteracion = false;

        for (int i = 0; i < V - 1; i++) {
            cambioEnIteracion = false;

            for (int u = 0; u < V; u++) {
                if (maxVictimas[u] == -INF) continue; // Si no podemos llegar aquí, lo saltamos

                for (int v = 0; v < V; v++) {
                    if (matrizAdj[u][v] != INF && u != v) {
                        // Calculamos cuántas víctimas tendríamos si vamos a 'v'
                        int nuevoValor;

                        if (visitado[v] || v == guarida) {
                            // Si ya pasamos por aquí antes, no contamos sus víctimas otra vez
                            nuevoValor = maxVictimas[u];
                        } else {
                            // Si es la primera vez que llegamos, sí contamos sus víctimas
                            nuevoValor = maxVictimas[u] + victimas[v];
                        }

                        if (nuevoValor > maxVictimas[v]) {
                            maxVictimas[v] = nuevoValor;
                            prev[v] = u;
                            // Marcamos este nodo como usado (excepto la guarida)
                            if (v != guarida && !visitado[v]) {
                                visitado[v] = true;
                            }
                            cambioEnIteracion = true;
                        }
                    }
                }
            }

            // Si no encontramos mejoras, ya terminamos de explorar
            if (!cambioEnIteracion) {
                break;
            }

            // Si ya llegamos a la guarida y no hay más mejoras, podemos parar
            if (maxVictimas[guarida] != -INF && !cambioEnIteracion) {
                break;
            }
        }

        // Revisamos si sí existe un camino hasta la guarida
        if (maxVictimas[guarida] == -INF) {
            System.out.println("No hay camino hacia la guarida");
            return new ArrayList<>();
        }

        // Ahora construimos el camino desde la guarida hacia atrás hasta el origen
        List<Integer> camino = new ArrayList<>();
        int actual = guarida;
        boolean[] visitadoCamino = new boolean[V];

        while (actual != -1) {
            if (visitadoCamino[actual]) {
                System.out.println("Ciclo detectado en reconstrucción");
                break;
            }

            visitadoCamino[actual] = true;
            camino.add(actual);

            // Recordamos que usamos este nodo
            visitado[actual] = true;

            actual = prev[actual];
        }

        Collections.reverse(camino); // Le damos vuelta al camino para que vaya del origen a la guarida

        System.out.println("Máximo de víctimas: " + maxVictimas[guarida]);

        return camino;
    }

    public void imprimirCamino(List<Integer> camino) {
        for (int i = 0; i < camino.size(); i++) {
            IO.print(camino.get(i) + " --> ");
        }
    }
}

