import java.util.*;

public class Grafo {
    int V;
    int[][] adj;
    private int[] victimas;
    private static final Integer INF = 100_000_000;
    public Grafo(int V) {
        this.V = V;
        adj = new int[V][V];
        victimas = new int[V];

        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                if (i == j) adj[i][j] = 0;
                else {
                    adj[i][j] = INF;
                }
            }
        }
    }

    public void setVictimas(int node, int c) {
        victimas[node] = c;
    }

    public void addEdge(int u, int v, int d) {
        adj[u][v] = d;
        adj[v][u] = d;
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
                if(adj[node][i] != INF && node != i && !visited[i]) {
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

    public void imprimirCamino(List<Integer> camino) {
        for (int i = 0; i < camino.size(); i++) {
            IO.print(camino.get(i) + " --> ");
        }
    }
}

