package service;

import model.Station;
import model.Train;

import java.util.List;

public class FareCalculator {

    // Fare is minimum 5.
    private static final double BASE_RATE_PER_STOP = 5.0;

    // Calculate fair based on journey distance and train type.
    public double calculateFare(List<Station> route, Train train) {
        int numberOfStops = route.size() - 1;

        // Based on journey distance. (Steps)
        double baseFare = numberOfStops * BASE_RATE_PER_STOP;

        // Based on train type. (AC, I, II, etc.)
        double multiplier = getFareMultiplier(train);

        // return double based on journey distance and train type.
        return baseFare * multiplier;
    }

    // To calculate fair based on the train type.
    private double getFareMultiplier(Train train) {
        switch (travelClass) {
            case AC: return 2.0;
            case FIRST: return 1.5;
            case SECOND: return 1.0;
            default: return 1.0; // Assume SECOND as default.
        }
    }
}