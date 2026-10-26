package ui.admin;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import simulation.SimulationClock;

public final class EventLog extends Tab {
  private final SimulationClock clock;
  private final ListView<String> listView = new ListView<>();

  public EventLog(SimulationClock clock) {
    super("Event Log");
    setClosable(false);
    this.clock = clock;
    listView.setPlaceholder(new Label("No events yet"));
    Label title = new Label("Control Panel");
    title.setStyle(
        "-fx-font-family: 'Pixel Operator'; -fx-font-size: 20px; -fx-text-fill: #00e5ff;");
    Label heading = new Label("Recent events");
    VBox layout = new VBox(10, title, heading, listView);
    VBox.setVgrow(listView, Priority.ALWAYS);
    layout.setPadding(new Insets(20));
    setContent(layout);
    refresh();
    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), _ -> refresh()));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
  }

  private void refresh() {
    listView.getItems().setAll(clock.getEventLog());
    if (!listView.getItems().isEmpty()) {
      listView.scrollTo(listView.getItems().size() - 1);
    }
  }
}
