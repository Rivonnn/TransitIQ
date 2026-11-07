package util;

import dao.TrainDAO;
import exceptions.DataAccessException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import model.ExpressTrain;
import model.Line;
import model.LocalTrain;
import model.Station;
import model.Train;

/**
 * Supplies the data the app starts with. Trains are read from the Postgres database (falling back
 * to built-in defaults if it is unreachable); stations and line orders are still hardcoded.
 */
public class SeedData {
  /**
   * Builds the hardcoded station list. The order of this list defines each station's index, which
   * express trains use as their stop numbers, so changing the order changes which stations they
   * stop at.
   */
  public static List<Station> loadStations() {
    List<Station> stations = new ArrayList<>();

    // Create shared interchange station instances first
    Station csmt = new Station("CSMT", List.of(Line.CENTRAL, Line.HARBOUR));
    Station dadar = new Station("Dadar", List.of(Line.WESTERN, Line.CENTRAL));
    Station kurla = new Station("Kurla", List.of(Line.CENTRAL, Line.HARBOUR));

    // 1. WESTERN LINE (South to North: Churchgate to Vasai Road)
    stations.add(new Station("Churchgate", List.of(Line.WESTERN)));
    stations.add(new Station("Marine Lines", List.of(Line.WESTERN)));
    stations.add(new Station("Charni Road", List.of(Line.WESTERN)));
    stations.add(new Station("Grant Road", List.of(Line.WESTERN)));
    stations.add(new Station("Mumbai Central", List.of(Line.WESTERN)));
    stations.add(dadar); // Dadar connects Western & Central
    stations.add(new Station("Bandra", List.of(Line.WESTERN)));
    stations.add(new Station("Andheri", List.of(Line.WESTERN)));
    stations.add(new Station("Borivali", List.of(Line.WESTERN)));
    stations.add(new Station("Dahisar", List.of(Line.WESTERN)));
    stations.add(new Station("Mira Road", List.of(Line.WESTERN)));
    stations.add(new Station("Bhayander", List.of(Line.WESTERN)));
    stations.add(new Station("Naigaon", List.of(Line.WESTERN)));
    stations.add(new Station("Vasai Road", List.of(Line.WESTERN)));

    // 2. CENTRAL LINE STATIONS (South to North: CSMT -> Dadar -> Kurla -> Thane)
    // CSMT added before Dadar for Central sequence
    stations.addFirst(csmt);
    stations.add(kurla); // Kurla connects Central & Harbour
    stations.add(new Station("Thane", List.of(Line.CENTRAL)));

    // 3. HARBOUR LINE STATIONS (South to North: CSMT -> Vadala Road -> Kurla -> Panvel)
    // Vadala Road sits between CSMT and Kurla
    stations.add(1, new Station("Vadala Road", List.of(Line.HARBOUR)));
    stations.add(new Station("Chembur", List.of(Line.HARBOUR)));
    stations.add(new Station("Vashi", List.of(Line.HARBOUR)));
    stations.add(new Station("Nerul", List.of(Line.HARBOUR)));
    stations.add(new Station("Belapur", List.of(Line.HARBOUR)));
    stations.add(new Station("Panvel", List.of(Line.HARBOUR)));

    return stations;
  }

  /**
   * Groups the stations by line, keeping the order from {@code allStations}. Built from each
   * station's line metadata rather than listed by hand.
   */
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

  /**
   * Loads all trains from the database. If the database can't be reached, logs the error and
   * returns the built-in default trains so the app can still start.
   */
  public static List<Train> loadTrains() {
    try {
      return new TrainDAO().getAllTrains();
    } catch (DataAccessException e) {
      System.err.println("DB unavailable (" + e.getErrorCode() + "): " + e.getMessage());
      return loadFallbackTrains();
    }
  }

  /** Default trains used only when the database is unavailable. Matches the rows in schema.sql. */
  private static List<Train> loadFallbackTrains() {
    List<Train> trains = new ArrayList<>();
    trains.add(new LocalTrain("L101", 60, 10.0));
    trains.add(new ExpressTrain("E201", 100, 20.0, new HashSet<>(List.of(2, 7, 9, 10, 15))));
    return trains;
  }
}
