package util;

import java.util.List;
import model.Station;

// To check if the station actually exists.
public class Validator {

  // Checks if a typed station name matches an existing station
  public static boolean isValidStation(String name, List<Station> stations) {
    for (Station station : stations) {
      if (station.getName().equalsIgnoreCase(name)) {
        return true;
      }
    }
    return false;
  }

  // Finds and returns the actual 'Station' object matching a typed name, or null if not found
  public static Station findStationByName(String name, List<Station> stations) {
    for (Station station : stations) {
      if (station.getName().equalsIgnoreCase(name)) {
        return station;
      }
    }
    return null;
  }
}
