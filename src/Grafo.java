import java.util.*;

public class Grafo {
    int V;
    int[][] matrizAdj;
    private int[] victimas;
    private static final Integer INF = 100_000_000;
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

    public void setVictimas(int node, int c) {
        victimas[node] = c;
    }

    public void addEdge(int u, int v, int d) {
        matrizAdj[u][v] = d;
        matrizAdj[v][u] = d;
    }

    public void bfsCheck(int start) {
        boolean[] visited = new boolean[V];
        Queue<Integer> q = new LinkedList<>();

        visited[start] = true;
        q.add(start);

        System.out.println("BFS desde nodo " + start + ":");

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

    public int minDistancia(int[] distances, boolean[] visited) {
        int min = INF;
        int index = -1;

        for (int i = 0; i < V ; i++) {
            if (!visited[i] && distances[i] < min) {
                min = distances[i];
                index = i;
            }
        }
        return index;
    }

    public List<Integer> dijkstra(int guarida, int origen, int[][] adj) {
        int[] distances = new int[V];
        boolean[] visited = new boolean[V];
        int[] prev = new int[V];

        Arrays.fill(distances, INF);
        Arrays.fill(visited, false);
        Arrays.fill(prev, -1);

        distances[origen] = 0;

        while(true) {
            int v = minDistancia(distances, visited);
            if (v == -1) break; //No encontro ninguna distancia menor a INF
            if (v == guarida) break; //Si se encontro guarida, ya se construyo el camino

            visited[v] = true;
            for (int w = 0; w < V; w++) {
                if(distances[v] + adj[v][w] < distances[w]) {
                    distances[w] = distances[v] + adj[v][w];
                    prev[w] = v;
                }
            }
        }
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
        boolean[] visitado = new boolean[V]; // Nuevo: rastrear nodos ya visitados en el camino

        Arrays.fill(maxVictimas, -INF);
        Arrays.fill(prev, -1);

        // Inicializar origen
        maxVictimas[origen] = victimas[origen];
        visitado[origen] = true; // Marcar origen como visitado

        // Detección temprana: verificar si guarida es alcanzable
        boolean cambioEnIteracion = false;

        for (int i = 0; i < V - 1; i++) {
            cambioEnIteracion = false;

            for (int u = 0; u < V; u++) {
                if (maxVictimas[u] == -INF) continue; // Saltar nodos no alcanzables

                for (int v = 0; v < V; v++) {
                    if (matrizAdj[u][v] != INF && u != v) {
                        // Calcular nuevo valor potencial
                        int nuevoValor;

                        if (visitado[v] || v ==guarida) {
                            // Si v ya fue visitado en algún camino, no sumar sus víctimas
                            nuevoValor = maxVictimas[u];
                        } else {
                            // Si v no ha sido visitado, sumar sus víctimas
                            nuevoValor = maxVictimas[u] + victimas[v];
                        }

                        if (nuevoValor > maxVictimas[v]) {
                            maxVictimas[v] = nuevoValor;
                            prev[v] = u;
                            // Marcar como visitado solo si sumamos sus víctimas
                            if (v != guarida && !visitado[v]) {
                                visitado[v] = true;
                            }
                            cambioEnIteracion = true;
                        }
                    }
                }
            }

            // Optimización: si no hubo cambios, no hay más caminos por explorar
            if (!cambioEnIteracion) {
                break;
            }

            // Si la guarida ya es alcanzable y no hay cambios, podemos parar
            if (maxVictimas[guarida] != -INF && !cambioEnIteracion) {
                break;
            }
        }

        // Verificar si hay camino a la guarida
        if (maxVictimas[guarida] == -INF) {
            System.out.println("No hay camino hacia la guarida");
            return new ArrayList<>();
        }

        // Reconstruir camino y marcar nodos visitados
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

            // Marcar este nodo como visitado para futuros cálculos
            visitado[actual] = true;

            actual = prev[actual];
        }

        Collections.reverse(camino);

        System.out.println("Máximo de víctimas: " + maxVictimas[guarida]);

        return camino;
    }

    public void imprimirCamino(List<Integer> camino) {
        for (int i = 0; i < camino.size(); i++) {
            IO.print(camino.get(i) + " --> ");
        }
    }
}

