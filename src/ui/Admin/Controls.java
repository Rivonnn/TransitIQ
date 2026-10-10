package ui.Admin;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import simulation.SimulationClock;

public class Controls extends Tab {

    private final SimulationClock clock;

    public Controls(SimulationClock clock) {
        super("Controls");
        setClosable(false);
        this.clock = clock;

        VBox layout = new VBox(20,
                buildDelayTrainSection(),
                new Separator(),
                buildDisruptionSection(),
                new Separator(),
                buildServiceSection()
        );
        layout.setPadding(new Insets(20));
        setContent(layout);
    }

    // Terminal option 1: delay one train
    private VBox buildDelayTrainSection() {
        Label heading = new Label("Delay a train");
        TextField idField = new TextField();
        idField.setPromptText("Train ID");
        TextField minutesField = new TextField();
        minutesField.setPromptText("Delay in minutes");
        Button apply = new Button("Apply delay");
        Label result = new Label();

        apply.setOnAction(e -> {
            // 1. Read and trim both fields.
            // 2. Parse the minutes (try/catch NumberFormatException) -> message in result, then return.
            // 3. Reject 0 or negative -> message in result, then return.
            // 4. Call clock.delayTrain(id, minutes) and show "delayed" or "no such train".
        });

        return new VBox(8, heading, idField, minutesField, apply, result);
    }

    // Terminal option 2: delay every train
    private VBox buildDisruptionSection() {
        Label heading = new Label("Network disruption (delay all trains)");
        TextField minutesField = new TextField();
        minutesField.setPromptText("Delay in minutes");
        Button apply = new Button("Trigger disruption");
        Label result = new Label();

        apply.setOnAction(e -> {
            // Same checks as above for the minutes, then clock.delayAllTrains(minutes).
        });

        return new VBox(8, heading, minutesField, apply, result);
    }

    String serviceStatus = "Suspend service";

    // Terminal option 3: suspend / resume
    private VBox buildServiceSection() {
        Label heading = new Label("Service");
        Button toggle = new Button(serviceStatus);
        // TODO: set the button text from clock.isSuspended()
        //       ("Suspend service" or "Resume service")

        toggle.setOnAction(e -> {
            if (serviceStatus.equals("Suspend service")) {
                serviceStatus = "Resume service";
            } else {
                serviceStatus = "Suspend service";
            }
            toggle.setText(serviceStatus);
            // 1. Call clock.setSuspended(opposite of clock.isSuspended()).
            // 2. Set the button text again from clock.isSuspended().
        });

        return new VBox(8, heading, toggle);
    }
}