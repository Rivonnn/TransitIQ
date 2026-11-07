# Contributing to TransitIQ

Thank you for contributing to TransitIQ! This document outlines our development workflow, coding standards, and commands
for formatting and linting.

---

## 1. Prerequisites

- **Java 25** or higher (configured in `pom.xml`)
- **Maven 3.6+**
- JavaFX 25, the PostgreSQL JDBC driver, Javalin, and Jackson (managed automatically by Maven)
- **PostgreSQL** (optional but recommended) — trains are persisted in a `transitiq` database; the app
  falls back to built-in seed trains if it can't connect. See `sql/schema.sql` and `sql/seed.sql`.

---

## 2. Git Workflow

- **Never commit directly to `main`**: All changes must go through a Pull Request (PR).
- **Branch naming**: Create a branch off the working branch named after your feature or task:
    - `git switch -c feature/<feature-name>` or `git switch -c fix/<issue-name>`
- **Scope discipline**:
    - Keep one logical feature or fix per branch.
    - Modify only the files required for your task.
    - Avoid unnecessary refactoring or style adjustments across untouched files.
- **Verification**: Before opening a Pull Request, ensure that:
    - The project compiles with zero compiler errors.
    - `mvn clean verify` passes successfully.

---

## 3. Code Style & Standards

We follow strict Object-Oriented Programming (OOP) principles and
use [Google Java Format](https://github.com/google/google-java-format) enforced via Spotless.

### Architectural Boundaries

Keep code strictly separated into its designated package:

- `model`: Domain models (`Train`, `Station`, `Line`, `Passenger`, `Route`, `TrainStatus`). Private fields, encapsulation, no JavaFX
  or JDBC components.
- `service`: Business and calculation logic (`RoutePlanner`, `FareCalculator`, `PassengerService`). No JavaFX
  or JDBC components.
- `dao`: Data access objects (`TrainDAO`). All SQL/JDBC lives here — every method opens its own connection
  via `util.DBConnection` in try-with-resources and wraps `SQLException` in `DataAccessException`. Nothing
  outside `dao/` should touch JDBC directly.
- `api`: The standalone Javalin REST API (`TransitApi`). A thin layer that parses/validates JSON, calls the
  DAO, and maps errors to HTTP status codes. No SQL here. Runs via `mvn compile exec:java` on port 8080.
- `simulation`: Discrete clock and simulation management (`SimulationClock`). Runs on its own daemon thread; all train state access is synchronized.
- `exceptions`: Custom domain exceptions (`InvalidStationException`, `NoRouteFoundException`, `DataAccessException`).
- `util`: Helpers and seed data setup (`SeedData`, `Validator`, `DBConnection`). `DBConnection` is the only
  place that knows the JDBC URL and credentials — read from `TRANSITIQ_DB_URL`, `TRANSITIQ_DB_USER`,
  and `TRANSITIQ_DB_PASSWORD` env vars.
- `ui`: JavaFX graphical presentation and event handlers (`ui/StartMenu.java`, `ui/passenger/`, `ui/admin/`,
  `ui/common/`). Admin tabs include Controls, EventLog, and TrainLiveStatus.

### Code Conventions

- Keep all fields `private` and provide controlled accessors/mutators only where necessary.
- Use descriptive identifiers (avoid ambiguous abbreviations).
- Return early from methods rather than creating deeply nested `if/else` blocks.
- Use `enum` for fixed sets of states (e.g., `TrainStatus`).
- Handle expected failure cases with custom exceptions rather than letting unhandled errors crash the application.

---

## 4. Formatting and Linting Commands

All contributors must verify their code formatting and run static analysis before pushing changes.

### A. Spotless (Code Formatter)

Spotless ensures all Java code adheres to Google Java Style and removes unused imports.

- **Check formatting** (will fail if code violates format rules):
  ```powershell
  mvn spotless:check
  ```
- **Automatically reformat code**:
  ```powershell
  mvn spotless:apply
  ```

### B. SpotBugs (Static Analysis Linter)

SpotBugs inspects compiled bytecode to detect potential bugs, memory leaks, or dangerous patterns.

- **Run SpotBugs analysis**:
  ```powershell
  mvn spotbugs:check
  ```
- **Inspect issues in SpotBugs GUI**:
  ```powershell
  mvn spotbugs:gui
  ```

### C. Compiler Warnings

The Maven Compiler plugin is configured with `-Xlint:all` and `showWarnings=true` to alert on potential compile-time
problems.

- **Compile with warnings**:
  ```powershell
  mvn clean compile
  ```

### D. Full Verification

Always run the complete verification suite before submitting a Pull Request:

```powershell
mvn clean verify
```

This compiles all sources, runs packaging, executes Spotless format validation, and runs SpotBugs analysis.

---

## 5. Database & API Notes

- **Schema changes**: the database schema lives in `sql/schema.sql` and seed data in `sql/seed.sql`.
  Update both when changing the `trains` table, and keep `TrainDAO`'s column names in sync.
- **Connection config**: never hardcode credentials. `util.DBConnection` reads
  `TRANSITIQ_DB_URL`, `TRANSITIQ_DB_USER`, and `TRANSITIQ_DB_PASSWORD` with local Postgres defaults.
- **API changes**: `api/TransitApi` is the only Javalin entry point. Request validation happens in the
  API layer; SQL stays in `dao/TrainDAO`. Endpoint documentation lives in `docs/api/README.md` — update it
  when endpoints change.
- **Admin add/remove trains**: these write to Postgres via `TrainDAO`, but the simulation loads trains
  once at startup — UI changes only appear after an app restart.
