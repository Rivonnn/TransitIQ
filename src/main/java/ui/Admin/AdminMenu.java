package ui.Admin;

import simulation.SimulationClock;

import java.util.List;
import java.util.Scanner;

/**
 * Terminal menu for the operator: delay a train, trigger a disruption,
 * suspend service, and inspect train status / events.
 */
public class AdminMenu {

    private final SimulationClock clock;
    private final Scanner scanner;

    public AdminMenu(SimulationClock clock) {
        this.clock = clock;
        this.scanner = new Scanner(System.in);
    }

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println("\n~~~~~ TransitIQ Admin Menu ~~~~~");
            System.out.println("1. Delay a train");
            System.out.println("2. Trigger network disruption (delay all trains)");
            System.out.println("3. Suspend / resume service");
            System.out.println("4. View train status");
            System.out.println("5. View event log");
            System.out.println("6. Exit\n");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    delayTrain();
                    break;
                case "2":
                    triggerDisruption();
                    break;
                case "3":
                    toggleService();
                    break;
                case "4":
                    printLines("Tick " + clock.getTickCount()
                            + (clock.isSuspended() ? " (SERVICE SUSPENDED)" : ""),
                            clock.getStatusReport());
                    break;
                case "5":
                    printLines("Recent events", clock.getEventLog());
                    break;
                case "6":
                    running = false;
                    System.out.println("Leaving admin menu.");
                    break;
                default:
                    System.out.println("Invalid option, try again.");
            }
        }
    }

    private void delayTrain() {
        System.out.print("Train ID: ");
        String id = scanner.nextLine().trim();
        int minutes = readPositiveInt("Delay in minutes: ");
        if (minutes < 0) {
            return;
        }
        if (clock.delayTrain(id, minutes)) {
            System.out.println("Train " + id.toUpperCase() + " delayed by " + minutes + " min.");
        } else {
            System.out.println("No train with ID '" + id + "'.");
        }
    }

    private void triggerDisruption() {
        int minutes = readPositiveInt("Delay for every train (minutes): ");
        if (minutes < 0) {
            return;
        }
        clock.delayAllTrains(minutes);
        System.out.println("Disruption applied: all trains delayed by " + minutes + " min.");
    }

    private void toggleService() {
        boolean nowSuspended = !clock.isSuspended();
        clock.setSuspended(nowSuspended);
        System.out.println("Service " + (nowSuspended ? "suspended." : "resumed."));
    }

    // Returns the number, or -1 (after printing why) if the input is not a positive integer.
    private int readPositiveInt(String prompt) {
        System.out.print(prompt);
        String line = scanner.nextLine().trim();
        try {
            int value = Integer.parseInt(line);
            if (value <= 0) {
                System.out.println("Enter a number greater than 0.");
                return -1;
            }
            return value;
        } catch (NumberFormatException e) {
            System.out.println("'" + line + "' is not a valid number.");
            return -1;
        }
    }

    private void printLines(String heading, List<String> lines) {
        System.out.println("\n--- " + heading + " ---");
        if (lines.isEmpty()) {
            System.out.println("(nothing yet)");
        }
        for (String line : lines) {
            System.out.println(line);
        }
    }
}
