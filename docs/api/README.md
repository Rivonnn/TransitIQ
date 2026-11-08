# TransitIQ REST API

A minimal CRUD API over the `trains` table, built with [Javalin](https://javalin.io) and Jackson.
It runs standalone on port **8080** — it does not start with the JavaFX app.

## Running it

Requires the database to be set up first (see `sql/schema.sql` and `sql/seed.sql`).

```bash
mvn compile exec:java
```

The server starts and blocks until you stop it (Ctrl+C). You should see:

```
TransitIQ API listening on http://localhost:8080
```

Database connection settings are read from `util.DBConnection` — by default
`jdbc:postgresql://localhost:5432/transitiq` as user `postgres`. Override with the
environment variables `TRANSITIQ_DB_URL`, `TRANSITIQ_DB_USER`, and `TRANSITIQ_DB_PASSWORD`.

## Endpoints

All endpoints live under `/api` and exchange JSON.

### List trains

```
GET /api/trains
```

Response `200` — array of raw table rows, ordered by ID:

```json
[
  {
    "id": "E201",
    "type": "EXPRESS",
    "speed": 100,
    "baseFare": 20.00,
    "expressStops": "2,7,9,10,15"
  },
  {
    "id": "L101",
    "type": "LOCAL",
    "speed": 60,
    "baseFare": 10.00,
    "expressStops": null
  }
]
```

### Create a train

```
POST /api/trains
Content-Type: application/json
```

```json
{
  "id": "L102",
  "type": "LOCAL",
  "speed": 60,
  "baseFare": 10.00,
  "stops": []
}
```

| Field       | Required | Notes                                                        |
| ----------- | -------- | ------------------------------------------------------------ |
| `id`        | yes      | Unique, max 20 chars. Must not already exist.                |
| `type`      | yes      | `"LOCAL"` or `"EXPRESS"`.                                    |
| `speed`     | yes      | Positive integer.                                            |
| `baseFare`  | yes      | Non-negative number. This is the **raw** base fare — express trains apply their 1.5x multiplier themselves. |
| `stops`     | no       | Array of station indexes. Only used for `EXPRESS`; stored as comma-separated text. Omit or `[]` for local trains. |

Response `201`:

```json
{ "id": "L102" }
```

### Update a train

```
PUT /api/trains/{id}
Content-Type: application/json
```

Same body shape as create (`id` in the path is authoritative; the body's `id` is ignored).
Replaces **all** fields of the train.

```json
{ "type": "EXPRESS", "speed": 110, "baseFare": 25.00, "stops": [2, 7, 9] }
```

Response `200`:

```json
{ "id": "L102" }
```

### Delete a train

```
DELETE /api/trains/{id}
```

Response `204` on success, `404` if no train has that ID.

## Errors

Errors return a JSON body with an `error` message:

| Status | Meaning                                              |
| ------ | ---------------------------------------------------- |
| `400`  | Invalid input — bad JSON, missing fields, bad values |
| `404`  | No train with the given ID (update/delete)           |
| `500`  | Database error — e.g. duplicate ID on create         |

```json
{ "error": "A train with ID 'L101' already exists" }
```

## Example with curl

```bash
# list
curl http://localhost:8080/api/trains

# create
curl -X POST http://localhost:8080/api/trains \
  -H "Content-Type: application/json" \
  -d '{"id":"E301","type":"EXPRESS","speed":100,"baseFare":20,"stops":[2,7,9]}'

# update
curl -X PUT http://localhost:8080/api/trains/E301 \
  -H "Content-Type: application/json" \
  -d '{"type":"EXPRESS","speed":120,"baseFare":22,"stops":[2,7]}'

# delete
curl -X DELETE http://localhost:8080/api/trains/E301
```

## Notes

- The API only touches the database. Trains added or changed here appear in the app on its next
  start, since trains are loaded once at startup.
- `expressStops` is a comma-separated list of station **indexes** (the order stations appear in
  `SeedData.loadStations()`), not names.
