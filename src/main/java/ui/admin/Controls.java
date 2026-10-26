package ui.admin;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import simulation.SimulationClock;
import simulation.SimulationClock.AdminSummary;

public final class Controls extends Tab {
  private final SimulationClock clock;
  private final Label summary = new Label();

  public Controls(SimulationClock clock) {
    super("Controls");
    setClosable(false);
    this.clock = clock;

    Label title = new Label("Control Panel");
    title.setStyle(
        "-fx-font-family: 'Pixel Operator'; -fx-font-size: 20px; -fx-text-fill: #00e5ff;");
    VBox layout =
        new VBox(
            20,
            title,
            buildSummarySection(),
            new Separator(),
            buildDelayTrainSection(),
            new Separator(),
            buildClearDelaySection(),
            new Separator(),
            buildDisruptionSection(),
            new Separator(),
            buildServiceSection());
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

  private VBox buildSummarySection() {
    Label heading = new Label("Network overview");
    summary.setWrapText(true);
    return new VBox(8, heading, summary);
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
            result.setText("Enter a train ID.");
            return;
          }
          Integer minutes = parsePositiveMinutes(minutesField.getText(), result);
          if (minutes == null) return;
          if (clock.delayTrain(trainId, minutes)) {
            result.setText("Train " + trainId.toUpperCase() + " delayed by " + minutes + " min.");
          } else {
            result.setText("No train with ID '" + trainId + "'.");
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
            result.setText("Enter a train ID.");
            return;
          }
          int clearedMinutes = clock.clearTrainDelay(trainId);
          if (clearedMinutes < 0) {
            result.setText("No train with ID '" + trainId + "'.");
          } else if (clearedMinutes == 0) {
            result.setText("Train " + trainId.toUpperCase() + " has no active delay.");
          } else {
            result.setText(
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
          result.setText("Disruption applied: all trains delayed by " + minutes + " min.");
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
          result.setText("Disruption applied: all trains delayed by " + minutes + " min.");
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
          result.setText("Service " + (suspended ? "suspended." : "resumed."));
        });
    return new VBox(8, heading, toggle, result);
  }

  private Integer parsePositiveMinutes(String text, Label result) {
    int minutes;
    try {
      minutes = Integer.parseInt(text.trim());
    } catch (NumberFormatException e) {
      result.setText("Enter a valid number of minutes.");
      return null;
    }
    if (minutes <= 0) {
      result.setText("Enter a number greater than 0.");
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
