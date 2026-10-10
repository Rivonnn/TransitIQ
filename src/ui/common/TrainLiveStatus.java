package ui.common;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.ExpressTrain;
import model.LocalTrain;
import model.Station;
import model.Train;
import service.PassengerService;
import simulation.SimulationClock;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Table of every train: id, type, status, current station and delay, refreshed
 * every second. Used by both the passenger and admin screens, each with its own
 * instance, because a Tab can only belong to one TabPane.
 */
public class TrainLiveStatus extends Tab {

    /**
     * One line of the table. Everything is a String, as it's only for display.
     * It's a snapshot of a train at the moment of the refresh, not a live link,
     * which is why the table is rebuilt every second.
     */
    public record TrainRow(String id, String type, String status, String station, String delay) {}

    // service: where the trains come from. clock: only used as a lock (see refresh()).
    // stations: used to turn a train's station index into a name.
    private final PassengerService service;
    private final SimulationClock clock;
    private final List<Station> stations;
    private final TableView<TrainRow> table = new TableView<>();

    /**
     * Builds the table and starts the refresh timer.
     * @param service provides the trains and the stations
     * @param clock   the simulation clock; locked on while the trains are read
     */
    public TrainLiveStatus(PassengerService service, SimulationClock clock) {
        super("Train Status");
        setClosable(false);
        this.service = service;
        this.clock = clock;
        this.stations = service.getStations();

        // One column per field of TrainRow.
        table.getColumns().addAll(
                column("Train", TrainRow::id),
                column("Type", TrainRow::type),
                column("Status", TrainRow::status),
                column("Station", TrainRow::station),
                column("Delay", TrainRow::delay)
        );
        // Make the columns fill the table's width, with the last one taking up the slack.
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // Layout: title above the table. setVgrow lets the table take all the leftover height.
        Label title = new Label("Live Status of Trains");
        VBox layout = new VBox(10, title, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        layout.setPadding(new Insets(20));
        setContent(layout);

        refresh(); // fill once now so the table isn't empty for the first second

        // Repeating timer: calls refresh() once a second, forever. A Timeline runs on the
        // JavaFX thread, so updating the table from it is safe without Platform.runLater.
        // NOTE: it's never stopped. Each visit to a screen creates a new tab and a new
        // timer, and the old ones keep running. Harmless here, but wasteful.
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> refresh()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Builds one table column.
     * @param title  the column header
     * @param getter picks this column's value out of a row (e.g. TrainRow::id)
     * The table requires an observable value in each cell, so the text is wrapped
     * in a SimpleStringProperty. The rows never change, so the wrapper never fires.
     */
    private TableColumn<TrainRow, String> column(String title, Function<TrainRow, String> getter) {
        TableColumn<TrainRow, String> col = new TableColumn<>(title);
        col.setCellValueFactory(data -> new SimpleStringProperty(getter.apply(data.getValue())));
        return col;
    }

    /**
     * Rebuilds the rows from the current trains and replaces the table's contents.
     * Runs once at construction and then every second from the Timeline.
     */
    private void refresh() {
        List<TrainRow> rows = new ArrayList<>();

        // The clock thread changes trains while holding its own lock, so we take the same
        // lock while reading them. Otherwise, a tick could land halfway through and give a
        // row whose status and station don't match. The lock is held only while copying
        // values into rows; the table is updated after it's released.
        synchronized (clock) {
            for (Train t : service.getTrains()) {
                // A train only stores a station index. Convert it to a name, or show "?"
                // if it's outside the station list so a bad index can't crash the refresh.
                int idx = t.getCurrentStationIndex();
                String station = (idx >= 0 && idx < stations.size())
                        ? stations.get(idx).getName() : "?";
                rows.add(new TrainRow(
                        t.getId(),
                        typeOf(t),
                        t.getStatus().name(),
                        station,
                        t.getDelayMinutes() + " min"));
            }
        }

        // Replace every row in a single operation so the table redraws once.
        // Side effect: any selected row is cleared on each refresh.
        table.getItems().setAll(rows);
    }

    // instanceof asks which subclass a train really is. "Train" is the fallback for
    // the base class.
    private String typeOf(Train t) {
        if (t instanceof ExpressTrain) return "Express";
        if (t instanceof LocalTrain) return "Local";
        return "Train";
    }
}