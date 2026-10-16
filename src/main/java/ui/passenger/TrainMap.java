package ui.passenger;

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
 - Map tab: draws the stations as retro squares and the tracks as lines.
 */
public class TrainMap extends Tab {

    // Colours
    private static final Color GREEN = Color.web("#00ff88");
    private static final Color RED = Color.web("#ff334b");
    private static final Color BLUE = Color.web("#00e5ff");
    private static final Color GRAY = Color.web("#8c8cd9");

    /** Initializes the train map tab and sets up its UI layout. */
    public TrainMap() {
        super("Train Map");
        setClosable(false);

        Label title = new Label("Network Map");
        title.setStyle("-fx-font-family: 'Pixel Operator'; -fx-font-size: 20px; -fx-text-fill: #00e5ff;");

        Pane mapPane = createMapPane();

        VBox layout = new VBox(20, title, mapPane);
        layout.setPadding(new Insets(20));

        setContent(layout);
    }

    /** Builds and populates the map pane using mock data. */
    private Pane createMapPane() {
        TrainMapData mapData = new TrainMapData();
        NetworkMapPane mapPane = new NetworkMapPane();

        for (MapNode node : mapData.getNodes()) {
            mapPane.createNode(node.name(), node.x(), node.y(), node.color(), node.labelPosition());
        }

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
        /** Creates a default map node with gray color and a top label position. */
        public MapNode(String name, double x, double y) {
            this(name, x, y, GRAY, LabelPosition.TOP);
        }
    }

    private record MapConnection(String fromStation, String toStation, Color color) {
        /** Creates a default map connection with gray color. */
        public MapConnection(String fromStation, String toStation) {
            this(fromStation, toStation, GRAY);
        }
    }

    private static class TrainMapData {
        private final List<MapNode> nodes = new ArrayList<>();
        private final List<MapConnection> connections = new ArrayList<>();

        /** Populates the initial static mock data for stations and routes. */
        public TrainMapData() {
            addNode("Church Gate", 100, 100, BLUE, LabelPosition.TOP);
            addNode("CSMT", 400, 100, GREEN, LabelPosition.TOP);
            addNode("Panvel", 400, 400, RED, LabelPosition.BOTTOM);
            addNode("Bandra", 100, 400, BLUE, LabelPosition.BOTTOM);

            addConnection("Church Gate", "CSMT", BLUE);
            addConnection("CSMT", "Panvel", GREEN);
            addConnection("Panvel", "Bandra", RED);
            addConnection("Bandra", "Church Gate", BLUE);
        }

        /** Adds a station node to the dataset. */
        private void addNode(String name, double x, double y, Color color, LabelPosition position) {
            nodes.add(new MapNode(name, x, y, color, position));
        }

        /** Adds a track connection between two stations to the dataset. */
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

        /** Initializes the map pane layers ensuring tracks render beneath nodes. */
        public NetworkMapPane() {
            getChildren().addAll(tracksPane, nodesPane);
        }

        /** Renders an individual station square and its positioned text label. */
        public void createNode(String stationName, double xPos, double yPos, Color nodeColor, LabelPosition position) {
            stationPositions.put(stationName, new Point2D(xPos, yPos));

            double size = 12;
            Rectangle square = new Rectangle(xPos - (size / 2), yPos - (size / 2), size, size);
            square.setFill(nodeColor);
            square.setStroke(Color.BLACK);
            square.setStrokeWidth(1.5);

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

        /** Draws a track line between two existing station positions. */
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