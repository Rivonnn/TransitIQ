package service;

import exceptions.NoRouteFoundException;
import model.ExpressTrain;
import model.Line;
import model.LocalTrain;
import model.Station;
import model.Train;

import java.util.List;
import java.util.Map;

/**
 * The logic behind the passenger screens, kept free of any UI code: route search,
 * train lookup and fare calculation. The UI calls these methods and never builds
 * routes or fares itself.
 */
public class PassengerService {

    // Shared data: all stations, all trains, and each line's stations in order.
    private final List<Station> stations;
    private final List<Train> trains;
    private final Map<Line, List<Station>> lineOrders;
    private final RoutePlanner routePlanner = new RoutePlanner();
    private final FareCalculator fareCalculator = new FareCalculator();

    // Constructor to initialize station, trains, lineOrders.
    public PassengerService(List<Station> stations, List<Train> trains,
                            Map<Line, List<Station>> lineOrders) {
        this.stations = stations;
        this.trains = trains;
        this.lineOrders = lineOrders;
    }

    public List<Station> getStations() {
        return stations; // getter
    }

    /**
     * Returns the real shared list, not a copy. The simulation clock changes these
     * trains from its own thread, so code that reads them from the UI must lock on
     * the clock first (see TrainLiveStatus).
     */
    public List<Train> getTrains() {
        return trains; // getter
    }

    /**
     * Finds the route between two stations: the direct line if they share one,
     * otherwise a search across transfers (see RoutePlanner).
     * @return the stations to pass through, in order, including both ends
     * @throws NoRouteFoundException if no path connects the two stations
     */
    public List<Station> findRoute(Station source, Station destination)
            throws NoRouteFoundException {
        return routePlanner.findRouteWithTransfers(source, destination, lineOrders, stations);
    }

    /**
     * Returns the first train of the given type ("local" or "express", ignoring
     * case), or null if there isn't one. Which train is picked doesn't matter much:
     * the fare uses the train's base fare, so trains of a type are assumed to cost the same.
     */
    public Train findTrainByType(String type) {
        for (Train train : trains) {
            if (type.equalsIgnoreCase("local") && train instanceof LocalTrain) return train;
            if (type.equalsIgnoreCase("express") && train instanceof ExpressTrain) return train;
        }
        return null;
    }

    /** Fare = [number of stops] x [train's base fare] (see FareCalculator). */
    public double calculateFare(List<Station> route, Train train) {
        return fareCalculator.calculateFare(route, train);
    }
}