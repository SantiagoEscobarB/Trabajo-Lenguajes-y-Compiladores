//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
void main() {
        //Declarar y asignar el grafo con lo que se pase por consola
        Grafo grafo = utils.leerDatos();
        //Prueba de bellmanford
        grafo.imprimirCamino(grafo.bellmanFordMaxVictimas(3, 0));
    }

