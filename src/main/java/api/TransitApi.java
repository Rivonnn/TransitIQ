package api;

import dao.TrainDAO;
import exceptions.DataAccessException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Minimal REST API exposing CRUD on the trains table. Standalone: run {@link #main} and it serves
 * on port 8080 until the process is killed.
 */
public class TransitApi {

  private static final int PORT = 8080;

  private final TrainDAO trainDAO = new TrainDAO();

  public TransitApi() {
    Javalin.create(
            config -> {
              config.routes.exception(
                  DataAccessException.class,
                  (e, ctx) -> ctx.status(500).json(error(e.getMessage())));
              config.routes.exception(
                  IllegalArgumentException.class,
                  (e, ctx) -> ctx.status(400).json(error(e.getMessage())));

              config.routes.get("/api/trains", ctx -> ctx.json(trainDAO.getAllTrainRows()));
              config.routes.post("/api/trains", this::createTrain);
              config.routes.put("/api/trains/{id}", this::updateTrain);
              config.routes.delete("/api/trains/{id}", this::deleteTrain);
            })
        .start(PORT);

    System.out.println("TransitIQ API listening on http://localhost:" + PORT);
  }

  public static void main(String[] args) {
    new TransitApi();
  }

  private void createTrain(Context ctx) throws DataAccessException {
    TrainRequest request = readRequest(ctx);
    if (request.id() == null || request.id().isBlank()) {
      throw new IllegalArgumentException("id is required");
    }
    trainDAO.addTrain(
        request.id(), request.type(), request.speed(), request.baseFare(), request.stopsSet());
    ctx.status(201).json(Map.of("id", request.id()));
  }

  private void updateTrain(Context ctx) throws DataAccessException {
    String id = ctx.pathParam("id");
    TrainRequest request = readRequest(ctx);
    boolean updated =
        trainDAO.updateTrain(
            id, request.type(), request.speed(), request.baseFare(), request.stopsSet());
    if (!updated) {
      notFound(ctx, id);
      return;
    }
    ctx.json(Map.of("id", id));
  }

  private void deleteTrain(Context ctx) throws DataAccessException {
    String id = ctx.pathParam("id");
    if (!trainDAO.removeTrain(id)) {
      notFound(ctx, id);
      return;
    }
    ctx.status(204);
  }

  /** Parses and validates the JSON body, or throws IllegalArgumentException (-> 400). */
  private TrainRequest readRequest(Context ctx) {
    TrainRequest request;
    try {
      request = ctx.bodyAsClass(TrainRequest.class);
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid JSON body");
    }
    if (request == null) {
      throw new IllegalArgumentException("Request body is required");
    }
    validate(request);
    return request;
  }

  private void validate(TrainRequest request) {
    if (request.type() == null
        || (!request.type().equals("LOCAL") && !request.type().equals("EXPRESS"))) {
      throw new IllegalArgumentException("type must be LOCAL or EXPRESS");
    }
    if (request.speed() <= 0) {
      throw new IllegalArgumentException("speed must be positive");
    }
    if (request.baseFare() < 0) {
      throw new IllegalArgumentException("baseFare must not be negative");
    }
  }

  private void notFound(Context ctx, String id) {
    ctx.status(404).json(error("No train with ID '" + id + "'"));
  }

  private Map<String, String> error(String message) {
    return Map.of("error", message);
  }

  private record TrainRequest(
      String id, String type, int speed, double baseFare, List<Integer> stops) {

    Set<Integer> stopsSet() {
      return stops == null ? Set.of() : Set.copyOf(stops);
    }
  }
}
