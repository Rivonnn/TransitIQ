package dao;

import exceptions.DataAccessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import model.ExpressTrain;
import model.LocalTrain;
import model.Train;
import util.DBConnection;

/**
 * Data access object for the trains table. All SQL for trains lives here, so the rest of the app
 * works with Train objects and never touches JDBC directly. Every method opens its own connection
 * and closes it automatically (try-with-resources).
 */
public class TrainDAO {

  private static final String SELECT_ALL =
      "SELECT id, type, speed, base_fare, express_stops FROM trains ORDER BY id";

  private static final String INSERT_TRAIN =
      "INSERT INTO trains (id, type, speed, base_fare, express_stops) VALUES (?, ?, ?, ?, ?)";

  private static final String DELETE_TRAIN = "DELETE FROM trains WHERE id = ?";

  private static final String UPDATE_TRAIN =
      "UPDATE trains SET type = ?, speed = ?, base_fare = ?, express_stops = ? WHERE id = ?";

  /** Postgres error code for a unique or primary key violation (e.g. a duplicate train ID). */
  private static final String DUPLICATE_KEY = "23505";

  /**
   * Loads every train from the database, building a LocalTrain or ExpressTrain for each row
   * depending on its type column.
   *
   * @return all trains, ordered by ID
   * @throws DataAccessException if the database can't be reached or the query fails
   */
  public List<Train> getAllTrains() throws DataAccessException {
    List<Train> trains = new ArrayList<>();

    try (Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
        ResultSet rs = ps.executeQuery()) {

      while (rs.next()) {
        String id = rs.getString("id");
        String type = rs.getString("type");
        int speed = rs.getInt("speed");
        // The raw base fare; ExpressTrain applies its own 1.5x multiplier in its constructor
        double baseFare = rs.getDouble("base_fare");

        if ("EXPRESS".equals(type)) {
          trains.add(
              new ExpressTrain(id, speed, baseFare, parseStops(rs.getString("express_stops"))));
        } else {
          trains.add(new LocalTrain(id, speed, baseFare));
        }
      }
    } catch (SQLException e) {
      throw new DataAccessException("Could not load trains from database", e);
    }

    return trains;
  }

  /**
   * Turns the stored stops text (e.g. "2,7,9") back into a set of station indexes.
   *
   * @param trainStops the comma-separated text from the express_stops column, may be null
   * @return the stop indexes, or an empty set if the text is null or blank
   */
  private Set<Integer> parseStops(String trainStops) {
    Set<Integer> stops = new HashSet<>();
    if (trainStops == null || trainStops.isBlank()) {
      return stops;
    }
    for (String part : trainStops.split(",")) {
      stops.add(Integer.parseInt(part.trim()));
    }
    return stops;
  }

  /**
   * Loads every train as a raw row (id, type, speed, baseFare, expressStops), exactly as stored in
   * the table. Unlike {@link #getAllTrains()}, the base fare is not adjusted for express trains, so
   * values can be round-tripped through updates.
   *
   * @return all trains, ordered by ID
   * @throws DataAccessException if the database can't be reached or the query fails
   */
  public List<Map<String, Object>> getAllTrainRows() throws DataAccessException {
    List<Map<String, Object>> rows = new ArrayList<>();

    try (Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
        ResultSet rs = ps.executeQuery()) {

      while (rs.next()) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", rs.getString("id"));
        row.put("type", rs.getString("type"));
        row.put("speed", rs.getInt("speed"));
        row.put("baseFare", rs.getBigDecimal("base_fare"));
        row.put("expressStops", rs.getString("express_stops"));
        rows.add(row);
      }
    } catch (SQLException e) {
      throw new DataAccessException("Could not load trains from database", e);
    }

    return rows;
  }

  /**
   * Inserts a new train. Pass the raw base fare, not the 1.5x express fare, because the
   * ExpressTrain constructor applies the multiplier when the train is loaded back.
   *
   * @param id the train ID, which must be unique
   * @param type "LOCAL" or "EXPRESS"
   * @param speed the train's speed
   * @param baseFare the raw base fare
   * @param stops the stop indexes, used only for express trains (ignored for local ones)
   * @throws DataAccessException if the ID already exists or the insert fails
   */
  public void addTrain(String id, String type, int speed, double baseFare, Set<Integer> stops)
      throws DataAccessException {

    try (Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(INSERT_TRAIN)) {

      ps.setString(1, id);
      ps.setString(2, type);
      ps.setInt(3, speed);
      ps.setDouble(4, baseFare);
      ps.setString(5, stopsToText(type, stops));
      ps.executeUpdate();

    } catch (SQLException e) {
      if (DUPLICATE_KEY.equals(e.getSQLState())) {
        throw new DataAccessException("A train with ID '" + id + "' already exists", e);
      }
      throw new DataAccessException("Could not add train to database", e);
    }
  }

  /**
   * Replaces all fields of the train with the given ID.
   *
   * @param id the ID of the train to update
   * @param type "LOCAL" or "EXPRESS"
   * @param speed the train's speed
   * @param baseFare the raw base fare
   * @param stops the stop indexes, used only for express trains (ignored for local ones)
   * @return true if a train was updated, false if no train had that ID
   * @throws DataAccessException if the update fails
   */
  public boolean updateTrain(String id, String type, int speed, double baseFare, Set<Integer> stops)
      throws DataAccessException {
    try (Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(UPDATE_TRAIN)) {

      ps.setString(1, type);
      ps.setInt(2, speed);
      ps.setDouble(3, baseFare);
      ps.setString(4, stopsToText(type, stops));
      ps.setString(5, id);
      return ps.executeUpdate() > 0;

    } catch (SQLException e) {
      throw new DataAccessException("Could not update train in database", e);
    }
  }

  /**
   * Converts stop indexes to the stored comma-separated text, or null for local trains. Stored
   * sorted so the column stays deterministic.
   */
  private static String stopsToText(String type, Set<Integer> stops) {
    if (!"EXPRESS".equals(type) || stops == null || stops.isEmpty()) {
      return null;
    }
    return new TreeSet<>(stops).stream().map(String::valueOf).collect(Collectors.joining(","));
  }

  /**
   * Deletes the train with the given ID.
   *
   * @param id the ID of the train to remove
   * @return true if a train was deleted, false if no train had that ID
   * @throws DataAccessException if the delete fails
   */
  public boolean removeTrain(String id) throws DataAccessException {
    try (Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(DELETE_TRAIN)) {

      ps.setString(1, id);
      // executeUpdate returns the number of rows deleted
      return ps.executeUpdate() > 0;

    } catch (SQLException e) {
      throw new DataAccessException("Could not remove train from database", e);
    }
  }
}
