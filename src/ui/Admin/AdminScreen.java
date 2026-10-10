package ui.Admin;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import service.PassengerService;
import simulation.SimulationClock;
import ui.common.TrainLiveStatus;

public class AdminScreen{

    private final BorderPane root;

    public AdminScreen(PassengerService service, SimulationClock clock, Runnable onBack) {
        Controls controls = new Controls(clock);
        EventLog eventLog = new EventLog(clock);
        TrainLiveStatus trainLiveStatus = new TrainLiveStatus(service, clock);

        TabPane tabs = new TabPane(
                controls,
                trainLiveStatus,
                eventLog
        );

        tabs.tabMinWidthProperty().bind(
                tabs.widthProperty()
                        .divide(tabs.getTabs().size())
                        .subtract(20));

        Button back = new Button("Back");
        back.setOnAction(e -> onBack.run());
        HBox top = new HBox(back);
        top.setPadding(new Insets(10));

        root = new BorderPane(tabs);
        root.setTop(top);
    }

    public Parent getRoot() { return root;}
}
