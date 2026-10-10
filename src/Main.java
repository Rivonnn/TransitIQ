import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.Line;
import model.Station;
import model.Train;
import service.PassengerService;
import simulation.SimulationClock;
import ui.Admin.AdminScreen;
import ui.Passenger.PassengerScreen;
import ui.StartMenu;
import util.SeedData;

import java.util.List;
import java.util.Map;

/**
 * Entry point of TransitIQ. Extends JavaFX's Application, so JavaFX creates the
 * window and calls start() for us. Owns the objects shared by every screen
 * (the service and the clock) and swaps between screens.
 */
public class Main extends Application {

    private Scene scene;
    private PassengerService passengerService;
    private SimulationClock clock;

    /**
     * Called by JavaFX once the runtime is ready. Loads the seed data, builds the
     * shared PassengerService, starts the simulation clock and shows the start menu.
     * The whole app uses a single Scene; moving between screens swaps its root.
     * @param stage the main window, provided by JavaFX
     */
    @Override
    public void start(Stage stage) {
        List<Station> stations = SeedData.loadStations();
        List<Train> trains = SeedData.loadTrains();
        Map<Line, List<Station>> lineOrders = SeedData.loadLineOrders(stations);
        passengerService = new PassengerService(stations, trains, lineOrders);

        clock = new SimulationClock(trains, Math.max(0, stations.size() - 1), 1000);
        clock.start();

        scene = new Scene(new StackPane(), 700, 450);
        stage.setTitle("TransitIQ");
        stage.setScene(scene);
        showStart();
        stage.show();
    }

    /**
     * Shows the start menu. Its two buttons are given showPassenger and showAdmin
     * to run when clicked.
     */
    private void showStart() {
        scene.setRoot(new StartMenu(this::showPassenger, this::showAdmin).getRoot());
    }

    /**
     * Shows the passenger screen. this::showStart is passed so its Back button
     * returns to the start menu.
     */
    private void showPassenger() {
        scene.setRoot(new PassengerScreen(passengerService, clock, this::showStart).getRoot());
    }

    /**
     * Shows the admin screen. this::showStart is passed so its Back button
     * returns to the start menu.
     */
    private void showAdmin() {
        scene.setRoot(new AdminScreen(passengerService, clock, this::showStart).getRoot());
    }

    /**
     * Called automatically by JavaFX when the window closes. Stops the clock's
     * background thread so it doesn't keep running.
     */
    @Override
    public void stop() {
        if (clock != null) clock.stop();
    }

    /**
     * JVM entry point. launch() starts JavaFX, which creates the window and calls
     * start(). It doesn't return until the window is closed.
     */
    public static void main(String[] args) {
        launch(args);
    }
}