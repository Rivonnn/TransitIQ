package ui.admin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;
import simulation.SimulationClock;

public final class EventLog extends Tab {
  private static final String ALL_EVENTS = "All events";
  private static final String ADMIN_ACTIONS = "Admin actions";
  private static final String TRAIN_MOVEMENT = "Train movement";
  private static final String DELAY_EVENTS = "Delay events";
  private static final String SERVICE_CHANGES = "Service changes";

  private final SimulationClock clock;
  private final ListView<String> listView = new ListView<>();
  private final ChoiceBox<String> typeFilter = new ChoiceBox<>();
  private final TextField searchField = new TextField();
  private final Label result = new Label();

  public EventLog(SimulationClock clock) {
    super("Event Log");
    setClosable(false);
    this.clock = clock;

    listView.setPlaceholder(new Label("No matching events"));
    typeFilter
        .getItems()
        .setAll(ALL_EVENTS, ADMIN_ACTIONS, TRAIN_MOVEMENT, DELAY_EVENTS, SERVICE_CHANGES);
    typeFilter.setValue(ALL_EVENTS);
    typeFilter.setOnAction(_ -> refresh());
    searchField.setPromptText("Search event text");
    searchField.textProperty().addListener((_, _, _) -> refresh());

    Label title = new Label("Control Panel");
    title.setStyle(
        "-fx-font-family: 'Pixel Operator'; -fx-font-size: 20px; -fx-text-fill: #00e5ff;");
    Label heading = new Label("Recent events");
    Button export = new Button("Export filtered CSV");
    export.setOnAction(_ -> exportFilteredEvents());
    HBox filters = new HBox(8, typeFilter, searchField);
    HBox.setHgrow(searchField, Priority.ALWAYS);

    VBox layout = new VBox(10, title, heading, filters, listView, export, result);
    VBox.setVgrow(listView, Priority.ALWAYS);
    layout.setPadding(new Insets(20));
    setContent(layout);

    refresh();
    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), _ -> refresh()));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
  }

  /** Reapplies filters to a fresh log snapshot as the simulation adds events. */
  private void refresh() {
    List<String> matchingEvents = new ArrayList<>();
    String query = searchField.getText().trim().toLowerCase(Locale.ROOT);
    String selectedType = typeFilter.getValue();
    for (String event : clock.getEventLog()) {
      if (matchesType(event, selectedType) && event.toLowerCase(Locale.ROOT).contains(query)) {
        matchingEvents.add(event);
      }
    }
    listView.getItems().setAll(matchingEvents);
    if (!matchingEvents.isEmpty()) {
      listView.scrollTo(matchingEvents.size() - 1);
    }
  }

  /** Matches SimulationClock event wording against the selected category. */
  private boolean matchesType(String event, String selectedType) {
    if (selectedType == null || ALL_EVENTS.equals(selectedType)) return true;
    String lowerEvent = event.toLowerCase(Locale.ROOT);
    return switch (selectedType) {
      case ADMIN_ACTIONS -> lowerEvent.contains("admin:");
      case TRAIN_MOVEMENT ->
          lowerEvent.contains("departed")
              || lowerEvent.contains("arrived")
              || lowerEvent.contains("turned around");
      case DELAY_EVENTS -> lowerEvent.contains("delay");
      case SERVICE_CHANGES ->
          lowerEvent.contains("service suspended") || lowerEvent.contains("service resumed");
      default -> true;
    };
  }

  /** Exports exactly the entries currently visible after applying both filters. */
  private void exportFilteredEvents() {
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Export filtered event log");
    chooser.setInitialFileName("transitiq-event-log.csv");
    chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
    Window owner = getTabPane() == null ? null : getTabPane().getScene().getWindow();
    java.io.File selectedFile = chooser.showSaveDialog(owner);
    if (selectedFile == null) return;

    List<String> csvLines = new ArrayList<>();
    csvLines.add("event");
    for (String event : listView.getItems()) {
      csvLines.add(escapeCsv(event));
    }
    try {
      Files.writeString(
          selectedFile.toPath(),
          String.join(System.lineSeparator(), csvLines),
          StandardCharsets.UTF_8);
      result.setText(
          "Exported " + listView.getItems().size() + " events to " + selectedFile.getName() + ".");
    } catch (IOException e) {
      result.setText("Could not export the event log: " + e.getMessage());
    }
  }

  /** Quotes each field and doubles embedded quotes as required by CSV. */
  private String escapeCsv(String value) {
    String quote = String.valueOf((char) 34);
    return quote + value.replace(quote, quote + quote) + quote;
  }
}
