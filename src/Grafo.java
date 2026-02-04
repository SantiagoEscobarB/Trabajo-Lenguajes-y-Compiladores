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
}

