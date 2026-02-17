import java.util.*;

public class Grafo {
    int V;
    int[][] matrizAdj;
    List<Edge> listaAdj;
    private int[] victimas;
    private static final Integer INF = 100_000_000;
    public Grafo(int V) {
        this.V = V;
        matrizAdj = new int[V][V];
        listaAdj = new ArrayList<>();
        victimas = new int[V];

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

        listaAdj.add(new Edge(u, v, d));
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
        int[] prev        = new int[V];
        Arrays.fill(maxVictimas, Integer.MIN_VALUE);
        Arrays.fill(prev, -1);
        maxVictimas[origen] = victimas[origen];

        for (int i = 0; i < V - 1; i++) {
            boolean actualizado = false;

                for (Edge e : listaAdj) {
                    int u = e.source;
                    int v = e.target;

                    if (maxVictimas[u] == Integer.MIN_VALUE) continue;
                    int aporte       = estaEnCamino(v, u, prev) ? 0 : victimas[v];
                    int nuevaVictimas = maxVictimas[u] + aporte;

                    if (nuevaVictimas > maxVictimas[v]) {
                        maxVictimas[v] = nuevaVictimas;
                        prev[v]        = u;
                        actualizado    = true;
                    }
                }
            if (!actualizado) break; // Optimización: convergencia temprana
        }

        // ── Sin camino ────────────────────────────────────────────────────────────
        if (maxVictimas[guarida] == Integer.MIN_VALUE) {
            System.out.println("No hay camino hacia la guarida");
            return null;
        }

        // ── Reconstrucción del camino siguiendo prev[] ────────────────────────────
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
            actual = prev[actual];
        }

        Collections.reverse(camino);
        System.out.println("Camino: " + camino);
        System.out.println("Máximo de víctimas: " + maxVictimas[guarida]);
        return camino;
    }

    private boolean estaEnCamino(int objetivo, int desde, int[] prev) {
        boolean[] visitado = new boolean[V];
        int actual = desde;
        while (actual != -1) {
            if (actual == objetivo) return true;
            if (visitado[actual])   break;
            visitado[actual] = true;
            actual = prev[actual];
        }
        return false;
    }




    public void imprimirCamino(List<Integer> camino) {
        for (int i = 0; i < camino.size(); i++) {
            IO.print(camino.get(i) + " --> ");
        }
    }
}

