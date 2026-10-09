package ui.Passenger;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;
import service.PassengerService;

public class TrainStatus extends Tab {

    public TrainStatus(PassengerService service) {
        super("Train Status");
        setClosable(false);

        Label title = new Label("Live Status of Trains");

        VBox layout = new VBox(title);
        layout.setPadding(new Insets(20));

        setContent(layout);
    }
}
