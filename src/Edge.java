public class Edge {
    private Node from;
    private Node to;
    private int distance;

    public Edge (Node from, Node to, int distance) {
        this.from = from;
        this.to = to;
        this.distance = distance;
    }
}
