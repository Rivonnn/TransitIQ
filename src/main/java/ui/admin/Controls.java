package ui.admin;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import simulation.SimulationClock;

public final class Controls extends Tab {
  private final SimulationClock clock;

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
            buildDelayTrainSection(),
            new Separator(),
            buildDisruptionSection(),
            new Separator(),
            buildServiceSection());
    layout.setPadding(new Insets(20));
    setContent(layout);
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
    return new VBox(8, heading, minutesField, apply, result);
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
}
