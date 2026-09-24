package model;

import java.util.Set;

/**
 * A train that stops only at designated stations along its route.
 * Express trains have higher fare (1.5x base) and skip intermediate stations,
 * making them faster but less accessible than local trains.
 */
public class ExpressTrain extends Train {
    private final Set<Integer> stopStations;

    /**
     * Constructs an express train with the given stop stations.
     * Express trains charge 1.5x the base fare.
     *
     * @param stopStations Set of station indices where this train stops
     */
    public ExpressTrain(String id, int speed, double baseFare, Set<Integer> stopStations) {
        super(id, speed, baseFare * 1.5);
        this.stopStations = stopStations;
    }

    /**
     * Checks if this express train stops at the given station.
     *
     * @param stationIndex The index of the station to check
     * @return true if the station is in the stop set, false otherwise
     */
    public boolean stopsAtStation(int stationIndex) {
        return stopStations.contains(stationIndex);
    }
}
