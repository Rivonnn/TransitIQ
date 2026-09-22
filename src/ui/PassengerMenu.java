package ui;

import model.Station;
import model.Train;
import model.Line;
import service.RoutePlanner;
import service.FareCalculator;
import util.Validator;
import exceptions.NoRouteFoundException;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class PassengerMenu {

    private List<Station> stations;
    private List<Train> trains;
    private Map<Line, List<Station>> lineOrders;
    private RoutePlanner routePlanner;
    private FareCalculator fareCalculator;
    private Scanner scanner;

    // Constructor
    public PassengerMenu(List<Station> stations, List<Train> trains,
                         Map<Line, List<Station>> lineOrders) {
        this.stations = stations;
        this.trains = trains;
        this.lineOrders = lineOrders;
        this.routePlanner = new RoutePlanner();
        this.fareCalculator = new FareCalculator();
        this.scanner = new Scanner(System.in);
    }

    // Simple menu screen
    public void show() {
        boolean running = true;
        while (running) {
            System.out.println("\n~~~~~ TransitIQ Passenger Menu ~~~~~");
            System.out.println("1. Search route");
            System.out.println("2. Check fare");
            System.out.println("3. Exit\n");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    searchRoute();
                    break;
                case "2":
                    checkFare();
                    break;
                case "3":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option, try again.");
            }
        }
    }

    // searchRoute: Validates both stations,
    private void searchRoute() {
        // Get source and destination as Strings.
        System.out.print("Enter source station: ");
        String sourceName = scanner.nextLine();
        System.out.print("Enter destination station: ");
        String destName = scanner.nextLine();

        // Check validity, return null or 'Station' object.
        Station source = Validator.findStationByName(sourceName, stations);
        Station destination = Validator.findStationByName(destName, stations);

        // If either is returned null, report it and return.
        if (source == null || destination == null) {
            System.out.println("One or both stations not found. Please check spelling.");
            return;
        }

        // Valid stations need to be checked if they hold a common line.
        // Create 'Line' object.
        Line commonLine = routePlanner.findCommonLine(source, destination);

        // If Line isn't present, null is given, report it and return.
        if (commonLine == null) {
            System.out.println("No direct route found (transfers not supported yet).");
            return;
        }

        // Valid stations and valid common line. Use try catch to check for route.
        // Use findRoute, and try to print route.
        // Catch 'NoRouteFoundException' if found, and display message for it.
        try {
            List<Station> route = routePlanner.findRoute(source, destination, lineOrders.get(commonLine));
            printRoute(route);
        } catch (NoRouteFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void checkFare() {
        /* Get source and destination, run through validator.
           Check for null for both of them and display message for it.
           Take the Stations and check if there is common route.
           Check for null Line and display message for it.
        */
        System.out.print("Enter source station: ");
        String sourceName = scanner.nextLine();
        System.out.print("Enter destination station: ");
        String destName = scanner.nextLine();

        Station source = Validator.findStationByName(sourceName, stations);
        Station destination = Validator.findStationByName(destName, stations);

        if (source == null || destination == null) {
            System.out.println("One or both stations not found. Please check spelling.");
            return;
        }

        Line commonLine = routePlanner.findCommonLine(source, destination);
        if (commonLine == null) {
            System.out.println("No direct route found (transfers not supported yet).");
            return;
        }

        // Get Train type
        System.out.print("Train type (local/express): ");
        String type = scanner.nextLine();
        Train train = findTrainByType(type);

        // Check for null
        if (train == null) {
            System.out.println("No train of that type available.");
            return;
        }

        // Try to find route, use that route to calculate fare.
        // catch for 'NoRouteFoundException'.
        try {
            List<Station> route = routePlanner.findRoute(source, destination, lineOrders.get(commonLine));
            double fare = fareCalculator.calculateFare(route, train);
            System.out.println("Fare: " + fare);
        } catch (NoRouteFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    // Check for Train type with string comparison and 'instanceof'.
    private Train findTrainByType(String type) {
        for (Train train : trains) {
            if (type.equalsIgnoreCase("local") && train instanceof model.LocalTrain) {
                return train;
            }
            if (type.equalsIgnoreCase("express") && train instanceof model.ExpressTrain) {
                return train;
            }
        }
        return null;
    }

    // Iteration to print route.
    private void printRoute(List<Station> route) {
        System.out.print("Route: ");
        for (Station s : route) {
            System.out.print(s.getName() + " ");
        }
        System.out.println();
    }
}