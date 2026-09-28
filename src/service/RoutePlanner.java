package service;

import model.Station;
import model.Line;
import exceptions.NoRouteFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoutePlanner {

    // The stations mentioned must be in the same line before we find the route.
    public Line findCommonLine(Station source, Station destination) {

        // Iterate through the source's line list and check if the destination's line list matches.
        for (Line line : source.getLines()) {
            if (destination.getLines().contains(line)) {
                return line;
            }
        }

        return null;
    }

    // Function to find route, returns List<Station>, throws NoRouteFoundException.
    public List<Station> findRoute(Station source, Station destination, List<Station> lineOrder)
            throws NoRouteFoundException {

        // Using 'indexOf' to find the index within the specified list.
        int fromIndex = lineOrder.indexOf(source);
        int toIndex = lineOrder.indexOf(destination);

        // If index is not found, it is assigned -1, catch it and throw error message.
        if (fromIndex == -1 || toIndex == -1) {
            throw new NoRouteFoundException(
                    "No route found from " + source.getName() + " to " + destination.getName()
            );
        }

        /* Stations are in a List with indexes 1, 2, 3, etc.
        If the path is straightforward,
            example; [A, B, C] is list. A -> C is route. A is index 1, C is index 3,
            1 < 3 is true, route is straight to trace.
        If path is reverse of the list present.
            example; Same list, but route is C -> A, 3 < 1 is false,
            return a reversed list which matches the path direction, [C, B, A].
        */
        if (fromIndex <= toIndex) {
            return new ArrayList<>(lineOrder.subList(fromIndex, toIndex + 1));
        } else {
            List<Station> reversed = new ArrayList<>(lineOrder.subList(toIndex, fromIndex + 1));
            java.util.Collections.reverse(reversed);
            return reversed;
        }

    }

    // Finds direct routes or transfer routes using shared interchange stations.
    public List<Station> findRouteWithTransfers(Station source, Station destination,
                                                Map<Line, List<Station>> lineOrders,
                                                List<Station> allStations) throws NoRouteFoundException {

        // 1. Check for a direct route on a common line first.
        Line commonLine = findCommonLine(source, destination);
        if (commonLine != null && lineOrders.containsKey(commonLine)) {
            return findRoute(source, destination, lineOrders.get(commonLine));
        }

        // 2. If no direct line exists, look for an interchange station connecting source line and destination line.
        for (Line sourceLine : source.getLines()) {
            List<Station> sourceLineStations = lineOrders.get(sourceLine);
            if (sourceLineStations == null) continue;

            for (Line destLine : destination.getLines()) {
                List<Station> destLineStations = lineOrders.get(destLine);
                if (destLineStations == null) continue;

                // Iterate through all stations to find an interchange stop belonging to both lines.
                for (Station interchange : allStations) {
                    if (interchange.getLines().contains(sourceLine) && interchange.getLines().contains(destLine)) {
                        try {
                            List<Station> leg1 = findRoute(source, interchange, sourceLineStations);
                            List<Station> leg2 = findRoute(interchange, destination, destLineStations);

                            // Merge leg 1 and leg 2 while dropping duplicate interchange station.
                            List<Station> fullRoute = new ArrayList<>(leg1);
                            fullRoute.addAll(leg2.subList(1, leg2.size()));
                            return fullRoute;
                        } catch (NoRouteFoundException ignored) {
                            // Continue searching if this interchange cannot complete the route.
                        }
                    }
                }
            }
        }

        throw new NoRouteFoundException("No route found between " + source.getName() + " and " + destination.getName());
    }
}