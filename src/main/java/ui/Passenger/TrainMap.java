package ui.Passenger;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Map tab: draws the stations as retro squares and the tracks as lines.
 */
public class TrainMap extends Tab {

    // Retro-themed color palette constants
    private static final Color NEON_BLUE = Color.web("#00e5ff");
    private static final Color NEON_GREEN = Color.web("#00ff88");
    private static final Color NEON_RED = Color.web("#ff073a");
    private static final Color RETRO_GRAY = Color.web("#7a7ac9");

    public TrainMap() {
        super("Train Map");
        setClosable(false);

        Label title = new Label("Network Map");
        // Apply retro font to the title if desired
        title.setStyle("-fx-font-family: 'Pixel Operator'; -fx-font-size: 20px; -fx-text-fill: #00e5ff;");

        Pane mapPane = createMapPane();

        VBox layout = new VBox(20, title, mapPane);
        layout.setPadding(new Insets(20));

        setContent(layout);
    }

    private Pane createMapPane() {
        TrainMapData mapData = new TrainMapData();
        NetworkMapPane mapPane = new NetworkMapPane();

        // 1. Render nodes
        for (MapNode node : mapData.getNodes()) {
            mapPane.createNode(node.name(), node.x(), node.y(), node.color(), node.labelPosition());
        }

        // 2. Render track connections
        for (MapConnection conn : mapData.getConnections()) {
            mapPane.createConnection(conn.fromStation(), conn.toStation(), conn.color());
        }

        mapPane.setPrefSize(600, 500);
        return mapPane;
    }

    public enum LabelPosition {
        TOP, BOTTOM, LEFT, RIGHT
    }

    private record MapNode(String name, double x, double y, Color color, LabelPosition labelPosition) {
        public MapNode(String name, double x, double y) {
            this(name, x, y, NEON_RED, LabelPosition.TOP);
        }
    }

    private record MapConnection(String fromStation, String toStation, Color color) {
        public MapConnection(String fromStation, String toStation) {
            this(fromStation, toStation, RETRO_GRAY);
        }
    }

    private static class TrainMapData {
        private final List<MapNode> nodes = new ArrayList<>();
        private final List<MapConnection> connections = new ArrayList<>();

        public TrainMapData() {
            // Using your new retro neon colors here
            addNode("Church Gate", 100, 100, NEON_BLUE, LabelPosition.TOP);
            addNode("CSMT", 400, 100, NEON_GREEN, LabelPosition.TOP);
            addNode("Panvel", 400, 400, NEON_RED, LabelPosition.BOTTOM);
            addNode("Bandra", 100, 400, NEON_BLUE, LabelPosition.BOTTOM);

            addConnection("Church Gate", "CSMT", NEON_BLUE);
            addConnection("CSMT", "Panvel", NEON_GREEN);
            addConnection("Panvel", "Bandra", NEON_RED);
            addConnection("Bandra", "Church Gate", RETRO_GRAY);
        }

        private void addNode(String name, double x, double y, Color color, LabelPosition position) {
            nodes.add(new MapNode(name, x, y, color, position));
        }

        private void addConnection(String from, String to, Color color) {
            connections.add(new MapConnection(from, to, color));
        }

        public List<MapNode> getNodes() { return nodes; }
        public List<MapConnection> getConnections() { return connections; }
    }

    private static class NetworkMapPane extends Pane {

        private final Pane tracksPane = new Pane();
        private final Pane nodesPane = new Pane();
        private final Map<String, Point2D> stationPositions = new HashMap<>();

        public NetworkMapPane() {
            getChildren().addAll(tracksPane, nodesPane);
        }

        public void createNode(String stationName, double xPos, double yPos, Color nodeColor, LabelPosition position) {
            stationPositions.put(stationName, new Point2D(xPos, yPos));

            // Retro Square Node (Width & Height set to 12px)
            double size = 12;
            Rectangle square = new Rectangle(xPos - (size / 2), yPos - (size / 2), size, size);
            square.setFill(nodeColor);
            square.setStroke(Color.BLACK);
            square.setStrokeWidth(1.5);

            // Use the "Pixel Operator" font and set color to match retro style
            Text label = new Text(stationName);
            label.setFont(Font.font("Pixel Operator", 14));
            label.setFill(Color.web("#f2f2ff")); // Light retro text color

            double offset = 10;
            switch (position) {
                case TOP -> {
                    label.setX(xPos - (label.getLayoutBounds().getWidth() / 2));
                    label.setY(yPos - offset);
                }
                case BOTTOM -> {
                    label.setX(xPos - (label.getLayoutBounds().getWidth() / 2));
                    label.setY(yPos + offset + 8);
                }
                case LEFT -> {
                    label.setX(xPos - label.getLayoutBounds().getWidth() - offset);
                    label.setY(yPos + 4);
                }
                case RIGHT -> {
                    label.setX(xPos + offset);
                    label.setY(yPos + 4);
                }
            }

            nodesPane.getChildren().addAll(square, label);
        }

        public void createConnection(String fromStation, String toStation, Color trackColor) {
            Point2D start = stationPositions.get(fromStation);
            Point2D end = stationPositions.get(toStation);

            if (start == null || end == null) return;

            Line line = new Line(start.getX(), start.getY(), end.getX(), end.getY());
            line.setStroke(trackColor);
            line.setStrokeWidth(3);

            tracksPane.getChildren().add(line);
        }
    }
}