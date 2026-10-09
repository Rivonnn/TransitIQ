package ui.Passenger;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import service.PassengerService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainMap extends Tab {

    // --- Heart of the Map system ---
    // Sets up the main tab layout and attaches the rendered map canvas
    public TrainMap(PassengerService service) {
        super("Train Map");
        setClosable(false);

        Label title = new Label("Network Map");

        Pane mapPane = createMapPane();

        VBox layout = new VBox(20, title, mapPane);
        layout.setPadding(new Insets(20));

        setContent(layout);
    }

    // Instantiates map data repository and delegates drawing nodes and connections to NetworkMapPane
    private Pane createMapPane() {
        TrainMapData mapData = new TrainMapData();
        NetworkMapPane mapPane = new NetworkMapPane();

        // 1. Render nodes using position and label placement metadata
        for (MapNode node : mapData.getNodes()) {
            mapPane.createNode(node.name(), node.x(), node.y(), node.color(), node.labelPosition());
        }

        // 2. Render track connections using track color metadata
        for (MapConnection conn : mapData.getConnections()) {
            mapPane.createConnection(conn.fromStation(), conn.toStation(), conn.color());
        }

        // Set fixed preferred size to fit inside tab view comfortably
        mapPane.setPrefSize(600, 500);

        return mapPane;
    }

    // --- INNER DATA MODELS ---
    // Records and enum defining station coordinates, connection endpoints, styling, and label positioning
    public enum LabelPosition {
        TOP, BOTTOM, LEFT, RIGHT
    }

    private record MapNode(String name, double x, double y, Color color, LabelPosition labelPosition) {
        public MapNode(String name, double x, double y, Color color) {
            this(name, x, y, color, LabelPosition.TOP);
        }
    }

    private record MapConnection(String fromStation, String toStation, Color color) {
        public MapConnection(String fromStation, String toStation) {
            this(fromStation, toStation, Color.GRAY);
        }
    }

    // --- INNER DATA REPOSITORY ---
    // Stores all station nodes and track connections to be drawn on the map
    private static class TrainMapData {
        private final List<MapNode> nodes = new ArrayList<>();
        private final List<MapConnection> connections = new ArrayList<>();

        public TrainMapData() {
            // Add initial station list and track connections here
            addNode("Not Church Gate", 0, 0, Color.CORAL, LabelPosition.TOP);
            addNode("Not CSMT", 1200, 0, Color.AQUAMARINE, LabelPosition.TOP);
            addNode("Not Here", 1200, 500, Color.GREEN, LabelPosition.BOTTOM);
            addNode("Not Bandra", 0, 500, Color.CRIMSON, LabelPosition.BOTTOM);

            addConnection("Not Church Gate", "Not CSMT", Color.CORAL);
            addConnection("Not CSMT", "Not Here", Color.AQUAMARINE);
            addConnection("Not Here", "Not Bandra", Color.GREEN);
            addConnection("Not Bandra", "Not Church Gate", Color.CRIMSON);
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

    // --- INNER CANVAS PANE ---
    // Handles JavaFX shape rendering for station circles, aligned text labels, and connection lines
    private static class NetworkMapPane extends Pane {
        private final Pane tracksPane = new Pane();
        private final Pane nodesPane = new Pane();
        private final Map<String, Point2D> stationPositions = new HashMap<>();

        public NetworkMapPane() {
            // Guarantee lines stay behind nodes
            getChildren().addAll(tracksPane, nodesPane);
        }

        // Helper to construct station node graphics (Circle + Text) and map their screen coordinates
        public void createNode(String stationName, double xPos, double yPos, Color nodeColor, LabelPosition position) {
            stationPositions.put(stationName, new Point2D(xPos, yPos));

            Circle circle = new Circle(xPos, yPos, 5, nodeColor);
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(1.5);

            Text label = new Text(stationName);
            label.setFont(Font.font(10));

            // Position label relative to station dot
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
                    label.setY(yPos + 3);
                }
                case RIGHT -> {
                    label.setX(xPos + offset);
                    label.setY(yPos + 3);
                }
            }

            nodesPane.getChildren().addAll(circle, label);
        }

        // Helper to construct connecting line graphics between stored station coordinates
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