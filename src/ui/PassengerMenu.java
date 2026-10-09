package ui;

import model.Station;
import model.Train;
import model.Line;
import service.PassengerService;
import service.RoutePlanner;
import service.FareCalculator;
import util.Validator;
import exceptions.NoRouteFoundException;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class PassengerMenu {

    private final List<Station> stations;
    private final PassengerService service;
    private final Scanner scanner;

    public PassengerMenu(List<Station> stations, List<Train> trains,
                         Map<Line, List<Station>> lineOrders) {
        this.stations = stations;
        this.service = new PassengerService(stations, trains, lineOrders);
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

            String choice = scanner.nextLine().trim();

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

    // searchRoute: Validates stations and delegates direct/transfer routing to RoutePlanner
    private void searchRoute() {
        System.out.print("Enter source station: ");
        String sourceName = scanner.nextLine().trim();
        System.out.print("Enter destination station: ");
        String destName = scanner.nextLine().trim();

        Station source = Validator.findStationByName(sourceName, stations);
        Station destination = Validator.findStationByName(destName, stations);

        if (source == null || destination == null) {
            System.out.println("One or both stations not found. Please check spelling.");
            return;
        }

        try {
            List<Station> route = service.findRoute(source, destination);
            printRoute(route);
        } catch (NoRouteFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void checkFare() {
        System.out.print("Enter source station: ");
        String sourceName = scanner.nextLine().trim();
        System.out.print("Enter destination station: ");
        String destName = scanner.nextLine().trim();

        Station source = Validator.findStationByName(sourceName, stations);
        Station destination = Validator.findStationByName(destName, stations);

        if (source == null || destination == null) {
            System.out.println("One or both stations not found. Please check spelling.");
            return;
        }

        System.out.print("Train type (local/express): ");
        String type = scanner.nextLine().trim();
        Train train = service.findTrainByType(type);

        if (train == null) {
            System.out.println("No train of that type available.");
            return;
        }

        try {
            List<Station> route = service.findRoute(source, destination);
            double fare = service.calculateFare(route, train); 
            System.out.println("Fare: ₹" + fare);
        } catch (NoRouteFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    // Iteration to print route with newlines
    private void printRoute(List<Station> route) {
        System.out.println("Route:");
        for (int i = 0; i < route.size(); i++) {
            System.out.print("  " + (i + 1) + ". " + route.get(i).getName());
            if (i < route.size() - 1) {
                System.out.println(" ↓");
            } else {
                System.out.println();
            }
        }
    }
}