package ui.passenger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/** - Map tab: draws the stations as retro squares and the tracks as lines. */
public final class TrainMap extends Tab {

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
    title.setStyle(
        "-fx-font-family: 'Pixel Operator'; -fx-font-size: 20px; -fx-text-fill: #00e5ff;");

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
    TOP,
    BOTTOM,
    LEFT,
    RIGHT
  }

  private record MapNode(
      String name, double x, double y, Color color, LabelPosition labelPosition) {
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
      // ===== Western Line Nodes =====
      // === Churchgate to Vasai Road ===
      addNode("Vasai Road", 100, 0, BLUE, LabelPosition.LEFT);
      addNode("Naigaon", 100, 17, BLUE, LabelPosition.LEFT);
      addNode("Bhayandar", 100, 35, BLUE, LabelPosition.LEFT);
      addNode("Mira Road", 100, 52, BLUE, LabelPosition.LEFT);
      addNode("Dahisar", 100, 69, BLUE, LabelPosition.LEFT);
      addNode("Borivali", 100, 86, BLUE, LabelPosition.LEFT);
      addNode("Kandivali", 100, 103, BLUE, LabelPosition.LEFT);
      addNode("Malad", 100, 120, BLUE, LabelPosition.LEFT);
      addNode("Goregaon", 100, 138, BLUE, LabelPosition.LEFT);
      addNode("Ram Mandir", 100, 155, BLUE, LabelPosition.LEFT);
      addNode("Jogeshwari", 100, 172, BLUE, LabelPosition.LEFT);
      addNode("Andheri", 100, 189, BLUE, LabelPosition.LEFT);
      addNode("Vile Parle", 100, 207, BLUE, LabelPosition.LEFT);
      addNode("Santacruz", 100, 224, BLUE, LabelPosition.LEFT);
      addNode("Khar Road", 100, 241, BLUE, LabelPosition.LEFT);
      addNode("Bandra", 100, 258, BLUE, LabelPosition.LEFT);
      addNode("Mahim Junction", 100, 275, BLUE, LabelPosition.LEFT);
      addNode("Matunga Road", 100, 292, BLUE, LabelPosition.LEFT);
      addNode("Dadar", 100, 310, BLUE, LabelPosition.LEFT);
      addNode("Prabhadevi", 100, 327, BLUE, LabelPosition.LEFT);
      addNode("Lower Parel", 100, 344, BLUE, LabelPosition.LEFT);
      addNode("Mahalaxmi", 100, 361, BLUE, LabelPosition.LEFT);
      addNode("Mumbai Central", 100, 378, BLUE, LabelPosition.LEFT);
      addNode("Grant Road", 100, 396, BLUE, LabelPosition.LEFT);
      addNode("Charni Road", 100, 413, BLUE, LabelPosition.LEFT);
      addNode("Marine Lines", 100, 430, BLUE, LabelPosition.LEFT);
      addNode("Churchgate", 100, 450, BLUE, LabelPosition.BOTTOM);

      // ===== Western Line Connections =====
      // === Churchgate to Vasai Road ===
      addConnection("Vasai Road", "Naigaon", BLUE);
      addConnection("Naigaon", "Bhayandar", BLUE);
      addConnection("Bhayandar", "Mira Road", BLUE);
      addConnection("Mira Road", "Dahisar", BLUE);
      addConnection("Dahisar", "Borivali", BLUE);
      addConnection("Borivali", "Kandivali", BLUE);
      addConnection("Kandivali", "Malad", BLUE);
      addConnection("Malad", "Goregaon", BLUE);
      addConnection("Goregaon", "Ram Mandir", BLUE);
      addConnection("Ram Mandir", "Jogeshwari", BLUE);
      addConnection("Jogeshwari", "Andheri", BLUE);
      addConnection("Andheri", "Vile Parle", BLUE);
      addConnection("Vile Parle", "Santacruz", BLUE);
      addConnection("Santacruz", "Khar Road", BLUE);
      addConnection("Khar Road", "Bandra", BLUE);
      addConnection("Bandra", "Mahim Junction", BLUE);
      addConnection("Mahim Junction", "Matunga Road", BLUE);
      addConnection("Matunga Road", "Dadar", BLUE);
      addConnection("Dadar", "Prabhadevi", BLUE);
      addConnection("Prabhadevi", "Lower Parel", BLUE);
      addConnection("Lower Parel", "Mahalaxmi", BLUE);
      addConnection("Mahalaxmi", "Mumbai Central", BLUE);
      addConnection("Mumbai Central", "Grant Road", BLUE);
      addConnection("Grant Road", "Charni Road", BLUE);
      addConnection("Charni Road", "Marine Lines", BLUE);
      addConnection("Marine Lines", "Churchgate", BLUE);

      // ====== Harbour Line Nodes ======
      // Waypoints (no square, no label). H = Harbour track, C = Central track
      // H1 -> H2 is Goregaon -> Mahim
      // H3 -> H4 is King's Circle -> Vadala chamfer
      // H5 -> H6 is CSMT -> Sandhurst Rd. (Harbour part)
      // C1 -> C2 is CSMT -> Sandhurst Rd. (Central part)
      addNode("~H1", 108, 138, GRAY, LabelPosition.TOP);
      addNode("~H2", 108, 275, GRAY, LabelPosition.TOP);
      addNode("~H3", 150, 309, GRAY, LabelPosition.TOP);
      addNode("~H4", 165, 324, GRAY, LabelPosition.TOP);
      addNode("~H5", 203, 450, GRAY, LabelPosition.TOP);
      addNode("~H6", 203, 410, GRAY, LabelPosition.TOP);
      addNode("~C1", 197, 450, GRAY, LabelPosition.TOP);
      addNode("~C2", 197, 410, GRAY, LabelPosition.TOP);

      // CSMT -> Kurla (CSMT to Sandhurst Rd. is shared between Harbour and Central)
      addNode("CSMT", 200, 450, GREEN, LabelPosition.BOTTOM);
      addNode("Masjid", 200, 430, GREEN, LabelPosition.RIGHT);
      addNode("Sandhurst Rd.", 200, 410, GREEN, LabelPosition.RIGHT);
      addNode("Dockyard Rd.", 218, 390, GREEN, LabelPosition.RIGHT);
      addNode("Reay Rd.", 234, 370, GREEN, LabelPosition.RIGHT);
      addNode("Cotton Green", 250, 352, GREEN, LabelPosition.RIGHT);
      addNode("Sewri", 250, 338, GREEN, LabelPosition.RIGHT);
      addNode("Vadala", 250, 324, GREEN, LabelPosition.RIGHT);

      // King's Circle between Vadala -> Mahim
      addNode("King's Circle", 150, 275, GREEN, LabelPosition.TOP);

      addNode("GTB Nagar", 250, 310, GREEN, LabelPosition.RIGHT);
      addNode("Chunabhatti", 250, 296, GREEN, LabelPosition.RIGHT);
      addNode("Kurla", 250, 282, GREEN, LabelPosition.TOP);

      // Tilak Nagar -> Vashi
      addNode("Tilak Nagar", 316, 282, GREEN, LabelPosition.TOP);
      addNode("Chembur", 352, 282, GREEN, LabelPosition.BOTTOM);
      addNode("Govandi", 378, 282, GREEN, LabelPosition.TOP);
      addNode("Mankhurd", 404, 282, GREEN, LabelPosition.BOTTOM);
      addNode("Vashi", 450, 282, GREEN, LabelPosition.TOP);

      // Sanpada -> Panvel
      addNode("Sanpada", 462, 296, GREEN, LabelPosition.RIGHT);
      addNode("Juinagar", 474, 310, GREEN, LabelPosition.LEFT);
      addNode("Nerul", 486, 324, GREEN, LabelPosition.LEFT);
      addNode("Seawood Darave", 498, 338, GREEN, LabelPosition.LEFT);
      addNode("Belapur", 510, 352, GREEN, LabelPosition.LEFT);
      addNode("Kharghar", 522, 366, GREEN, LabelPosition.LEFT);
      addNode("Manasrovar", 534, 380, GREEN, LabelPosition.LEFT);
      addNode("Khandeshwar", 546, 394, GREEN, LabelPosition.LEFT);
      addNode("Panvel", 580, 394, GREEN, LabelPosition.BOTTOM);

      // ===== Harbour Line Connections =====
      // CSMT -> Sandhurst Rd. (Central part)
      addConnection("~C1", "~C2", RED);

      // Goregaon -> Mahim
      addConnection("Goregaon", "~H1", GREEN);
      addConnection("~H1", "~H2", GREEN);
      addConnection("~H2", "Mahim Jn", GREEN);

      // Mahim -> Vadala via King's Circle
      addConnection("~H2", "King's Circle", GREEN);
      addConnection("King's Circle", "~H3", GREEN);
      addConnection("~H3", "~H4", GREEN);
      addConnection("~H4", "Vadala", GREEN);

      // CSMT -> Kurla
      addConnection("~H5", "~H6", GREEN);
      addConnection("~H6", "Dockyard Rd.", GREEN);
      addConnection("Dockyard Rd.", "Reay Rd.", GREEN);
      addConnection("Reay Rd.", "Cotton Green", GREEN);
      addConnection("Cotton Green", "Sewri", GREEN);
      addConnection("Sewri", "Vadala", GREEN);
      addConnection("Vadala", "GTB Nagar", GREEN);
      addConnection("GTB Nagar", "Chunabhatti", GREEN);
      addConnection("Chunabhatti", "Kurla", GREEN);

      // Kurla -> Panvel
      addConnection("Kurla", "Tilak Nagar", GREEN);
      addConnection("Tilak Nagar", "Chembur", GREEN);
      addConnection("Chembur", "Govandi", GREEN);
      addConnection("Govandi", "Mankhurd", GREEN);
      addConnection("Mankhurd", "Vashi", GREEN);
      addConnection("Vashi", "Sanpada", GREEN);
      addConnection("Sanpada", "Juinagar", GREEN);
      addConnection("Juinagar", "Nerul", GREEN);
      addConnection("Nerul", "Seawood Darave", GREEN);
      addConnection("Seawood Darave", "Belapur", GREEN);
      addConnection("Belapur", "Kharghar", GREEN);
      addConnection("Kharghar", "Manasrovar", GREEN);
      addConnection("Manasrovar", "Khandeshwar", GREEN);
      addConnection("Khandeshwar", "Panvel", GREEN);

      // ===== Central Line Nodes =====
      // Chamfers for, Sandhurst Rd. -> Byculla and Parel -> Sion
      addNode(
          "~C3",
          185,
          398,
          GRAY,
          LabelPosition.TOP); // chamfer: red leaves Sandhurst, heads up the left side
      addNode(
          "~C4",
          185,
          330,
          GRAY,
          LabelPosition.TOP); // start of 45° diagonal to Sion (crosses Mahim branch at y=324)

      // Byculla -> Parel
      addNode("Byculla", 185, 386, RED, LabelPosition.LEFT);
      addNode("Chinchpokli", 185, 370, RED, LabelPosition.LEFT);
      addNode("Currey Rd.", 185, 356, RED, LabelPosition.LEFT);
      addNode("Parel", 185, 342, RED, LabelPosition.LEFT);

      // Dadar (C) -> Sion
      addNode("Dadar#C", 205, 310, RED, LabelPosition.LEFT);
      addNode("Matunga", 219, 296, RED, LabelPosition.LEFT);
      addNode("Sion", 233, 282, RED, LabelPosition.LEFT);

      // Kurla is shared between Central and Harbour
      // Vidyavihar -> Kalyan
      addNode("Vidyavihar", 290, 252, RED, LabelPosition.RIGHT);
      addNode("Ghatkopar", 306, 240, RED, LabelPosition.LEFT);
      addNode("Vikhroli", 322, 228, RED, LabelPosition.RIGHT);
      addNode("Kanjur Marg", 338, 216, RED, LabelPosition.LEFT);
      addNode("Bhandup", 354, 204, RED, LabelPosition.RIGHT);
      addNode("Nahur", 370, 192, RED, LabelPosition.LEFT);
      addNode("Mulund", 386, 180, RED, LabelPosition.RIGHT);
      addNode("Thane", 402, 168, RED, LabelPosition.LEFT);
      addNode("Kalwa", 418, 156, RED, LabelPosition.RIGHT);
      addNode("Mumbra", 434, 144, RED, LabelPosition.LEFT);
      addNode("Diva", 450, 132, RED, LabelPosition.RIGHT);
      addNode("Kopar", 466, 120, RED, LabelPosition.LEFT);
      addNode("Dombivli", 482, 108, RED, LabelPosition.RIGHT);
      addNode("Thakurli", 498, 96, RED, LabelPosition.LEFT);
      addNode("Kalyan", 514, 84, RED, LabelPosition.TOP);

      // Kalya -> Kasara
      addNode("Shahad", 542, 70, RED, LabelPosition.TOP);
      addNode("Ambivli", 572, 63, RED, LabelPosition.BOTTOM);
      addNode("Titwala", 602, 56, RED, LabelPosition.TOP);
      addNode("Khadavli", 632, 49, RED, LabelPosition.BOTTOM);
      addNode("Vasind", 662, 42, RED, LabelPosition.TOP);
      addNode("Asangaon", 692, 37, RED, LabelPosition.BOTTOM);
      addNode("Atgaon", 722, 30, RED, LabelPosition.TOP);
      addNode("Khardi", 752, 24, RED, LabelPosition.BOTTOM);
      addNode("Umbermali", 782, 17, RED, LabelPosition.TOP);
      addNode("Kasara", 815, 11, RED, LabelPosition.RIGHT);

      // Kalyan -> Khopoli
      addNode("Vithalwadi", 545, 105, RED, LabelPosition.RIGHT);
      addNode("Ulhasnagar", 566, 126, RED, LabelPosition.RIGHT);
      addNode("Ambernath", 587, 147, RED, LabelPosition.RIGHT);
      addNode("Badlapur", 608, 168, RED, LabelPosition.RIGHT);
      addNode("Vangani", 629, 185, RED, LabelPosition.RIGHT);
      addNode("Shelu", 629, 203, RED, LabelPosition.RIGHT);
      addNode("Neral", 629, 223, RED, LabelPosition.RIGHT);
      addNode("Bhivpuri Rd.", 629, 243, RED, LabelPosition.RIGHT);
      addNode("Karjat", 629, 263, RED, LabelPosition.RIGHT);
      addNode("Palasdhari", 629, 283, RED, LabelPosition.RIGHT);
      addNode("Kelavali", 629, 303, RED, LabelPosition.RIGHT);
      addNode("Dolavli", 629, 323, RED, LabelPosition.RIGHT);
      addNode("Lowjee", 629, 343, RED, LabelPosition.RIGHT);
      addNode("Khopoli", 629, 363, RED, LabelPosition.RIGHT);

      // ===== Central Line Connections =====
      // Sandhurst Rd. -> Kurla
      addConnection("~C2", "~C3", RED);
      addConnection("~C3", "Byculla", RED);
      addConnection("Byculla", "Chinchpokli", RED);
      addConnection("Chinchpokli", "Currey Rd.", RED);
      addConnection("Currey Rd.", "Parel", RED);
      addConnection("Parel", "~C4", RED);
      addConnection("~C4", "Dadar#C", RED);
      addConnection("Dadar#C", "Matunga", RED);
      addConnection("Matunga", "Sion", RED);
      addConnection("Sion", "Kurla", RED);

      // Kurla -> Kalyan
      addConnection("Kurla", "Vidyavihar", RED);
      addConnection("Vidyavihar", "Ghatkopar", RED);
      addConnection("Ghatkopar", "Vikhroli", RED);
      addConnection("Vikhroli", "Kanjur Marg", RED);
      addConnection("Kanjur Marg", "Bhandup", RED);
      addConnection("Bhandup", "Nahur", RED);
      addConnection("Nahur", "Mulund", RED);
      addConnection("Mulund", "Thane", RED);
      addConnection("Thane", "Kalwa", RED);
      addConnection("Kalwa", "Mumbra", RED);
      addConnection("Mumbra", "Diva", RED);
      addConnection("Diva", "Kopar", RED);
      addConnection("Kopar", "Dombivli", RED);
      addConnection("Dombivli", "Thakurli", RED);
      addConnection("Thakurli", "Kalyan", RED);

      // Kalyan -> Kasara
      addConnection("Kalyan", "Shahad", RED);
      addConnection("Shahad", "Ambivli", RED);
      addConnection("Ambivli", "Titwala", RED);
      addConnection("Titwala", "Khadavli", RED);
      addConnection("Khadavli", "Vasind", RED);
      addConnection("Vasind", "Asangaon", RED);
      addConnection("Asangaon", "Atgaon", RED);
      addConnection("Atgaon", "Khardi", RED);
      addConnection("Khardi", "Umbermali", RED);
      addConnection("Umbermali", "Kasara", RED);

      // Kalyan -> Khopoli
      addConnection("Kalyan", "Vithalwadi", RED);
      addConnection("Vithalwadi", "Ulhasnagar", RED);
      addConnection("Ulhasnagar", "Ambernath", RED);
      addConnection("Ambernath", "Badlapur", RED);
      addConnection("Badlapur", "Vangani", RED);
      addConnection("Vangani", "Shelu", RED);
      addConnection("Shelu", "Neral", RED);
      addConnection("Neral", "Bhivpuri Rd.", RED);
      addConnection("Bhivpuri Rd.", "Karjat", RED);
      addConnection("Karjat", "Palasdhari", RED);
      addConnection("Palasdhari", "Kelavali", RED);
      addConnection("Kelavali", "Dolavli", RED);
      addConnection("Dolavli", "Lowjee", RED);
      addConnection("Lowjee", "Khopoli", RED);
    }

    /** Adds a station node to the dataset. */
    private void addNode(String name, double x, double y, Color color, LabelPosition position) {
      nodes.add(new MapNode(name, x, y, color, position));
    }

    /** Adds a track connection between two stations to the dataset. */
    private void addConnection(String from, String to, Color color) {
      connections.add(new MapConnection(from, to, color));
    }

    public List<MapNode> getNodes() {
      return nodes;
    }

    public List<MapConnection> getConnections() {
      return connections;
    }
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
    public void createNode(
        String stationName, double xPos, double yPos, Color nodeColor, LabelPosition position) {
      stationPositions.put(stationName, new Point2D(xPos, yPos));

      if (stationName.startsWith("~")) return;

      double size = 10;
      Rectangle square = new Rectangle(xPos - (size / 2), yPos - (size / 2), size, size);
      square.setFill(nodeColor);
      square.setStroke(Color.BLACK);
      square.setStrokeWidth(1.5);

      Text label = new Text(stationName.split("#")[0]);
      label.setFont(Font.font("Pixel Operator", 12));
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
      line.setStrokeLineCap(StrokeLineCap.ROUND);

      tracksPane.getChildren().add(line);
    }
  }
}
