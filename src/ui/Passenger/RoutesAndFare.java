package ui.Passenger;

import exceptions.NoRouteFoundException;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import model.Station;
import model.Train;
import service.PassengerService;
import java.util.List;
import java.util.stream.Collectors;

/**
 * "Route & Fare" tab: pick two stations and a train type, press Search to see
 * the route and the fare. Input errors and "no route" messages show in red.
 */
public class RoutesAndFare extends Tab {

    /**
     * @param service provides the stations, route search, train lookup and fare calculation
     */
    public RoutesAndFare(PassengerService service) {
        super("Route & Fare"); // Creates a tab with the String mentioned.
        setClosable(false); // Makes the tab non-closable.

        // Tells the ComboBox to show a station's name instead of the object's default
        // text. fromString is never used because the boxes aren't editable, so it returns null.
        StringConverter<Station> nameConverter = new StringConverter<Station>() {
            @Override
            public String toString(Station s) {
                return s == null ? "" : s.getName();
            }

            @Override
            public Station fromString(String text) {
                return null;
            }
        };

        // ComboBoxes which display the list of Stations.
        ComboBox<Station> source = new ComboBox<>();
        ComboBox<Station> destination = new ComboBox<>();

        // Both station boxes get the same converter and the same list of stations.
        for (ComboBox<Station> box : List.of(source, destination)) {
            box.setConverter(nameConverter); // Convert into String.
            box.getItems().addAll(service.getStations()); // Add stations to box.
        }

        // Setting the promptText for the ComboBoxes.
        source.setPromptText("Select source");
        destination.setPromptText("Select destination");

        // ComboBox for the Train types
        ComboBox<String> trainType = new ComboBox<>();
        trainType.getItems().addAll("Local", "Express");
        trainType.setValue("Local"); // Default

        // Search button
        Button search = new Button("Search");

        // Label to display route
        Label routeLabel = new Label();
        routeLabel.setWrapText(true);

        // fareLabel: the fare, or "no train of that type available".
        // errorLabel: red, for input and routing errors.
        Label fareLabel = new Label();
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        // Runs on every click. First clear the previous search's results.
        search.setOnAction(e -> {
            routeLabel.setText("");
            fareLabel.setText("");
            errorLabel.setText("");

            // Get the source and destination values from the ComboBoxes
            Station src = source.getValue();
            Station dst = destination.getValue();

            // Both stations must be chosen.
            if (src == null || dst == null) {
                errorLabel.setText("Pick both stations.");
                return;
            }

            // Source and destination cannot be the same.
            if (src.equals(dst)) {
                errorLabel.setText("Source and destination are the same.");
                return;
            }

            // Find the route, show it as "A → B → C" with the number of stops, then
            // work out the fare from the first train of the chosen type.
            try {
                List<Station> route = service.findRoute(src, dst);
                String path = route.stream()
                        .map(Station::getName)
                        .collect(Collectors.joining(" → "));
                routeLabel.setText("Route: " + path + " (" + (route.size() - 1) + " stops)");

                // null means no train of this type exists, so there's no fare to calculate.
                Train train = service.findTrainByType(trainType.getValue());
                if (train == null) {
                    fareLabel.setText("No train of that type available.");
                } else {
                    double fare = service.calculateFare(route, train);
                    fareLabel.setText("Fare: ₹" + String.format("%.2f", fare));
                }
            }
            // Thrown by service.findRoute when no path connects the two stations.
            catch (NoRouteFoundException ex) {
                errorLabel.setText(ex.getMessage());
            }
        });

        // Layout: each label sits above its box; the two station boxes go side by side.
        VBox sourceWidget = new VBox(new Label("From"), source);
        VBox destinationWidget = new VBox(new Label("To"), destination);
        VBox trainTypeWidget = new VBox(new Label("Train Type"), trainType);

        HBox fromTo = new HBox(4, sourceWidget, destinationWidget);

        VBox form = new VBox(10,
                fromTo,
                trainTypeWidget,
                search,
                routeLabel,
                fareLabel,
                errorLabel
        );
        form.setPadding(new Insets(20));

        setContent(form);
    }
}