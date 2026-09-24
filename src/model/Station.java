package model;

import java.util.List;

public class Station {
    private final String name;
    private final List<Line> lines;

    public Station(String name, List<Line> lines) {
        this.name = name;
        this.lines = lines;
    }

    public String getName() {
        return name;
    }

    public List<Line> getLines() {
        return lines;
    }
}