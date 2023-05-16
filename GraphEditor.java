package editorgraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class GraphEditor extends JFrame {
    private Map<String, Node> nodes;
    private Map<String, Edge> edges;
    private GraphPanel graphPanel;
    

    public GraphEditor() {
        super("Graph Editor");
        nodes = new HashMap<>();
        edges = new HashMap<>();
        graphPanel = new GraphPanel();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 600));
        getContentPane().add(graphPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void createNode(String key, String value, int x, int y) {
        Node node = new Node(key, value, x, y);
        nodes.put(key, node);
        graphPanel.repaint();
    }

    public void createEdge(String key, String value, String fromNodeKey, String toNodeKey) {
        Node fromNode = nodes.get(fromNodeKey);
        Node toNode = nodes.get(toNodeKey);
        if (fromNode != null && toNode != null) {
            Edge edge = new Edge(key, value, fromNode, toNode);
            edges.put(key, edge);
            graphPanel.repaint();
        }
    }

    public void editNode(String key, String newKey, String newValue) {
        Node node = nodes.get(key);
        if (node != null) {
            node.setKey(newKey);
            node.setValue(newValue);
            graphPanel.repaint();
        }
    }

    public void editEdge(String key, String newKey, String newValue) {
        Edge edge = edges.get(key);
        if (edge != null) {
            edge.setKey(newKey);
            edge.setValue(newValue);
            graphPanel.repaint();
        }
    }

    public void deleteNode(String key) {
        Node node = nodes.get(key);
        if (node != null) {
            nodes.remove(key);
            edges.entrySet().removeIf(entry -> entry.getValue().getFromNode() == node || entry.getValue().getToNode() == node);
            graphPanel.repaint();
        }
    }

    public void deleteEdge(String key) {
        edges.remove(key);
        graphPanel.repaint();
    }

    private class GraphPanel extends JPanel {
        private static final int NODE_SIZE = 50;
        private Node draggedNode;  // Nodul care este în proces de glisare
        private int dragOffsetX;  // Diferența dintre poziția cursorului mouse-ului și poziția x a nodului în timpul glisării
        private int dragOffsetY; 

        public GraphPanel() {
            setPreferredSize(new Dimension(800, 600));
            setBackground(Color.WHITE);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        String key = JOptionPane.showInputDialog("Enter node key:");
                        String value = JOptionPane.showInputDialog("Enter node value:");
                        createNode(key, value, e.getX(), e.getY());
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        String key = JOptionPane.showInputDialog("Enter edge key:");
                        String value = JOptionPane.showInputDialog("Enter edge value:");
                        String fromNodeKey = JOptionPane.showInputDialog("Enter source node key:");
                        String toNodeKey = JOptionPane.showInputDialog("Enter destination node key:");
                        createEdge(key, value, fromNodeKey, toNodeKey);
                    } else if (SwingUtilities.isMiddleMouseButton(e)) {
                        String key = JOptionPane.showInputDialog("Enter node or edge key to delete:");
                        if (nodes.containsKey(key)) {
                            deleteNode(key);
                        } else if (edges.containsKey(key)) {
                            deleteEdge(key);
                        }
                    }
                }
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        // Verificăm dacă cursorul se află pe un nod
                        for (Node node : nodes.values()) {
                            if (isCursorOverNode(node, e.getX(), e.getY())) {
                                // Începem glisarea
                                draggedNode = node;
                                dragOffsetX = e.getX() - node.getX();
                                dragOffsetY = e.getY() - node.getY();
                                break;
                            }
                        }
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e) && draggedNode != null) {
                        // Terminăm glisarea și actualizăm poziția nodului
                        draggedNode.setX(e.getX() - dragOffsetX);
                        draggedNode.setY(e.getY() - dragOffsetY);
                        graphPanel.repaint();
                        draggedNode = null;
                    }
                }
                

            });
            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (draggedNode != null) {
                        // Actualizăm poziția nodului pe măsură ce mouse-ul se deplasează
                        draggedNode.setX(e.getX() - dragOffsetX);
                        draggedNode.setY(e.getY() - dragOffsetY);
                        graphPanel.repaint();
                    }
                }
            });
            
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            for (Edge edge : edges.values()) {
                g.drawLine(edge.getFromNode().getX() + NODE_SIZE / 2, edge.getFromNode().getY() + NODE_SIZE / 2,
                		edge.getToNode().getX() + NODE_SIZE / 2, edge.getToNode().getY() + NODE_SIZE / 2);
                g.drawString(edge.getKey() + ": " + edge.getValue(),
                (edge.getFromNode().getX() + edge.getToNode().getX() + NODE_SIZE) / 2,
                (edge.getFromNode().getY() + edge.getToNode().getY() + NODE_SIZE) / 2);
                }
                for (Node node : nodes.values()) {
                g.setColor(Color.LIGHT_GRAY);
                g.fillOval(node.getX(), node.getY(), NODE_SIZE, NODE_SIZE);
                g.setColor(Color.BLACK);
                g.drawOval(node.getX(), node.getY(), NODE_SIZE, NODE_SIZE);
                g.drawString(node.getKey() + ": " + node.getValue(), node.getX() + NODE_SIZE / 4, node.getY() + NODE_SIZE / 2);
                }
                }
        private boolean isCursorOverNode(Node node, int x, int y) {
            return x >= node.getX() && x <= node.getX() + NODE_SIZE &&
                   y >= node.getY() && y <= node.getY() + NODE_SIZE;
        }
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(800, 600);
        }
    }

    private class Node {
        private String key;
        private String value;
        private int x;
        private int y;

        public Node(String key, String value, int x, int y) {
            this.key = key;
            this.value = value;
            this.x = x;
            this.y = y;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }

    private class Edge {
        private String key;
        private String value;
        private Node fromNode;
        private Node toNode;

        public Edge(String key, String value, Node fromNode, Node toNode) {
            this.key = key;
            this.value = value;
            this.fromNode = fromNode;
            this.toNode = toNode;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Node getFromNode() {
            return fromNode;
        }

        public Node getToNode() {
            return toNode;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GraphEditor());
    }
}
