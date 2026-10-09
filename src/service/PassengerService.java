package service;

import exceptions.NoRouteFoundException;
import model.ExpressTrain;
import model.Line;
import model.LocalTrain;
import model.Station;
import model.Train;

import java.util.List;
import java.util.Map;

public class PassengerService {

    private final List<Station> stations;
    private final List<Train> trains;
    private final Map<Line, List<Station>> lineOrders;
    private final RoutePlanner routePlanner = new RoutePlanner();
    private final FareCalculator fareCalculator = new FareCalculator();

    public PassengerService(List<Station> stations, List<Train> trains,
                            Map<Line, List<Station>> lineOrders) {
        this.stations = stations;
        this.trains = trains;
        this.lineOrders = lineOrders;
    }

    public List<Station> getStations() {
        return stations;
    }

    public List<Station> findRoute(Station source, Station destination)
            throws NoRouteFoundException {
        return routePlanner.findRouteWithTransfers(source, destination, lineOrders, stations);
    }

    // Returns null if no train of that type exists.
    public Train findTrainByType(String type) {
        for (Train train : trains) {
            if (type.equalsIgnoreCase("local") && train instanceof LocalTrain) return train;
            if (type.equalsIgnoreCase("express") && train instanceof ExpressTrain) return train;
        }
        return null;
    }

    public double calculateFare(List<Station> route, Train train) {
        return fareCalculator.calculateFare(route, train);
    }
}