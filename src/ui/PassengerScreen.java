package ui;

import exceptions.NoRouteFoundException;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import model.Station;
import model.Train;
import service.PassengerService;

import java.util.List;
import java.util.stream.Collectors;

public class PassengerScreen {

    private final BorderPane root;

    public PassengerScreen(PassengerService service, Runnable onBack) {
        // Dropdowns show station names instead of the object's default text.
        StringConverter<Station> nameConverter = new StringConverter<Station>() {
            @Override public String toString(Station s) { return s == null ? "" : s.getName(); }
            @Override public Station fromString(String text) { return null; }
        };

        ComboBox<Station> source = new ComboBox<>();
        ComboBox<Station> destination = new ComboBox<>();
        for (ComboBox<Station> box : List.of(source, destination)) {
            box.setConverter(nameConverter);
            box.getItems().addAll(service.getStations());
        }
        source.setPromptText("Select source");
        destination.setPromptText("Select destination");

        ComboBox<String> trainType = new ComboBox<>();
        trainType.getItems().addAll("Local", "Express");
        trainType.setValue("Local");

        Button search = new Button("Search");
        Label routeLabel = new Label();
        routeLabel.setWrapText(true);
        Label fareLabel = new Label();
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        search.setOnAction(e -> {
            routeLabel.setText("");
            fareLabel.setText("");
            errorLabel.setText("");

            Station src = source.getValue();
            Station dst = destination.getValue();
            if (src == null || dst == null) {
                errorLabel.setText("Pick both stations.");
                return;
            }
            if (src.equals(dst)) {
                errorLabel.setText("Source and destination are the same.");
                return;
            }

            try {
                List<Station> route = service.findRoute(src, dst);
                String path = route.stream().map(Station::getName)
                        .collect(Collectors.joining(" → "));
                routeLabel.setText("Route: " + path + " (" + (route.size() - 1) + " stops)");

                Train train = service.findTrainByType(trainType.getValue());
                if (train == null) {
                    fareLabel.setText("No train of that type available.");
                } else {
                    double fare = service.calculateFare(route, train);
                    fareLabel.setText("Fare: ₹" + String.format("%.2f", fare));
                }
            } catch (NoRouteFoundException ex) {
                errorLabel.setText(ex.getMessage());
            }
        });

        VBox sourceWidget = new VBox(
                new Label("From"), source
        );

        VBox destinationWidget = new VBox(
                new Label("To"), destination
        );

        VBox trainTypeWidget = new VBox(
                new Label("Train Type"), trainType
        );

        HBox fromTo = new HBox(4,
                sourceWidget,
                destinationWidget
        );

        VBox form = new VBox(10,
                fromTo,
                trainTypeWidget,
                search, routeLabel, fareLabel, errorLabel);
        form.setPadding(new Insets(20));

        Tab routeTab = new Tab("Route & Fare", form);
        routeTab.setClosable(false);
        TabPane tabs = new TabPane(routeTab);   // Live Status tab goes here later

        Button back = new Button("Back");
        back.setOnAction(e -> onBack.run());
        HBox top = new HBox(back);
        top.setPadding(new Insets(10));

        root = new BorderPane(tabs);
        root.setTop(top);
    }

    public Parent getRoot() {
        return root;
    }
}