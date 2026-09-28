import model.Station;
import model.Train;
import util.SeedData;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Station> stations = SeedData.loadStations();
        List<Train> trains = SeedData.loadTrains();

        System.out.println("Stations loaded: " + stations.size());
        System.out.println("Trains loaded: " + trains.size());
    }
}