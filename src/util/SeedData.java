package util;

import model.Line;
import model.Station;
import model.Train;
import model.LocalTrain;
import model.ExpressTrain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class SeedData {

    public static List<Station> loadStations() {
        List<Station> stations = new ArrayList<>();

        stations.add(new Station("Churchgate", Arrays.asList(Line.WESTERN)));
        stations.add(new Station("Marine Lines", Arrays.asList(Line.WESTERN)));
        stations.add(new Station("Charni Road", Arrays.asList(Line.WESTERN)));
        stations.add(new Station("Grant Road", Arrays.asList(Line.WESTERN)));
        stations.add(new Station("Dadar", Arrays.asList(Line.WESTERN, Line.CENTRAL)));
        stations.add(new Station("Bandra", Arrays.asList(Line.WESTERN, Line.HARBOUR)));
        stations.add(new Station("Andheri", Arrays.asList(Line.WESTERN, Line.HARBOUR)));

        return stations;
    }

    public static List<Train> loadTrains() {
        List<Train> trains = new ArrayList<>();
        trains.add(new LocalTrain("L101", 60, 5.0));
        trains.add(new ExpressTrain("E201", 80, 5.0, new HashSet<>(Arrays.asList(0, 4, 6))));
        return trains;
    }

    // Helper to generate line-to-station mappings for RoutePlanner
    public static Map<Line, List<Station>> loadLineOrders(List<Station> allStations) {
        Map<Line, List<Station>> lineOrders = new HashMap<>();

        for (Line line : Line.values()) {
            List<Station> lineStations = new ArrayList<>();
            for (Station s : allStations) {
                if (s.getLines().contains(line)) {
                    lineStations.add(s);
                }
            }
            lineOrders.put(line, lineStations);
        }

        return lineOrders;
    }
}