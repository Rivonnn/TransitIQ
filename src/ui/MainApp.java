package ui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Line;
import model.Station;
import model.Train;
import service.PassengerService;
import ui.Passenger.PassengerScreen;
import util.SeedData;

import java.util.List;
import java.util.Map;

public class MainApp extends Application {

    private Scene scene;

    private PassengerService passengerService;

    @Override
    public void start(Stage stage) {
        List<Station> stations = SeedData.loadStations();
        List<Train> trains = SeedData.loadTrains();
        Map<Line, List<Station>> lineOrders = SeedData.loadLineOrders(stations);
        passengerService = new PassengerService(stations, trains, lineOrders);

        scene = new Scene(new StackPane(), 700, 450);
        stage.setTitle("TransitIQ");
        stage.setScene(scene);
        showStart();
        stage.show();
    }

    private void showPassenger() {
        scene.setRoot(new PassengerScreen(passengerService, this::showStart).getRoot());
    }

    private void showStart() {
        scene.setRoot(new StartMenu(this::showPassenger, this::showAdmin).getRoot());
    }

    private void showAdmin() {
        scene.setRoot(placeholder("Admin screen"));
    }

    // Temporary stand-in until the real screens exist.
    private Parent placeholder(String text) {
        Label label = new Label(text + " (coming soon)");
        Button back = new Button("Back");
        back.setOnAction(e -> showStart());

        VBox box = new VBox(15, label, back);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    public static void main(String[] args) {
        launch(args);
    }
}