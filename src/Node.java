import java.util.ArrayList;

public class Node {
    private int id;
    private int numeroPersonas;
    private ArrayList<Edge> edges;

    public Node(int id, int numeroPersonas) {
        this.id = id;
        this.numeroPersonas = numeroPersonas;
        this.edges = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public int getNumeroPersonas() {
        return numeroPersonas;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public int compareTo(Node that) {
        return Integer.compare(this.getNumeroPersonas(), that.getNumeroPersonas());
    }
}
