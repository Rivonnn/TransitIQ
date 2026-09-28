import model.Line;
import model.Station;
import model.Train;
import simulation.SimulationClock;
import ui.PassengerMenu;
import ui.AdminMenu;
import util.SeedData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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

        // 3. Create menus
        PassengerMenu passengerMenu = new PassengerMenu(stations, trains, lineOrders);
        AdminMenu adminMenu = new AdminMenu(clock);
        Scanner scanner = new Scanner(System.in);

        // 4. Main menu loop
        boolean running = true;
        while (running) {
            System.out.println("\n~~~~~ TransitIQ Main Menu ~~~~~");
            System.out.println("1. Passenger Menu");
            System.out.println("2. Admin Menu");
            System.out.println("3. Exit\n");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    passengerMenu.show();
                    break;
                case "2":
                    adminMenu.show();
                    break;
                case "3":
                    running = false;
                    System.out.println("Shutting down TransitIQ...");
                    break;
                default:
                    System.out.println("Invalid option, try again.");
            }
        }

        // 5. Cleanup on exit
        clock.stop();
        scanner.close();
    }
}