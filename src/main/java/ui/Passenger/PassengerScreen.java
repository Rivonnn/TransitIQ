package ui.Passenger;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import service.PassengerService;
import simulation.SimulationClock;
import ui.common.TrainLiveStatus;

/**
 * The passenger window: a Back button on top, three tabs below (Route & Fare,
 * Train Map, Train Status). Main builds a new one each time the user opens it.
 */
public class PassengerScreen {

    private final BorderPane root;

    /**
     * @param service passed to the tabs that need data
     * @param clock   passed to TrainLiveStatus, which locks on it to read trains safely
     * @param onBack  run by the Back button (Main returns to the start menu)
     */
    public PassengerScreen(PassengerService service, SimulationClock clock, Runnable onBack) {
        // Creating the tab based objects.
        RoutesAndFare routesAndFareTab = new RoutesAndFare(service);
        TrainMap trainMapTab = new TrainMap();
        TrainLiveStatus trainStatusTab = new TrainLiveStatus(service, clock);

        // Using the tab based objects.
        TabPane tabs = new TabPane(
                routesAndFareTab,
                trainMapTab,
                trainStatusTab
        );

        // Give each tab header an equal share of the width. This is a binding, not a
        // one-off value, so it follows window resizing. It must come after the tabs are
        // added, because getTabs().size() is read once, when this line runs. The
        // subtracted number is a fudge factor for each header's padding, tuned by eye:
        // too small and scroll arrows appear, too big and a gap shows on the right.
        tabs.tabMinWidthProperty().bind(
                tabs.widthProperty()
                        .divide(tabs.getTabs().size())
                        .subtract(20));

        // Layout: Back button in a bar along the top, tabs fill the rest.
        Button back = new Button("Back");
        back.setOnAction(e -> onBack.run());
        HBox top = new HBox(back);
        top.setPadding(new Insets(10));

        // Top and BorderPane being set.
        root = new BorderPane(tabs);
        root.setTop(top);
    }

    // Used by Main to put this screen on the Scene.
    public Parent getRoot() {
        return root; // Getter
    }
}