package model;

import java.util.ArrayList;
import java.util.List;

/**
 * A passenger: where they start, where they want to go, and the route they chose.
 */
public class Passenger {
    private final String name;
    private final Station source;
    private final Station destination;
    private List<Station> chosenRoute;

    public Passenger(String name, Station source, Station destination) {
        if (source == null || destination == null) {
            throw new IllegalArgumentException("Source and destination must not be null.");
        }
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.chosenRoute = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Station getSource() {
        return source;
    }

    public Station getDestination() {
        return destination;
    }

    public List<Station> getChosenRoute() {
        return chosenRoute;
    }

    // Stores the route this passenger picked (for example one returned by RoutePlanner).
    public void setChosenRoute(List<Station> route) {
        this.chosenRoute = (route == null) ? new ArrayList<>() : new ArrayList<>(route);
    }

    @Override
    public String toString() {
        return name + ": " + source.getName() + " -> " + destination.getName()
                + " (" + chosenRoute.size() + " stations on route)";
    }
}
