package ui.passenger;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Bounds;
import javafx.geometry.BoundingBox;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.scene.control.Tooltip;
import java.util.*;

/** Schematic map of the operational Mumbai suburban railway corridors. */
public class TrainMap extends Tab {
    private static final double MIN_ZOOM = 0.05;
    private static final double MAX_ZOOM = 2.5;
    private static final double MAP_PADDING = 24;
    private static final double STATION_SPACING = 105;
    private static final double ROUTE_SPACING = 150;
    private static final Color WESTERN = Color.web("#00e5ff"), CENTRAL = Color.web("#ff3ea5"),
            HARBOUR = Color.web("#00ff88"), TRANS_HARBOUR = Color.web("#ffd166"),
            URAN = Color.web("#b388ff"), KASARA = Color.web("#ff8c42"), KHOPOLI = Color.web("#58a6ff");
    private static final List<Route> ROUTES = List.of(
            new Route("Western", WESTERN, "Churchgate|Marine Lines|Charni Road|Grant Road|Mumbai Central|Mahalaxmi|Lower Parel|Prabhadevi|Dadar|Matunga Road|Mahim|Bandra|Khar Road|Santacruz|Vile Parle|Andheri|Jogeshwari|Ram Mandir|Goregaon|Malad|Kandivali|Borivali|Dahisar|Mira Road|Bhayandar|Naigaon|Vasai Road|Nalasopara|Virar|Vaitarna|Saphale|Kelve Road|Palghar|Umroli|Boisar|Vangaon|Dahanu Road"),
            new Route("Central Main", CENTRAL, "CSMT|Masjid|Sandhurst Road|Byculla|Chinchpokli|Currey Road|Parel|Dadar|Matunga|Sion|Kurla|Vidyavihar|Ghatkopar|Vikhroli|Kanjur Marg|Bhandup|Nahur|Mulund|Thane|Kalwa|Mumbra|Diva|Kopar|Dombivli|Thakurli|Kalyan"),
            new Route("Kasara Branch", KASARA, "Kalyan|Shahad|Ambivli|Titwala|Khadavli|Vasind|Asangaon|Atgaon|Thansit|Khardi|Umbermali|Kasara"),
            new Route("Khopoli Branch", KHOPOLI, "Kalyan|Vithalwadi|Ulhasnagar|Ambarnath|Badlapur|Vangani|Shelu|Neral|Bhivpuri Road|Karjat|Palasdhari|Kelavli|Dolavli|Lowjee|Khopoli"),
            new Route("Harbour CSMT–Panvel", HARBOUR, "CSMT|Masjid|Sandhurst Road|Dockyard Road|Reay Road|Cotton Green|Sewri|Wadala Road|Guru Tegh Bahadur Nagar|Chunabhatti|Kurla|Tilak Nagar|Chembur|Govandi|Mankhurd|Vashi|Sanpada|Juinagar|Nerul|Seawoods-Darave|CBD Belapur|Kharghar|Mansarovar|Khandeshwar|Panvel"),
            new Route("Harbour Wadala–Goregaon", Color.web("#7ee787"), "Wadala Road|King's Circle|Mahim|Bandra|Khar Road|Santacruz|Vile Parle|Andheri|Jogeshwari|Ram Mandir|Goregaon"),
            new Route("Trans-Harbour", TRANS_HARBOUR, "Thane|Digha|Airoli|Rabale|Ghansoli|Kopar Khairane|Turbhe|Sanpada|Vashi|Juinagar|Nerul|Seawoods-Darave|CBD Belapur|Kharghar|Mansarovar|Khandeshwar|Panvel"),
            // Sagar Sangam appears on one 2026 map, but current Uran timetables omit it as a stop.
            new Route("Uran / Port Line (Nerul)", URAN, "Nerul|Seawoods-Darave|Targhar|Bamandongri|Kharkopar|Gavhan|Shematikhar|Ranjanpada|Nhava Sheva|Dronagiri|Uran"),
            new Route("Uran / Port Line (Belapur)", Color.web("#d6b8ff"), "CBD Belapur|Targhar|Bamandongri|Kharkopar|Gavhan|Shematikhar|Ranjanpada|Nhava Sheva|Dronagiri|Uran"));

