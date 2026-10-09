package ui.Passenger;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import model.Train;
import service.PassengerService;

public class PassengerScreen {

    private final BorderPane root;

    public PassengerScreen(PassengerService service, Runnable onBack) {
        // Instantiate component tab
        RoutesAndFare routesAndFareTab = new RoutesAndFare(service);
        TrainMap trainMapTab = new TrainMap(service);
        TrainStatus trainStatusTab = new TrainStatus(service);

        TabPane tabs = new TabPane(
                routesAndFareTab,
                trainMapTab,
                trainStatusTab
        );

        Button back = new Button("Back");
        back.setOnAction(e -> onBack.run());
        HBox top = new HBox(back);
        top.setPadding(new Insets(10));

        root = new BorderPane(tabs);
        root.setTop(top);
    }

    public Parent getRoot() {
        return root;
    }
}