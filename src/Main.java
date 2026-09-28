import model.Line;
import model.Station;
import model.Train;
import simulation.SimulationClock;
import ui.PassengerMenu;
import util.SeedData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        // 1. Load data
        List<Station> stations = SeedData.loadStations();
        List<Train> trains = SeedData.loadTrains();
        Map<Line, List<Station>> lineOrders = SeedData.loadLineOrders(stations);

        // 2. Start simulation clock
        int lastStationIndex = Math.max(0, stations.size() - 1);
        SimulationClock clock = new SimulationClock(trains, lastStationIndex, 1000);
        clock.start();

        // 3. Launch Passenger Menu directly
        PassengerMenu passengerMenu = new PassengerMenu(stations, trains, lineOrders);
        passengerMenu.show();

        // 4. Cleanup on exit
        clock.stop();
    }
}