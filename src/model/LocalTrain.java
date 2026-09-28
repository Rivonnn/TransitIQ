package model;

/**
 * A train that stops at every station along its route.
 * Local trains have standard fare and visit all intermediate stations,
 * making them slower but more accessible than express trains.
 */
public class LocalTrain extends Train {
    public LocalTrain(String id, int speed, double baseFare) {
        super(id, speed, baseFare);
    }

    /**
     * Local trains stop at all stations.
     * This method always returns true for local trains.
     */
    public boolean stopsAtStation(int stationIndex) {
        return true;
    }
}
