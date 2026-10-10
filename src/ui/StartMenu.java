package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * StartMenu is the first screen which is shown to the user.
 * The user can select Passenger or Admin.
 */
public class StartMenu {

    private final VBox root; // the screen's layout, built once in the constructor

    /**
     * @param onPassenger code to run when "Passenger" is clicked (Main supplies it)
     * @param onAdmin code to run when "Admin" is clicked (Main supplies it)
     * The menu doesn't know what happens next, so it stays independent of the other screens.
     */
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

        // Stack the items top to bottom, with 15px between them.
        root = new VBox(15, title, subtitle, passengerBtn, adminBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
    }
    // Lets Main put this screen on the Scene without knowing it is a VBox.
    public Parent getRoot() {
        return root;
    }
}