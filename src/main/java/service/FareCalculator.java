package service;

import model.Station;
import model.Train;

import java.util.List;

public class FareCalculator {

    private static final double BASE_RATE_PER_STOP = 5.0;

    // Find number of stops by route size and return (Fare * Steps).
    public double calculateFare(List<Station> route, Train train) {
        int numberOfStops = route.size() - 1;
        return numberOfStops * train.getBaseFare();
    }
}