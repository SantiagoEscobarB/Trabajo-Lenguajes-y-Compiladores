//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
void main() {
        Grafo grafo = utils.leerDatos();
        grafo.imprimirCamino(grafo.dijkstra(3, 0, grafo.adj));
    }

