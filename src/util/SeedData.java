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

        // Create shared interchange station instances
        Station csmt = new Station("CSMT", Arrays.asList(Line.CENTRAL, Line.HARBOUR));
        Station dadar = new Station("Dadar", Arrays.asList(Line.WESTERN, Line.CENTRAL));
        Station kurla = new Station("Kurla", Arrays.asList(Line.CENTRAL, Line.HARBOUR));

        // 1. WESTERN LINE (South to North)
        stations.add(new Station("Churchgate", Arrays.asList(Line.WESTERN)));
        stations.add(new Station("Marine Lines", Arrays.asList(Line.WESTERN)));
        stations.add(new Station("Charni Road", Arrays.asList(Line.WESTERN)));
        stations.add(new Station("Grant Road", Arrays.asList(Line.WESTERN)));
        stations.add(new Station("Mumbai Central", Arrays.asList(Line.WESTERN)));
        stations.add(dadar);
        stations.add(new Station("Bandra", Arrays.asList(Line.WESTERN)));
        stations.add(new Station("Andheri", Arrays.asList(Line.WESTERN)));
        stations.add(new Station("Borivali", Arrays.asList(Line.WESTERN)));
        stations.add(new Station("Dahisar", Arrays.asList(Line.WESTERN)));
        stations.add(new Station("Mira Road", Arrays.asList(Line.WESTERN)));
        stations.add(new Station("Bhayander", Arrays.asList(Line.WESTERN)));
        stations.add(new Station("Naigaon", Arrays.asList(Line.WESTERN)));
        stations.add(new Station("Vasai Road", Arrays.asList(Line.WESTERN)));

        // 2. CENTRAL LINE STATIONS (Ensuring South-to-North sequence: CSMT -> Dadar -> Kurla -> Thane)
        // CSMT is placed at the front of Central Line list
        stations.add(0, csmt);
        stations.add(kurla);
        stations.add(new Station("Thane", Arrays.asList(Line.CENTRAL)));

        // 3. HARBOUR LINE STATIONS (Vadala Road sits between CSMT and Kurla)
        // Insert Vadala Road right after CSMT (index 1)
        stations.add(1, new Station("Vadala Road", Arrays.asList(Line.HARBOUR)));
        stations.add(new Station("Chembur", Arrays.asList(Line.HARBOUR)));
        stations.add(new Station("Vashi", Arrays.asList(Line.HARBOUR)));
        stations.add(new Station("Nerul", Arrays.asList(Line.HARBOUR)));
        stations.add(new Station("Belapur", Arrays.asList(Line.HARBOUR)));
        stations.add(new Station("Panvel", Arrays.asList(Line.HARBOUR)));

        return stations;
    }

    // Fully automated and dynamic map generator using enum metadata
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

    public static List<Train> loadTrains() {
        List<Train> trains = new ArrayList<>();
        trains.add(new LocalTrain("L101", 60, 5.0));
        trains.add(new ExpressTrain("E201", 80, 5.0, new HashSet<>(Arrays.asList(0, 5, 7, 8, 13))));
        return trains;
    }
}