    public TrainMap() {
        super("Train Map"); setClosable(false);
        Label title = new Label("MUMBAI SUBURBAN RAILWAY"); title.getStyleClass().add("heading");
        Label note = new Label("Operational station sequence • schematic, not to scale"); note.getStyleClass().add("map-caption");
        Pane drawing = buildMap();
        Group zoomGroup = new Group(drawing);
        Scale zoomTransform = new Scale(1, 1, 0, 0);
        zoomGroup.getTransforms().add(zoomTransform);
        Pane mapSurface = new Pane(zoomGroup);
        ScrollPane viewport = new ScrollPane(mapSurface); viewport.setPannable(true); viewport.setFitToHeight(false); viewport.setFitToWidth(false); viewport.getStyleClass().add("map-viewport");
        Button plus = new Button("Zoom +"), minus = new Button("Zoom −"), reset = new Button("Fit / Reset");
        boolean[] fitOnResize = {true};
        Runnable fit = () -> fitMap(viewport, mapSurface, zoomGroup, zoomTransform, drawing);
        plus.setOnAction(e -> { fitOnResize[0] = false; changeZoom(zoomGroup, mapSurface, zoomTransform, viewport, drawing, 1.2); });
        minus.setOnAction(e -> { fitOnResize[0] = false; changeZoom(zoomGroup, mapSurface, zoomTransform, viewport, drawing, 1 / 1.2); });
        reset.setOnAction(e -> { fitOnResize[0] = true; Platform.runLater(fit); });
        viewport.setOnMousePressed(e -> fitOnResize[0] = false);
        viewport.setOnScroll(e -> fitOnResize[0] = false);
        viewport.viewportBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            if (fitOnResize[0]) Platform.runLater(fit);
        });
        FlowPane legend = new FlowPane(12, 8); for (Route route: ROUTES) { Label item=new Label("■  "+route.name); item.setTextFill(route.color); legend.getChildren().add(item); }
        HBox controls=new HBox(8,plus,minus,reset); VBox box=new VBox(8,title,note,legend,controls,viewport); box.setPadding(new Insets(12)); VBox.setVgrow(viewport, javafx.scene.layout.Priority.ALWAYS); setContent(box);
    }

    private void changeZoom(Group map, Pane surface, Scale transform, ScrollPane viewport,
                            Pane drawing, double multiplier) {
        double scale = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, transform.getX() * multiplier));
        applyZoom(map, surface, transform, viewport, drawing, scale, true);
    }

    private void updateLabelVisibility(Pane drawing, double scale) {
        List<Text> candidates = new ArrayList<>();
        for (var child : drawing.getChildren()) {
            if (child instanceof Text label && label.getUserData() instanceof LabelTier tier) {
                label.setVisible(switch (tier) {
                    case OVERVIEW -> true;
                    case MAJOR -> scale >= 0.34;
                    case ALL_STATIONS -> scale >= 0.78;
                });
                if (label.isVisible()) candidates.add(label);
            }
        }
        candidates.sort(Comparator.comparingInt(label -> ((LabelTier) label.getUserData()).ordinal()));
        List<Bounds> occupied = new ArrayList<>();
        for (Text label : candidates) {
            Bounds local = label.getBoundsInParent();
            Bounds screen = new BoundingBox(local.getMinX() * scale, local.getMinY() * scale,
                    local.getWidth() * scale, local.getHeight() * scale);
            boolean overlaps = occupied.stream().anyMatch(bounds -> intersects(bounds, screen, 5));
            label.setVisible(!overlaps);
            if (!overlaps) occupied.add(screen);
        }
    }

    private void fitMap(ScrollPane viewport, Pane surface, Group map, Scale transform, Pane drawing) {
        Bounds content = getContentBounds(drawing);
        if (content.isEmpty()) return;
        double availableWidth = Math.max(1, viewport.getViewportBounds().getWidth() - MAP_PADDING * 2);
        double availableHeight = Math.max(1, viewport.getViewportBounds().getHeight() - MAP_PADDING * 2);
        double scale = Math.min(MAX_ZOOM,
                Math.min(availableWidth / content.getWidth(), availableHeight / content.getHeight()));
        applyZoom(map, surface, transform, viewport, drawing, scale, false);
    }

    private void applyZoom(Group map, Pane surface, Scale transform, ScrollPane viewport,
                           Pane drawing, double scale, boolean centerScroll) {
        Bounds content = getContentBounds(drawing);
        transform.setX(scale);
        transform.setY(scale);
        updateLabelVisibility(drawing, scale);
        double scaledWidth = content.getWidth() * scale;
        double scaledHeight = content.getHeight() * scale;
        double viewportWidth = viewport.getViewportBounds().getWidth();
        double viewportHeight = viewport.getViewportBounds().getHeight();
        double surfaceWidth = Math.max(viewportWidth, scaledWidth + MAP_PADDING * 2);
        double surfaceHeight = Math.max(viewportHeight, scaledHeight + MAP_PADDING * 2);
        map.setTranslateX((surfaceWidth - scaledWidth) / 2 - content.getMinX() * scale);
        map.setTranslateY((surfaceHeight - scaledHeight) / 2 - content.getMinY() * scale);
        surface.setMinSize(surfaceWidth, surfaceHeight);
        surface.setPrefSize(surfaceWidth, surfaceHeight);
        surface.setMaxSize(surfaceWidth, surfaceHeight);
        surface.resize(surfaceWidth, surfaceHeight);
        Platform.runLater(() -> {
            viewport.setHvalue(centerScroll ? 0.5 : 0);
            viewport.setVvalue(centerScroll ? 0.5 : 0);
        });
    }

    private Bounds getContentBounds(Pane drawing) {
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (var child : drawing.getChildren()) {
            Bounds bounds = child.getBoundsInParent();
            minX = Math.min(minX, bounds.getMinX());
            minY = Math.min(minY, bounds.getMinY());
            maxX = Math.max(maxX, bounds.getMaxX());
            maxY = Math.max(maxY, bounds.getMaxY());
        }
        return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
    }

    private boolean intersects(Bounds first, Bounds second, double gap) {
        return first.getMinX() < second.getMaxX() + gap && first.getMaxX() + gap > second.getMinX()
                && first.getMinY() < second.getMaxY() + gap && first.getMaxY() + gap > second.getMinY();
    }

    private Pane buildMap() {
        Map<String,Point2D> positions=new LinkedHashMap<>(); Map<String,Set<Color>> memberships=new HashMap<>();
        // Each corridor occupies its own track; shared stations reuse the same map identity.
        int row=0; for(Route route:ROUTES) { String[] names=route.stations.split("\\|"); for(int i=0;i<names.length;i++){ String name=names[i]; positions.putIfAbsent(name,new Point2D(100+i*STATION_SPACING,100+row*ROUTE_SPACING)); memberships.computeIfAbsent(name,k->new LinkedHashSet<>()).add(route.color); } row++; }
        positionCentralBranches(positions);
        positionHarbourBranch(positions);
        positionTransHarbourBranch(positions);
        positionUranBranches(positions);
        Pane pane=new Pane();
        pane.getStyleClass().add("network-map");
        Map<EdgeKey, List<Route>> edgeRoutes = new LinkedHashMap<>();
        for (Route route : ROUTES) {
            String[] names = route.stations.split("\\|");
            for (int i = 0; i < names.length - 1; i++) edgeRoutes.computeIfAbsent(new EdgeKey(names[i], names[i + 1]), key -> new ArrayList<>()).add(route);
        }
        for (var edge : edgeRoutes.entrySet()) {
            Point2D start = positions.get(edge.getKey().first), end = positions.get(edge.getKey().second);
            List<Route> routes = edge.getValue();
            for (int i = 0; i < routes.size(); i++) drawConnection(pane, start, end, routes.get(i).color, i - (routes.size() - 1) / 2.0);
        }
        int labelOrdinal = 0;
        for(var entry:positions.entrySet()){String name=entry.getKey(); Point2D p=entry.getValue(); Set<Color> lines=memberships.get(name); boolean interchange=lines.size()>1; Circle node=new Circle(p.getX(),p.getY(),interchange?9:6,Color.web("#14142b")); node.setStroke(lines.iterator().next()); node.setStrokeWidth(interchange?4:3); if(interchange) node.setFill(Color.web("#ff3ea5"));
            Tooltip.install(node, new Tooltip(name));
            Text label=new Text(name); label.setFont(Font.font("Pixel Operator",13)); label.getStyleClass().add("map-station-label"); label.setX(p.getX()+14); label.setY(p.getY() + (labelOrdinal++ % 2 == 0 ? -12 : 22));
            LabelTier tier = interchange || Set.of("Churchgate", "Dahanu Road", "CSMT", "Kalyan", "Kasara", "Khopoli", "Panvel", "Uran", "Wadala Road", "Thane", "Dadar", "Kurla", "Nerul", "CBD Belapur", "Vashi").contains(name)
                    ? LabelTier.OVERVIEW
                    : Set.of("Mumbai Central", "Bandra", "Mahim", "Andheri", "Borivali", "Vasai Road", "Virar", "Ghatkopar", "Dombivli", "Mankhurd", "Goregaon", "Turbhe", "Kharkopar").contains(name)
                    ? LabelTier.MAJOR : LabelTier.ALL_STATIONS;
            label.setVisible(tier != LabelTier.ALL_STATIONS);
            label.setUserData(tier);
            pane.getChildren().addAll(node,label);
            if(name.equals("Churchgate")||name.equals("Dahanu Road")||name.equals("CSMT")||name.equals("Kasara")||name.equals("Khopoli")||name.equals("Panvel")||name.equals("Uran")){ Rectangle mark=new Rectangle(p.getX()-12,p.getY()-12,24,24); mark.setFill(Color.TRANSPARENT); mark.setStroke(Color.web("#ffd166")); mark.setStrokeWidth(2); pane.getChildren().add(mark); }
        }
        return pane;
    }

    private void positionCentralBranches(Map<String, Point2D> positions) {
        Point2D kalyan = positions.get("Kalyan");
        placeRouteAfter(positions, "Kasara Branch", "Kalyan", kalyan.getY() + ROUTE_SPACING, 90);
        placeRouteAfter(positions, "Khopoli Branch", "Kalyan", kalyan.getY() + ROUTE_SPACING * 2, 90);
    }

    private void positionHarbourBranch(Map<String, Point2D> positions) {
        positions.put("Wadala Road", new Point2D(940, 400));
        positions.put("King's Circle", new Point2D(1045, 250));
    }

    private void positionTransHarbourBranch(Map<String, Point2D> positions) {
        positions.put("Digha", new Point2D(1990, 350));
        positions.put("Airoli", new Point2D(1890, 450));
        positions.put("Rabale", new Point2D(1790, 450));
        positions.put("Ghansoli", new Point2D(1690, 450));
        positions.put("Kopar Khairane", new Point2D(1590, 450));
        positions.put("Turbhe", new Point2D(1690, 600));
    }

    private void positionUranBranches(Map<String, Point2D> positions) {
        Point2D belapur = positions.get("CBD Belapur");
        double branchY = belapur.getY() + 220;
        positions.put("Targhar", new Point2D(belapur.getX(), branchY));
        placeRouteAfter(positions, "Uran / Port Line (Nerul)", "Targhar", branchY, 100);
        placeRouteAfter(positions, "Uran / Port Line (Belapur)", "Targhar", branchY, 100);
    }

    private void placeRouteAfter(Map<String, Point2D> positions, String routeName, String anchorName,
                                 double laneY, double stationSpacing) {
        Route route = ROUTES.stream().filter(candidate -> candidate.name.equals(routeName)).findFirst().orElseThrow();
        String[] stations = route.stations.split("\\|");
        int anchorIndex = Arrays.asList(stations).indexOf(anchorName);
        Point2D anchor = positions.get(anchorName);
        for (int i = anchorIndex + 1; i < stations.length; i++) {
            positions.put(stations[i], new Point2D(anchor.getX() + (i - anchorIndex) * stationSpacing, laneY));
        }
    }

    private void drawConnection(Pane pane, Point2D start, Point2D end, Color color, double parallelOffset) {
        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        double length = Math.max(1, Math.hypot(dx, dy));
        double offsetX = -dy / length * parallelOffset * 5;
        double offsetY = dx / length * parallelOffset * 5;
        double x1 = start.getX() + offsetX, y1 = start.getY() + offsetY;
        double x2 = end.getX() + offsetX, y2 = end.getY() + offsetY;
        double bendY = (y1 + y2) / 2;
        Polyline track = new Polyline(x1, y1, x1, bendY, x2, bendY, x2, y2);
        track.setStroke(color);
        track.setStrokeWidth(4);
        track.setFill(Color.TRANSPARENT);
        pane.getChildren().add(track);
    }

    private record EdgeKey(String first, String second) {
        private EdgeKey {
            if (first.compareTo(second) > 0) {
                String swap = first;
                first = second;
                second = swap;
            }
        }
    }
    private enum LabelTier { OVERVIEW, MAJOR, ALL_STATIONS }
    private record Route(String name, Color color, String stations) { }
}
