package model;

import java.util.List;

public class Route {
    private final List<Station> stations;

    public Route(List<Station> stations) {
        this.stations = stations;
    }

    public List<Station> getStations() {
        return stations;
    }
}