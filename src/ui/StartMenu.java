package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class StartMenu {

    private final VBox root;

    public StartMenu(Runnable onPassenger, Runnable onAdmin) {
        Label title = new Label("TransitIQ");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");
        Label subtitle = new Label("Simulated railway network");

        Button passengerBtn = new Button("Passenger");
        Button adminBtn = new Button("Admin");
        passengerBtn.setPrefWidth(200);
        adminBtn.setPrefWidth(200);

        passengerBtn.setOnAction(e -> onPassenger.run());
        adminBtn.setOnAction(e -> onAdmin.run());

        root = new VBox(15, title, subtitle, passengerBtn, adminBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
    }

    public Parent getRoot() {
        return root;
    }
}