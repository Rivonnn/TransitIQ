package ui.passenger;

import exceptions.NoRouteFoundException;
import java.util.List;
import java.util.stream.Collectors;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import model.Station;
import model.Train;
import service.PassengerService;

/**
 * "Route & Fare" tab: pick two stations and a train type, press Search to see the route and the
 * fare. Input errors and "no route" messages show in red.
 */
public final class RoutesAndFare extends Tab {

  /**
   * @param service provides the stations, route search, train lookup and fare calculation
   */
  public RoutesAndFare(PassengerService service) {
    super("Route & Fare"); // Creates a tab with the String mentioned.
    setClosable(false); // Makes the tab non-closable.

    // Tells the ComboBox to show a station's name instead of the object's default
    // text. fromString is never used because the boxes aren't editable, so it returns null.
    StringConverter<Station> nameConverter =
        new StringConverter<>() {
          @Override
          public String toString(Station s) {
            return s == null ? "" : s.getName();
          }

          @Override
          public Station fromString(String text) {
            return null;
          }
        };

    Label title = new Label("Routes and Fare");
    title.setStyle(
        "-fx-font-family: 'Pixel Operator'; -fx-font-size: 20px; -fx-text-fill: #00e5ff;");

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

    // Give all three dropdowns the same width
    source.setPrefWidth(Double.MAX_VALUE);
    destination.setPrefWidth(Double.MAX_VALUE);
    trainType.setPrefWidth(Double.MAX_VALUE);

    // Search button
    Button search = new Button("Search");

    // routeLabel: the route. fareLabel: the fare, shown larger as the answer.
    // errorLabel: red, for input and routing errors.
    Label routeLabel = new Label();
    routeLabel.setWrapText(true);
    Label fareLabel = new Label();
    fareLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #00e5ff;");
    Label errorLabel = new Label();
    errorLabel.setStyle("-fx-text-fill: #ff5555;");
    errorLabel.setWrapText(true);

    // Runs on every click. First clear the previous search's results.
    search.setOnAction(
        _ -> {
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
            String path = route.stream().map(Station::getName).collect(Collectors.joining(" → "));
            routeLabel.setText("Route: " + path + " (" + (route.size() - 1) + " stops)");

            // null means no train of this type exists, so there's no fare to calculate.
            Train train = service.findTrainByType(trainType.getValue());
            if (train == null) {
              errorLabel.setText("No train of that type available.");
            } else if (!service.trainStopsAt(train, src) || !service.trainStopsAt(train, dst)) {
              errorLabel.setText(trainType.getValue() + " doesn't stop at both stations.");
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

    // Layout: the three inputs sit in one row of equal-width columns, each with a dim label above.
    GridPane inputs = new GridPane();
    inputs.setHgap(16);
    for (int i = 0; i < 3; i++) {
      ColumnConstraints col = new ColumnConstraints();
      col.setPercentWidth(100.0 / 3);
      inputs.getColumnConstraints().add(col);
    }
    inputs.add(fieldWithLabel("From", source), 0, 0);
    inputs.add(fieldWithLabel("To", destination), 1, 0);
    inputs.add(fieldWithLabel("Train Type", trainType), 2, 0);
    // Results get their own area under a divider. The minimum height stops the card from
    // jumping in size when a result appears.
    VBox results = new VBox(8, routeLabel, fareLabel, errorLabel);
    results.setMinHeight(90);

    // Everything sits in one bordered card, so it reads as a single panel.
    VBox card = new VBox(16, inputs, search, new Separator(), results);
    card.setPadding(new Insets(20));
    card.setStyle("-fx-background-color: #1b1c3a; -fx-border-color: #00e5ff; -fx-border-width: 2;");

    VBox form = new VBox(16, title, card);
    form.setPadding(new Insets(20));

    setContent(form);
  }

  /** Puts a dim label above a field. */
  private VBox fieldWithLabel(String text, Control field) {
    Label label = new Label(text);
    label.setStyle("-fx-text-fill: #9aa0c8;");
    return new VBox(4, label, field);
  }
}
