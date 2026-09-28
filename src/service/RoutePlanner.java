package service;

import model.Line;
import model.Station;
import exceptions.NoRouteFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class RoutePlanner {

    // Finds a direct common line between two stations if one exists
    public Line findCommonLine(Station source, Station destination) {
        for (Line line : source.getLines()) {
            if (destination.getLines().contains(line)) {
                return line;
            }
        }
        return null;
    }

    // Finds a direct segment between two stations on a specific line order
    public List<Station> findRoute(Station source, Station destination, List<Station> lineOrder)
            throws NoRouteFoundException {

        int fromIndex = lineOrder.indexOf(source);
        int toIndex = lineOrder.indexOf(destination);

        if (fromIndex == -1 || toIndex == -1) {
            throw new NoRouteFoundException(
                    "No route found from " + source.getName() + " to " + destination.getName()
            );
        }

        if (fromIndex <= toIndex) {
            return new ArrayList<>(lineOrder.subList(fromIndex, toIndex + 1));
        } else {
            List<Station> reversed = new ArrayList<>(lineOrder.subList(toIndex, fromIndex + 1));
            Collections.reverse(reversed);
            return reversed;
        }
    }

    // Finds the optimal route across direct, single-transfer, or multi-transfer paths
    public List<Station> findRouteWithTransfers(Station source, Station destination,
                                                Map<Line, List<Station>> lineOrders,
                                                List<Station> allStations) throws NoRouteFoundException {

        // 1. Direct route check
        Line commonLine = findCommonLine(source, destination);
        if (commonLine != null && lineOrders.containsKey(commonLine)) {
            return findRoute(source, destination, lineOrders.get(commonLine));
        }

        // 2. Multi-line search using BFS across stations and interchange connections
        Queue<List<Station>> pathQueue = new LinkedList<>();
        Set<Station> visitedStations = new HashSet<>();

        List<Station> initialPath = new ArrayList<>();
        initialPath.add(source);
        pathQueue.add(initialPath);
        visitedStations.add(source);

        while (!pathQueue.isEmpty()) {
            List<Station> currentPath = pathQueue.poll();
            Station currentStation = currentPath.get(currentPath.size() - 1);

            // Explore adjacent stations connected on all lines passing through currentStation
            for (Line line : currentStation.getLines()) {
                List<Station> lineStations = lineOrders.get(line);
                if (lineStations == null) continue;

                int currentIndex = lineStations.indexOf(currentStation);
                if (currentIndex == -1) continue;

                // Adjacent stations on this line (previous and next)
                int[] neighborIndices = {currentIndex - 1, currentIndex + 1};

                for (int neighborIndex : neighborIndices) {
                    if (neighborIndex >= 0 && neighborIndex < lineStations.size()) {
                        Station neighbor = lineStations.get(neighborIndex);

                        if (!visitedStations.contains(neighbor)) {
                            List<Station> newPath = new ArrayList<>(currentPath);
                            newPath.add(neighbor);

                            if (neighbor.equals(destination)) {
                                return newPath; // Return shortest station-hop path found
                            }

                            visitedStations.add(neighbor);
                            pathQueue.add(newPath);
                        }
                    }
                }
            }
        }

        throw new NoRouteFoundException("No route found between " + source.getName() + " and " + destination.getName());
    }
}