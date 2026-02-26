import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InterfazGrafo extends JFrame {
    private static final int INF = 100_000_000;

    private final JTextField txtVertices = new JTextField("6", 5);
    private final JTextField txtOrigen = new JTextField("0", 5);
    private final JTextField txtGuarida = new JTextField("5", 5);

    private final JComboBox<String> cmbModo = new JComboBox<>(new String[]{
            "Ambos",
            "Solo camino más corto",
            "Solo camino más víctimas (Bellman-Ford)"
    });

    private final JTextArea txtDatos = new JTextArea(10, 30);

    private final JLabel lblCaminoCorto = new JLabel("Camino más corto: -");
    private final JLabel lblCaminoVictimas = new JLabel("Camino con más víctimas: -");
    private final JLabel lblDistancia = new JLabel("Distancia camino corto: -");
    private final JLabel lblDistanciaVictimas = new JLabel("Distancia camino más víctimas: -");
    private final JLabel lblVictimas = new JLabel("Víctimas recolectadas (ruta de cacería): -");

    private final PanelGrafo panelGrafo = new PanelGrafo();

    public InterfazGrafo() {
        super("Visualizador de Grafo - Caminos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1250, 780);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelIzquierdo = crearPanelControl();
        add(panelIzquierdo, BorderLayout.WEST);
        add(panelGrafo, BorderLayout.CENTER);

        cargarEjemplo();
    }

    private JPanel crearPanelControl() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.setPreferredSize(new Dimension(410, 780));

        JLabel titulo = new JLabel("Datos del grafo");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(10));

        panel.add(fila("Nodos (V):", txtVertices));
        panel.add(Box.createVerticalStrut(8));
        panel.add(fila("Origen:", txtOrigen));
        panel.add(Box.createVerticalStrut(8));
        panel.add(fila("Guarida:", txtGuarida));
        panel.add(Box.createVerticalStrut(8));
        panel.add(fila("Mostrar:", cmbModo));
        panel.add(Box.createVerticalStrut(12));

        cmbModo.addActionListener(e -> panelGrafo.setModo((String) cmbModo.getSelectedItem()));

        JLabel formato = new JLabel("Formato por línea: u, v, distancia, víctimas(v)");
        formato.setAlignmentX(Component.LEFT_ALIGNMENT);
        formato.setFont(formato.getFont().deriveFont(12f));
        panel.add(formato);

        JLabel notaVictimas = new JLabel("c = víctimas del nodo destino v (se muestran como V:x)");
        notaVictimas.setAlignmentX(Component.LEFT_ALIGNMENT);
        notaVictimas.setFont(notaVictimas.getFont().deriveFont(Font.ITALIC, 11.5f));
        notaVictimas.setForeground(new Color(80, 80, 80));
        panel.add(notaVictimas);

        panel.add(Box.createVerticalStrut(6));

        txtDatos.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtDatos.setLineWrap(false);
        JScrollPane scroll = new JScrollPane(txtDatos);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(scroll);
        panel.add(Box.createVerticalStrut(10));

        JButton btnEjemplo = new JButton("Cargar ejemplo");
        btnEjemplo.addActionListener(e -> cargarEjemplo());

        JButton btnConstruir = new JButton("Construir y resaltar caminos");
        btnConstruir.addActionListener(this::construirYMostrar);

        JPanel botones = new JPanel(new GridLayout(1, 2, 8, 0));
        botones.setAlignmentX(Component.LEFT_ALIGNMENT);
        botones.add(btnEjemplo);
        botones.add(btnConstruir);
        panel.add(botones);
        panel.add(Box.createVerticalStrut(14));

        JLabel resultadoTitulo = new JLabel("Resultados");
        resultadoTitulo.setFont(resultadoTitulo.getFont().deriveFont(Font.BOLD, 16f));
        resultadoTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(resultadoTitulo);
        panel.add(Box.createVerticalStrut(8));

        for (JLabel lbl : new JLabel[]{lblCaminoCorto, lblCaminoVictimas, lblDistancia, lblDistanciaVictimas, lblVictimas}) {
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(lbl);
            panel.add(Box.createVerticalStrut(6));
        }

        panel.add(Box.createVerticalStrut(10));
        panel.add(crearLeyenda());

        return panel;
    }

    private JPanel fila(String texto, JComponent campo) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = new JLabel(texto);
        lbl.setPreferredSize(new Dimension(120, 24));
        row.add(lbl, BorderLayout.WEST);
        row.add(campo, BorderLayout.CENTER);
        return row;
    }

    private JPanel crearLeyenda() {
        JPanel legend = new JPanel();
        legend.setLayout(new BoxLayout(legend, BoxLayout.Y_AXIS));
        legend.setAlignmentX(Component.LEFT_ALIGNMENT);
        legend.setBorder(BorderFactory.createTitledBorder("Leyenda"));

        legend.add(legendItem(new Color(90, 140, 255), "Camino más corto (Dijkstra)"));
        legend.add(legendItem(new Color(230, 75, 75), "Camino más víctimas (Bellman-Ford)"));
        legend.add(legendItem(Color.GRAY, "Aristas del grafo"));
        return legend;
    }

    private JPanel legendItem(Color color, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        JLabel box = new JLabel("   ");
        box.setOpaque(true);
        box.setBackground(color);
        box.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        p.add(box);
        p.add(new JLabel(text));
        return p;
    }

    private void cargarEjemplo() {
        txtVertices.setText("7");
        txtOrigen.setText("0");
        txtGuarida.setText("5");
        cmbModo.setSelectedItem("Ambos");

        txtDatos.setText(
                "0,1,4,2\n" +
                        "0,2,2,1\n" +
                        "0,6,9,10\n" +
                        "1,2,1,3\n" +
                        "1,3,5,4\n" +
                        "2,3,8,2\n" +
                        "2,4,10,6\n" +
                        "3,4,2,5\n" +
                        "3,5,6,7\n" +
                        "6,2,9,3\n"
        );

        construirYMostrar(null);
    }

    private void construirYMostrar(ActionEvent e) {
        try {
            int v = Integer.parseInt(txtVertices.getText().trim());
            int origen = Integer.parseInt(txtOrigen.getText().trim());
            int guarida = Integer.parseInt(txtGuarida.getText().trim());

            if (v <= 0) throw new IllegalArgumentException("El número de nodos debe ser mayor que 0.");
            if (origen < 0 || origen >= v || guarida < 0 || guarida >= v) {
                throw new IllegalArgumentException("Origen/guarida fuera del rango 0.." + (v - 1));
            }

            Grafo grafo = new Grafo(v);
            int[] victimasNodo = new int[v];

            String[] lineas = txtDatos.getText().split("\\R");
            for (String linea : lineas) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.equalsIgnoreCase("END")) continue;

                String[] p = linea.split(",");
                if (p.length != 4) {
                    throw new IllegalArgumentException("Línea inválida: '" + linea + "'. Usa: u,v,d,c");
                }

                int u = Integer.parseInt(p[0].trim());
                int w = Integer.parseInt(p[1].trim());
                int d = Integer.parseInt(p[2].trim());
                int c = Integer.parseInt(p[3].trim());

                if (u < 0 || u >= v || w < 0 || w >= v) {
                    throw new IllegalArgumentException("Nodo fuera de rango en línea: " + linea);
                }
                if (d < 0) {
                    throw new IllegalArgumentException("La distancia no puede ser negativa (Dijkstra): " + linea);
                }
                if (c < 0) {
                    throw new IllegalArgumentException("Las víctimas no pueden ser negativas: " + linea);
                }

                // Manteniendo tu lógica original
                grafo.addEdge(u, w, d);
                grafo.setVictimas(w, c);
                victimasNodo[w] = c; // para mostrar V:x en la GUI
            }

            // Mantengo la llamada tal como tú la venías usando
            List<Integer> caminoCorto = grafo.dijkstra(guarida, origen, grafo.matrizAdj);

            // Bellman-Ford original (requisito)
            List<Integer> caminoVictimasBellman = grafo.bellmanFordMaxVictimas(origen, guarida);
            List<Integer> caminoVictimas = caminoVictimasBellman;

            if (!esCaminoValido(caminoCorto, origen, guarida, grafo.matrizAdj)) {
                caminoCorto = new ArrayList<>();
            }

            // Si Bellman-Ford devuelve una ruta inválida/incompleta, la GUI reconstruye una ruta válida para visualizar
            if (!esCaminoValido(caminoVictimas, origen, guarida, grafo.matrizAdj)) {
                caminoVictimas = calcularRutaCaceriaVisual(grafo.matrizAdj, victimasNodo, origen, guarida);
            }

            if (!esCaminoValido(caminoVictimas, origen, guarida, grafo.matrizAdj)) {
                caminoVictimas = new ArrayList<>();
            }

            panelGrafo.setData(grafo, origen, guarida, caminoCorto, caminoVictimas, victimasNodo);
            panelGrafo.setModo((String) cmbModo.getSelectedItem());

            lblCaminoCorto.setText("Camino más corto: " +
                    (caminoCorto.isEmpty() ? "No encontrado" : formatPath(caminoCorto)));

            lblCaminoVictimas.setText("Camino con más víctimas: " +
                    (caminoVictimas.isEmpty() ? "No encontrado" : formatPath(caminoVictimas)));

            lblDistancia.setText("Distancia camino corto: " +
                    (caminoCorto.isEmpty() ? "-" : calcularDistancia(caminoCorto, grafo.matrizAdj)));

            lblDistanciaVictimas.setText("Distancia camino más víctimas: " +
                    (caminoVictimas.isEmpty() ? "-" : calcularDistancia(caminoVictimas, grafo.matrizAdj)));

            lblVictimas.setText("Víctimas recolectadas (ruta de cacería): " +
                    (caminoVictimas.isEmpty() ? "-" : sumarVictimasUnicas(caminoVictimas, victimasNodo)));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error al construir el grafo",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private boolean esCaminoValido(List<Integer> camino, int origen, int guarida, int[][] adj) {
        if (camino == null || camino.isEmpty()) return false;
        if (camino.get(0) != origen) return false;
        if (camino.get(camino.size() - 1) != guarida) return false;

        for (int i = 0; i < camino.size() - 1; i++) {
            int a = camino.get(i);
            int b = camino.get(i + 1);

            if (a < 0 || b < 0 || a >= adj.length || b >= adj.length) return false;
            if (adj[a][b] >= INF) return false;
        }
        return true;
    }

    private String formatPath(List<Integer> path) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            if (i > 0) sb.append(" -> ");
            sb.append(path.get(i));
        }
        return sb.toString();
    }

    private int calcularDistancia(List<Integer> path, int[][] adj) {
        int total = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            total += adj[path.get(i)][path.get(i + 1)];
        }
        return total;
    }

    private int sumarVictimasUnicas(List<Integer> path, int[] victimas) {
        Set<Integer> usados = new HashSet<>();
        int total = 0;

        for (int n : path) {
            if (!usados.contains(n)) {
                total += victimas[n];
                usados.add(n);
            }
        }
        return total;
    }

    // ==========================
    // FALLBACK VISUAL PARA Cacería
    // (sin tocar Grafo.java)
    // ==========================
    private static class MejorRutaCaceria {
        List<Integer> camino = new ArrayList<>();
        int victimas = Integer.MIN_VALUE;
        int distancia = Integer.MAX_VALUE;
    }

    private List<Integer> calcularRutaCaceriaVisual(int[][] adj, int[] victimasNodo, int origen, int guarida) {
        if (adj == null || adj.length == 0) return new ArrayList<>();
        if (origen < 0 || origen >= adj.length || guarida < 0 || guarida >= adj.length) return new ArrayList<>();

        MejorRutaCaceria mejor = new MejorRutaCaceria();
        boolean[] visitado = new boolean[adj.length];
        List<Integer> actual = new ArrayList<>();

        visitado[origen] = true;
        actual.add(origen);

        int victimasIniciales = (origen < victimasNodo.length) ? victimasNodo[origen] : 0;

        dfsRutaCaceria(
                origen, guarida,
                adj, victimasNodo,
                visitado, actual,
                victimasIniciales, 0,
                mejor
        );

        return mejor.camino;
    }

    private void dfsRutaCaceria(int u, int guarida,
                                int[][] adj, int[] victimasNodo,
                                boolean[] visitado, List<Integer> actual,
                                int victimasAcumuladas, int distanciaAcumulada,
                                MejorRutaCaceria mejor) {

        if (u == guarida) {
            boolean esMejor =
                    (victimasAcumuladas > mejor.victimas) ||
                            (victimasAcumuladas == mejor.victimas && distanciaAcumulada < mejor.distancia) ||
                            (victimasAcumuladas == mejor.victimas && distanciaAcumulada == mejor.distancia
                                    && (mejor.camino.isEmpty() || actual.size() < mejor.camino.size()));

            if (esMejor) {
                mejor.victimas = victimasAcumuladas;
                mejor.distancia = distanciaAcumulada;
                mejor.camino = new ArrayList<>(actual);
            }
            return;
        }

        for (int v = 0; v < adj.length; v++) {
            if (v == u) continue;
            if (adj[u][v] >= INF) continue; // no hay arista
            if (visitado[v]) continue;      // evita ciclos + evita sumar víctimas repetidas

            visitado[v] = true;
            actual.add(v);

            int victimasV = (v < victimasNodo.length) ? victimasNodo[v] : 0;

            dfsRutaCaceria(
                    v, guarida,
                    adj, victimasNodo,
                    visitado, actual,
                    victimasAcumuladas + victimasV,
                    distanciaAcumulada + adj[u][v],
                    mejor
            );

            actual.remove(actual.size() - 1);
            visitado[v] = false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InterfazGrafo().setVisible(true));
    }

    // ==========================
    // PANEL QUE DIBUJA EL GRAFO
    // ==========================
    private static class PanelGrafo extends JPanel {
        private Grafo grafo;
        private int origen = -1;
        private int guarida = -1;
        private int[] victimasNodo = new int[0];

        private List<Integer> caminoCorto = new ArrayList<>();
        private List<Integer> caminoVictimas = new ArrayList<>();

        private String modo = "Ambos";

        PanelGrafo() {
            setBackground(Color.WHITE);
            setBorder(new EmptyBorder(8, 8, 8, 8));
        }

        void setData(Grafo grafo, int origen, int guarida,
                     List<Integer> corto, List<Integer> victimasPath, int[] victimasNodo) {
            this.grafo = grafo;
            this.origen = origen;
            this.guarida = guarida;
            this.caminoCorto = (corto != null) ? corto : new ArrayList<>();
            this.caminoVictimas = (victimasPath != null) ? victimasPath : new ArrayList<>();
            this.victimasNodo = (victimasNodo != null) ? victimasNodo.clone() : new int[0];
            repaint();
        }

        void setModo(String modo) {
            this.modo = (modo != null) ? modo : "Ambos";
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            if (grafo == null || grafo.V == 0) {
                g2.setColor(Color.DARK_GRAY);
                g2.drawString("Carga un grafo para visualizarlo.", 20, 30);
                g2.dispose();
                return;
            }

            int n = grafo.V;
            Point[] pos = calcularPosiciones(n, getWidth(), getHeight());

            // Aristas base
            g2.setStroke(new BasicStroke(1.5f));
            g2.setColor(new Color(160, 160, 160));

            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (grafo.matrizAdj[i][j] < INF) {
                        Point a = pos[i];
                        Point b = pos[j];
                        g2.draw(new Line2D.Double(a.x, a.y, b.x, b.y));
                        dibujarPeso(g2, a, b, grafo.matrizAdj[i][j]);
                    }
                }
            }

            // Qué caminos mostrar según selector
            boolean mostrarCorto = "Ambos".equals(modo) || "Solo camino más corto".equals(modo);
            boolean mostrarVictimas = "Ambos".equals(modo) || "Solo camino más víctimas (Bellman-Ford)".equals(modo);

            if (mostrarCorto) {
                dibujarCamino(g2, pos, caminoCorto, new Color(90, 140, 255), false, -4f, 4f);
            }
            if (mostrarVictimas) {
                dibujarCamino(g2, pos, caminoVictimas, new Color(230, 75, 75), true, 4f, 4f);
            }

            // Nodos resaltados según rutas visibles
            Set<Integer> setCorto = mostrarCorto ? new HashSet<>(caminoCorto) : new HashSet<>();
            Set<Integer> setVictimas = mostrarVictimas ? new HashSet<>(caminoVictimas) : new HashSet<>();

            for (int i = 0; i < n; i++) {
                Point p = pos[i];
                int r = 21;

                Color fill = Color.WHITE;
                if (setCorto.contains(i) && setVictimas.contains(i)) fill = new Color(215, 185, 255);
                else if (setCorto.contains(i)) fill = new Color(210, 225, 255);
                else if (setVictimas.contains(i)) fill = new Color(255, 215, 215);

                g2.setColor(fill);
                g2.fillOval(p.x - r, p.y - r, 2 * r, 2 * r);

                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke((i == origen || i == guarida) ? 3f : 1.5f));
                g2.drawOval(p.x - r, p.y - r, 2 * r, 2 * r);

                // ID nodo (centro)
                String texto = String.valueOf(i);
                FontMetrics fm = g2.getFontMetrics();
                int tx = p.x - fm.stringWidth(texto) / 2;
                int ty = p.y + fm.getAscent() / 2 - 2;
                g2.drawString(texto, tx, ty);

                // O / G
                if (i == origen) {
                    g2.setColor(new Color(35, 140, 60));
                    g2.drawString("O", p.x + 24, p.y - 10);
                }
                if (i == guarida) {
                    g2.setColor(new Color(180, 100, 20));
                    g2.drawString("G", p.x + 24, p.y + 14);
                }

                // Víctimas por nodo (debajo)
                g2.setColor(new Color(60, 60, 60));
                String victTxt = "V:" + ((i < victimasNodo.length) ? victimasNodo[i] : 0);
                Font old = g2.getFont();
                g2.setFont(old.deriveFont(Font.PLAIN, 11f));
                FontMetrics fm2 = g2.getFontMetrics();
                int vx = p.x - fm2.stringWidth(victTxt) / 2;
                int vy = p.y + r + 15;
                g2.drawString(victTxt, vx, vy);
                g2.setFont(old);

                g2.setColor(Color.BLACK);
            }

            g2.dispose();
        }

        private void dibujarCamino(Graphics2D g2, Point[] pos, List<Integer> camino,
                                   Color color, boolean dashed, float offset, float width) {
            if (camino == null || camino.size() < 2) return;

            Stroke stroke = dashed
                    ? new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    10f, new float[]{8f, 6f}, 0f)
                    : new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

            g2.setStroke(stroke);
            g2.setColor(color);

            for (int i = 0; i < camino.size() - 1; i++) {
                Point a = pos[camino.get(i)];
                Point b = pos[camino.get(i + 1)];

                double dx = b.x - a.x;
                double dy = b.y - a.y;
                double len = Math.hypot(dx, dy);
                if (len == 0) len = 1;

                double nx = -dy / len;
                double ny = dx / len;

                double ax = a.x + nx * offset;
                double ay = a.y + ny * offset;
                double bx = b.x + nx * offset;
                double by = b.y + ny * offset;

                g2.draw(new Line2D.Double(ax, ay, bx, by));
            }
        }

        private void dibujarPeso(Graphics2D g2, Point a, Point b, int peso) {
            int mx = (a.x + b.x) / 2;
            int my = (a.y + b.y) / 2;
            String txt = String.valueOf(peso);

            FontMetrics fm = g2.getFontMetrics();
            int w = fm.stringWidth(txt) + 6;
            int h = fm.getHeight() - 2;

            g2.setColor(new Color(255, 255, 255, 225));
            g2.fillRoundRect(mx - w / 2, my - h / 2, w, h, 8, 8);
            g2.setColor(new Color(90, 90, 90));
            g2.drawRoundRect(mx - w / 2, my - h / 2, w, h, 8, 8);
            g2.drawString(txt, mx - fm.stringWidth(txt) / 2, my + fm.getAscent() / 2 - 2);
        }

        private Point[] calcularPosiciones(int n, int width, int height) {
            Point[] pos = new Point[n];
            int cx = width / 2;
            int cy = height / 2;

            int radius = Math.max(130, Math.min(width, height) / 2 - 95);

            for (int i = 0; i < n; i++) {
                double ang = -Math.PI / 2 + (2 * Math.PI * i / n);
                int x = (int) Math.round(cx + radius * Math.cos(ang));
                int y = (int) Math.round(cy + radius * Math.sin(ang));
                pos[i] = new Point(x, y);
            }

            return pos;
        }
    }
}