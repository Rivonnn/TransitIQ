package ui.admin;

import dao.TrainDAO;
import exceptions.DataAccessException;
import java.util.HashSet;
import java.util.Set;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import simulation.SimulationClock;
import simulation.SimulationClock.AdminSummary;

public final class Controls extends Tab {
  private static final String ERROR_STYLE = "-fx-text-fill: #ff5555;";
  private static final String SUCCESS_STYLE = "-fx-text-fill: #39ff88;";

  private final SimulationClock clock;
  private final Label summary = new Label();

  public Controls(SimulationClock clock) {
    super("Controls");
    setClosable(false);
    this.clock = clock;

    Label title = new Label("Control Panel");
    title.setStyle(
        "-fx-font-family: 'Pixel Operator'; -fx-font-size: 20px; -fx-text-fill: #00e5ff;");

    // Two equal-width columns so the left and right sections line up in every row
    GridPane grid = new GridPane();
    grid.setHgap(40);
    grid.setVgap(20);

    ColumnConstraints leftCol = new ColumnConstraints();
    leftCol.setPercentWidth(50);
    ColumnConstraints rightCol = new ColumnConstraints();
    rightCol.setPercentWidth(50);
    grid.getColumnConstraints().addAll(leftCol, rightCol);

    grid.add(buildAddTrainSection(), 0, 0);
    grid.add(buildRemoveTrainSection(), 1, 0);
    grid.add(buildDelayTrainSection(), 0, 1);
    grid.add(buildClearDelaySection(), 1, 1);
    grid.add(buildDisruptionSection(), 0, 2);
    grid.add(buildServiceSection(), 1, 2);

    VBox layout = new VBox(10, title, buildSummarySection(), new Separator(), grid);
    layout.setPadding(new Insets(20));

    ScrollPane scrollPane = new ScrollPane(layout);
    scrollPane.setFitToWidth(true);
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    setContent(scrollPane);

    refreshSummary();
    // The clock provides a synchronized snapshot; Timeline keeps UI updates on the JavaFX thread.
    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), _ -> refreshSummary()));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
  }

  // ---------- Message helpers ----------

  /** Shows a red message for invalid input or a failed action. */
  private void showError(Label label, String message) {
    label.setStyle(ERROR_STYLE);
    label.setText(message);
  }

  /** Shows a green message for a successful action. */
  private void showSuccess(Label label, String message) {
    label.setStyle(SUCCESS_STYLE);
    label.setText(message);
  }

  /** Shows a message in the default colour, for outcomes that are neither errors nor successes. */
  private void showInfo(Label label, String message) {
    label.setStyle("");
    label.setText(message);
  }

  // ---------- Sections ----------

  private VBox buildSummarySection() {
    Label heading = new Label("Network overview");
    summary.setWrapText(true);
    return new VBox(8, heading, summary);
  }

  /** Adds a train to the database. The simulation only picks it up after a restart. */
  private VBox buildAddTrainSection() {
    Label heading = new Label("Add a train");
    TextField idField = new TextField();
    idField.setPromptText("Train ID");

    ComboBox<String> typeBox = new ComboBox<>();
    typeBox.getItems().addAll("LOCAL", "EXPRESS");
    typeBox.setValue("LOCAL");

    TextField speedField = new TextField();
    speedField.setPromptText("Speed");
    TextField fareField = new TextField();
    fareField.setPromptText("Base fare");
    TextField stopsField = new TextField();
    stopsField.setPromptText("Stops, e.g. 2,7,9");
    // Stops only matter for express trains, so the field is disabled for local ones
    stopsField.disableProperty().bind(typeBox.valueProperty().isNotEqualTo("EXPRESS"));

    Button add = new Button("Add train");
    Label result = new Label();
    result.setWrapText(true);

    add.setOnAction(
        _ -> {
          String id = idField.getText().trim().toUpperCase();
          if (id.isEmpty()) {
            showError(result, "Enter a train ID.");
            return;
          }
          int speed;
          double fare;
          try {
            speed = Integer.parseInt(speedField.getText().trim());
            fare = Double.parseDouble(fareField.getText().trim());
          } catch (NumberFormatException e) {
            showError(result, "Speed and fare must be numbers.");
            return;
          }
          if (speed <= 0 || fare <= 0) {
            showError(result, "Speed and fare must be greater than 0.");
            return;
          }

          String type = typeBox.getValue();
          Set<Integer> stops = new HashSet<>();
          if (type.equals("EXPRESS")) {
            stops = parseStops(stopsField.getText(), result);
            if (stops == null) return;
          }

          try {
            new TrainDAO().addTrain(id, type, speed, fare, stops);
            showSuccess(result, "Train " + id + " added. Restart to see it in the simulation.");
            idField.clear();
            speedField.clear();
            fareField.clear();
            stopsField.clear();
          } catch (DataAccessException e) {
            showError(result, e.getMessage());
          }
        });

    return new VBox(8, heading, idField, typeBox, speedField, fareField, stopsField, add, result);
  }

  /** Removes a train from the database after confirmation. Takes effect on restart. */
  private VBox buildRemoveTrainSection() {
    Label heading = new Label("Remove a train");
    TextField idField = new TextField();
    idField.setPromptText("Train ID");
    Button remove = new Button("Remove train");
    Label result = new Label();
    result.setWrapText(true);

    remove.setOnAction(
        _ -> {
          String id = idField.getText().trim().toUpperCase();
          if (id.isEmpty()) {
            showError(result, "Enter a train ID.");
            return;
          }

          Alert confirm =
              new Alert(
                  Alert.AlertType.CONFIRMATION,
                  "Remove train " + id + " from the database?",
                  ButtonType.YES,
                  ButtonType.NO);
          confirm.setHeaderText(null);
          if (confirm.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) {
            return;
          }

          try {
            if (new TrainDAO().removeTrain(id)) {
              showSuccess(
                  result, "Train " + id + " removed. Restart to apply it to the simulation.");
              idField.clear();
            } else {
              showError(result, "No train with ID '" + id + "'.");
            }
          } catch (DataAccessException e) {
            showError(result, e.getMessage());
          }
        });

    return new VBox(8, heading, idField, remove, result);
  }

  private VBox buildDelayTrainSection() {
    Label heading = new Label("Delay a train");
    TextField idField = new TextField();
    idField.setPromptText("Train ID");
    TextField minutesField = new TextField();
    minutesField.setPromptText("Delay in minutes");
    Button apply = new Button("Apply delay");
    Label result = new Label();
    apply.setOnAction(
        _ -> {
          String trainId = idField.getText().trim();
          if (trainId.isEmpty()) {
            showError(result, "Enter a train ID.");
            return;
          }
          Integer minutes = parsePositiveMinutes(minutesField.getText(), result);
          if (minutes == null) return;
          if (clock.delayTrain(trainId, minutes)) {
            showSuccess(
                result, "Train " + trainId.toUpperCase() + " delayed by " + minutes + " min.");
          } else {
            showError(result, "No train with ID '" + trainId + "'.");
          }
        });
    return new VBox(8, heading, idField, minutesField, apply, result);
  }

  private VBox buildClearDelaySection() {
    Label heading = new Label("Clear a train delay");
    TextField idField = new TextField();
    idField.setPromptText("Train ID");
    Button clear = new Button("Clear delay");
    Label result = new Label();
    clear.setOnAction(
        _ -> {
          String trainId = idField.getText().trim();
          if (trainId.isEmpty()) {
            showError(result, "Enter a train ID.");
            return;
          }
          // clearTrainDelay returns -1 if the train doesn't exist, otherwise the minutes cleared
          int clearedMinutes = clock.clearTrainDelay(trainId);
          if (clearedMinutes < 0) {
            showError(result, "No train with ID '" + trainId + "'.");
          } else if (clearedMinutes == 0) {
            showInfo(result, "Train " + trainId.toUpperCase() + " has no active delay.");
          } else {
            showSuccess(
                result,
                "Cleared "
                    + clearedMinutes
                    + " min delay for train "
                    + trainId.toUpperCase()
                    + ".");
          }
        });
    return new VBox(8, heading, idField, clear, result);
  }

  private VBox buildDisruptionSection() {
    Label heading = new Label("Network disruption (delay all trains)");
    TextField minutesField = new TextField();
    minutesField.setPromptText("Delay in minutes");
    Button apply = new Button("Trigger disruption");
    Label result = new Label();
    apply.setOnAction(
        _ -> {
          Integer minutes = parsePositiveMinutes(minutesField.getText(), result);
          if (minutes == null) return;
          clock.delayAllTrains(minutes);
          showSuccess(result, "Disruption applied: all trains delayed by " + minutes + " min.");
        });

    Button fiveMinutes = presetButton(5, result);
    Button tenMinutes = presetButton(10, result);
    HBox presets = new HBox(8, fiveMinutes, tenMinutes);
    return new VBox(8, heading, minutesField, apply, new Label("Quick presets"), presets, result);
  }

  private Button presetButton(int minutes, Label result) {
    Button preset = new Button(minutes + " min");
    preset.setOnAction(
        _ -> {
          clock.delayAllTrains(minutes);
          showSuccess(result, "Disruption applied: all trains delayed by " + minutes + " min.");
        });
    return preset;
  }

  private VBox buildServiceSection() {
    Label heading = new Label("Service");
    Button toggle = new Button();
    Label result = new Label();
    updateServiceButton(toggle);
    toggle.setOnAction(
        _ -> {
          boolean suspended;
          synchronized (clock) {
            suspended = !clock.isSuspended();
            clock.setSuspended(suspended);
          }
          updateServiceButton(toggle);
          showSuccess(result, "Service " + (suspended ? "suspended." : "resumed."));
        });
    return new VBox(8, heading, toggle, result);
  }

  // ---------- Helpers ----------

  /**
   * Parses a comma-separated list of station indexes such as "2,7,9".
   *
   * @param text the raw text from the stops field
   * @param result the label that shows the error message if the text is invalid
   * @return the set of stop indexes, or null if the text is invalid (the error is already shown)
   */
  private Set<Integer> parseStops(String text, Label result) {
    Set<Integer> stops = new HashSet<>();
    try {
      for (String part : text.split(",")) {
        int index = Integer.parseInt(part.trim());
        if (index < 0) {
          showError(result, "Stop numbers can't be negative.");
          return null;
        }
        stops.add(index);
      }
    } catch (NumberFormatException e) {
      showError(result, "Stops must be numbers separated by commas.");
      return null;
    }
    return stops;
  }

  /**
   * Parses a delay in minutes and checks it is greater than 0.
   *
   * @param text the raw text from the minutes field
   * @param result the label that shows the error message if the text is invalid
   * @return the number of minutes, or null if invalid (the error is already shown)
   */
  private Integer parsePositiveMinutes(String text, Label result) {
    int minutes;
    try {
      minutes = Integer.parseInt(text.trim());
    } catch (NumberFormatException e) {
      showError(result, "Enter a valid number of minutes.");
      return null;
    }
    if (minutes <= 0) {
      showError(result, "Enter a number greater than 0.");
      return null;
    }
    return minutes;
  }

  private void updateServiceButton(Button toggle) {
    toggle.setText(clock.isSuspended() ? "Resume service" : "Suspend service");
  }

  /** Refreshes the dashboard from one clock snapshot so its counts stay consistent. */
  private void refreshSummary() {
    AdminSummary current = clock.getAdminSummary();
    summary.setText(
        "Tick "
            + current.tickCount()
            + " | moving "
            + current.movingTrains()
            + " | waiting "
            + current.waitingTrains()
            + " | arrived "
            + current.arrivedTrains()
            + " | delayed "
            + current.delayedTrains()
            + " | service "
            + (current.suspended() ? "suspended" : "running"));
  }
}
