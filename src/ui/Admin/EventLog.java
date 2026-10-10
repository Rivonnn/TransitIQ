package ui.Admin;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import simulation.SimulationClock;

public class EventLog extends Tab {

    private final SimulationClock clock;
    private final ListView<String> listView = new ListView<>();

    public EventLog(SimulationClock clock) {
        super("Event Log");
        setClosable(false);
        this.clock = clock;

        listView.setPlaceholder(new Label("No events yet"));

        Label heading = new Label("Recent events");
        VBox layout = new VBox(10, heading, listView);
        VBox.setVgrow(listView, Priority.ALWAYS);
        layout.setPadding(new Insets(20));
        setContent(layout);

        refresh(); // fill once so it isn't blank for the first second

        // TODO: a Timeline that calls refresh() every second.
        //       Same pattern as in TrainLiveStatus.
    }

    private void refresh() {
        // 1. Get the events: clock.getEventLog() returns a List<String>.
        // 2. Replace the list's contents with them: listView.getItems().setAll(...)
        // 3. If the list isn't empty, scroll to the newest entry: listView.scrollTo(index of last item)
    }
}