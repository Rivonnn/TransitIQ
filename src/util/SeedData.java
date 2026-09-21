package util;

import model.Station;
import model.Train;
import java.util.ArrayList;
import java.util.List;

public class SeedData {
    // List made out of 'Station' objects, loading stations for later use.
    public static List<Station> loadStations() {
        return new ArrayList<>(); {
        }
    }

    // List made out of 'Train' objects, loading trains for later use.
    public static List<Train> loadTrains() {
        return new ArrayList<>();
    }
}
