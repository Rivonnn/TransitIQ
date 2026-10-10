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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Map tab: draws the stations as dots and the tracks as lines.
 * The layout is hardcoded on purpose. Where a station appears on screen is a
 * display decision, so it lives here and not on the Station model.
 * NOTES:
 *  - All the station names along with their positioning is not done yet.
 *    For now 4 stations and 4 connections are made just to display a base for the map.
 */
public class TrainMap extends Tab {

    /**
     * Builds the tab: a title above the map pane.
     */
    public TrainMap() {
        super("Train Map");
        setClosable(false);

        Label title = new Label("Network Map");

        Pane mapPane = createMapPane();

        VBox layout = new VBox(20, title, mapPane);
        layout.setPadding(new Insets(20));

        setContent(layout);
    }

    /**
     * Builds the hardcoded map data, then draws it onto a new pane.
     * Nodes are drawn before connections: createConnection looks up each station's
     * position by name and silently skips a line whose end isn't registered yet.
     * @return the pane with every node and connection drawn
     */
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

    // Where a station's name sits relative to its dot.
    public enum LabelPosition {
        TOP, BOTTOM, LEFT, RIGHT
    }

    /**
     * One station on the map.
     * @param name station name
     * @param x horizontal position on the pane
     * @param y vertical position on the pane
     * @param color color of the dot
     * @param labelPosition where the name sits around the dot
     */
    private record MapNode(String name, double x, double y, Color color, LabelPosition labelPosition) {
        // the dot is CRIMSON and the name sits above it (TOP).
        public MapNode(String name, double x, double y) {
            this(name, x, y, Color.CRIMSON, LabelPosition.TOP);
        }
    }

    /**
     * One track between two stations.
     * @param fromStation name of the station where the line starts
     * @param toStation name of the station where the line ends
     * @param color color of the line
     */
    private record MapConnection(String fromStation, String toStation, Color color) {
        public MapConnection(String fromStation, String toStation) {
            this(fromStation, toStation, Color.GRAY);
        }
    }

    /**
     * Holds the hardcoded list of nodes and connections to draw.
     * Placeholder data for now; replace with real station names later.
     */
    private static class TrainMapData {
        private final List<MapNode> nodes = new ArrayList<>();
        private final List<MapConnection> connections = new ArrayList<>();

        /** Adding nodes and connections when constructor runs. */
        public TrainMapData() {

            addNode("Church Gate", 0, 0, Color.CORAL, LabelPosition.TOP);
            addNode("CSMT", 1200, 0, Color.AQUAMARINE, LabelPosition.TOP);
            addNode("Panvel", 1200, 500, Color.GREEN, LabelPosition.BOTTOM);
            addNode("Bandra", 0, 500, Color.CRIMSON, LabelPosition.BOTTOM);

            addConnection("Church Gate", "CSMT", Color.CORAL);
            addConnection("CSMT", "Panvel", Color.AQUAMARINE);
            addConnection("Panvel", "Bandra", Color.GREEN);
            addConnection("Bandra", "Church Gate", Color.CRIMSON);
        }

        /** Adds a MapNode with the given values to the list. */
        private void addNode(String name, double x, double y, Color color, LabelPosition position) {
            nodes.add(new MapNode(name, x, y, color, position));
        }

        /** Adds a MapConnection with the given values to the list. */
        private void addConnection(String from, String to, Color color) {
            connections.add(new MapConnection(from, to, color));
        }

        // Getters used by createMapPane to read the data.
        public List<MapNode> getNodes() { return nodes; }
        public List<MapConnection> getConnections() { return connections; }
    }

    /**
     * A Pane that draws the map: stations (a dot plus a name) and tracks (lines).
     * Children of a Pane draw in the order they were added, with later ones on top.
     * That's why the tracks live in their own layer, added first, so the station
     * dots always sit above the lines.
     */
    private static class NetworkMapPane extends Pane {

        // Two layers inside this pane: lines at the back, stations in front.
        private final Pane tracksPane = new Pane();
        private final Pane nodesPane = new Pane();

        // Remembers each station's position by name. createConnection uses it to find
        // the two ends of a line, which is why every node must be created before the
        // connections that use it.
        private final Map<String, Point2D> stationPositions = new HashMap<>();

        /** Adds the two layers in drawing order: tracks first (back), nodes second (front). */
        public NetworkMapPane() {
            getChildren().addAll(tracksPane, nodesPane);
        }

        /**
         * Draws one station: a circle at (xPos, yPos) with its name next to it.
         * Also records the position under the station's name for createConnection.
         * @param stationName the name shown, and the key used to look the position up later
         * @param xPos horizontal position of the dot's center
         * @param yPos vertical position of the dot's center
         * @param nodeColor fill color of the dot
         * @param position where the name sits relative to the dot
         */
        public void createNode(String stationName, double xPos, double yPos, Color nodeColor, LabelPosition position) {
            stationPositions.put(stationName, new Point2D(xPos, yPos));

            // The dot: radius 5, with a black outline so it stands out against the line.
            Circle circle = new Circle(xPos, yPos, 5, nodeColor);
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(1.5);

            Text label = new Text(stationName);
            label.setFont(Font.font(10));

            /* Place the name relative to the dot. A Text's Y is its baseline (the line the
             * letters sit on), not its top, which is why BOTTOM adds 8 and LEFT/RIGHT add 3.
             * Those numbers are tuned by eye for the 10pt font.
             * getLayoutBounds().getWidth() is the text's width, used to centre the name above
             * or below the dot, or to push it fully to the left. The offset stops the text
             * from covering the circle.
             */
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

        /**
         * Draws one track as a straight line between two stations.
         * Does nothing if either station hasn't been created yet, so a name that doesn't
         * match silently leaves the line out instead of crashing.
         * @param fromStation name of the station where the line starts
         * @param toStation name of the station where the line ends
         * @param trackColor color of the line
         */
        public void createConnection(String fromStation, String toStation, Color trackColor) {
            Point2D start = stationPositions.get(fromStation);
            Point2D end = stationPositions.get(toStation);

            // An unknown name gives null here, skip the line.
            if (start == null || end == null) return;

            Line line = new Line(start.getX(), start.getY(), end.getX(), end.getY());
            line.setStroke(trackColor);
            line.setStrokeWidth(3);

            tracksPane.getChildren().add(line);
        }
    }
}