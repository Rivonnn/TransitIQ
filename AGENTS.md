# TransitIQ: AGENTS.md

This file tells an AI agent how to work on this codebase. Follow it exactly. If something you're asked to do conflicts with this file, stop and point out the conflict instead of guessing.

## What This Project Is

TransitIQ is a Java OOP mini-project simulating a transit info system (inspired by M-Indicator/Chalo). It uses **JavaFX** for a retro-styled graphical interface, **PostgreSQL via JDBC** for train persistence, and a standalone **Javalin REST API** for train CRUD.

## Current Phase: Database, API & Release Preparation

The project is now focused on three main areas:
1. **Database persistence** - Trains are stored in PostgreSQL via `dao/TrainDAO.java` and `util/DBConnection.java`. The app falls back to built-in seed trains if the database is unreachable.
2. **REST API** - `api/TransitApi.java` exposes CRUD on the `trains` table via Javalin on port 8080. It runs standalone (`mvn compile exec:java`), not with the JavaFX app.
3. **Preparing for GitHub release** - Packaging, documentation, and distribution

## Current Scope — Do Not Exceed It

**Allowed:**
- JavaFX GUI components and styling
- PostgreSQL persistence for trains only, via JDBC (`dao/`, `util/DBConnection.java`, `sql/`)
- The standalone Javalin REST API in `api/` (trains CRUD only)
- In-memory data using `List` / `Map` for everything that isn't trains
- Hardcoded seed data written directly in code
- A simple tick counter to simulate time passing
- Maven build configuration and packaging
- MSI installer creation via jpackage or WiX

**Not allowed, until the team explicitly says otherwise:**
- Databases or persistence for anything other than trains
- REST endpoints beyond the existing `/api/trains` CRUD
- Web-based interfaces or server components beyond the standalone Javalin API
- Real multithreading or synchronization beyond the existing SimulationClock
- Adding new train lines or stations not already in SeedData (unless refactoring the map)

If a task seems to require one of these, stop and flag it instead of implementing it.

## Where Code Goes

Place new code based on what it *does*, not what feature it for. Each folder has one job:

- **model** — Data classes that represent real-world things in the system (a train, a station, a passenger, etc.). These classes hold data and basic behavior about themselves. They must not interact with JavaFX components or JDBC directly.
- **service** — Logic that operates on model classes to answer a question or perform a calculation (e.g. finding a route, calculating a fare). No JavaFX or JDBC code here either.
- **dao** — Data access objects. All SQL/JDBC for a table lives in one DAO (`TrainDAO` for `trains`). Methods open their own connection via `DBConnection.getConnection()` inside try-with-resources and wrap `SQLException` in `DataAccessException`.
- **api** — The standalone Javalin REST API (`TransitApi`). Thin layer: parse/validate JSON, call the DAO, map errors to HTTP status codes. Never put SQL here.
- **simulation** — Code that advances the system over time (the tick counter and whatever reacts to each tick). The SimulationClock runs on its own daemon thread and includes event logging with automatic log rotation (max 200 entries). It provides synchronized admin operations: delayTrain, delayAllTrains, setSuspended, isSuspended, and read-only accessors: getEventLog, getStatusReport, getTickCount.
- **exceptions** — Custom error types for expected failure cases (e.g. an invalid station name, no route found, database failures via `DataAccessException`).
- **util** — Shared helper code, such as seed data setup (`SeedData`), input validation (`Validator`), or infrastructure like `DBConnection` (the single place that knows the JDBC URL and credentials).
- **ui** — All JavaFX UI code. Organized by screen:
  - `ui/StartMenu.java` — The main entry screen
  - `ui/passenger/` — Passenger-facing screens (PassengerScreen, RoutesAndFare, TrainMap)
  - `ui/admin/` — Admin-facing screens (AdminScreen, Controls, EventLog)
  - `ui/common/` — Shared UI components (TrainLiveStatus)
- **src/main/resources** — Static assets (CSS, fonts, images)

Before creating a new file, check if an existing file already does this job. Add to it instead of duplicating logic.

## Admin Implementation Priorities

The admin interface is implemented. Current state:

1. **Controls Tab** (`ui/admin/Controls.java`) - **Complete**
   - Delay train, clear delay, network disruption (custom + 5/10-min presets), service toggle — all wired to `SimulationClock`
   - Add/remove train sections write to Postgres via `TrainDAO`; changes apply on next app start since trains load once at startup
   - Network overview summary refreshes every second via Timeline

2. **Event Log Tab** (`ui/admin/EventLog.java`) - **Complete**
   - Populates from `clock.getEventLog()`, auto-refreshes via Timeline
   - Supports event-type and text filters, plus CSV export of filtered entries

3. **Train Status Tab** (`ui/common/TrainLiveStatus.java`) - **Complete**
   - Table updates in real-time every second via Timeline
   - Thread-safe reading of train state (synchronized on clock)
   - Displays train ID, type, status, current station, and delay
   - Future enhancements: color coding for train status, sorting capabilities

4. **Possible future work** (only if requested)
   - Extend the API/DAO beyond the `trains` table (stations, lines) — requires schema changes in `sql/`

## CSS & Styling Guidelines

The project uses a retro neon pixel-art theme defined in `src/main/resources/pixel.css`. When styling:

- **Use the existing color palette**:
  - Background: `#14142b` (dark purple)
  - Accent: `#00e5ff` (neon blue)
  - Secondary: `#ff3ea5` (neon pink)
  - Text: `#f2f2ff` (light retro text)
  - Controls: `#2a2a5a` (dark purple for buttons/inputs)
  - Control inner background: `#1e1e44` (pale purple for tables/inputs)

- **Font**: Use "Pixel Operator" font (loaded in Main.java) for the retro aesthetic

- **Consistency**: Match existing CSS patterns for similar components (buttons, tables, tabs, scrollbars)

- **No inline styles**: Prefer adding classes to `pixel.css` over inline `-fx-style` strings in Java code

- **Existing CSS classes**:
  - `.title` - Large title text (48px, neon blue with pink shadow)
  - `.subtitle` - Smaller subtitle text (24px, light purple)
  - `.button` - Button styling with hover and pressed states
  - `.text-input` - Text field styling with focus state
  - `.heading` - Medium heading text (32px, neon blue)
  - `.error-label` - Error text color (neon pink)
  - `.table-row-cell:empty` - Empty table row styling
  - `.tab:selected` - Selected tab styling
  - `.scroll-bar` - Custom scrollbar styling

## Train Map Implementation

The train map is already implemented in `ui/Passenger/TrainMap.java`. It displays:

- Stations as retro square nodes with labels
- Track connections as colored lines
- Uses the neon color palette consistent with the rest of the app

**Map improvements to consider** (if requested):
- Dynamic positioning based on actual station coordinates
- Train indicators showing current train positions on the map
- Zoom/pan functionality for larger networks
- Legend for line colors

## Release Preparation

When preparing for a GitHub release:

1. **Version Management**
   - Update `<version>` in `pom.xml` from `1.0-SNAPSHOT` to a release version (e.g., `1.0.0`)
   - Create a Git tag matching the version (e.g., `v1.0.0`)

2. **Build Artifacts**
   - Run `mvn clean package` to create the shaded JAR
   - Optionally create an MSI installer using jpackage (see README.md for commands)

3. **Documentation**
   - Ensure README.md is up-to-date with installation and usage instructions
   - Add a CHANGELOG.md if tracking changes
   - Verify all external links and references are correct

4. **Quality Checks**
   - Run the application and verify all features work
   - Check that the admin controls affect the simulation correctly
   - Verify the train map renders correctly
   - Test on different screen sizes/resolutions

5. **GitHub Release**
   - Merge the release changes into protected `master` through a Pull Request
   - Create and push a version tag on the merged `master` commit (for example, `v1.0.0`)
   - Java CI runs on the Pull Request and merge push to `master`; after those checks pass, push the version tag to trigger `.github/workflows/release.yml`
   - The release workflow verifies the tag points to `master`, then builds the shaded JAR, creates the MSI with `jpackage`, and attaches it to the GitHub release
   - Write or review release notes describing what's included

## Environment Configuration

The optional PostgreSQL settings are documented in `env.example`:

- `TRANSITIQ_DB_URL`
- `TRANSITIQ_DB_USER`
- `TRANSITIQ_DB_PASSWORD`

Copy the example values into the local environment when overriding the defaults. Never commit
database credentials.

## Code Style Rules

- Keep every class focused on one clear responsibility.
- Keep all fields `private`.
- Use full, descriptive names — no abbreviations that aren't obvious.
- Prefer returning early over nesting `if` statements deeply.
- Keep methods short. If a method is doing several distinct things, split it.
- Use `enum` for any value that represents a fixed set of states (e.g. a train's status).
- Validate input and handle expected errors with the custom exceptions — don't let invalid input crash the program.
- Never put JavaFX component code inside `model` or `service` classes.
- In UI classes, separate layout logic from business logic where possible.

## Comments

- Only comment on things that aren't obvious from reading the code.
- Explain *why* a decision was made, not what a line of code does.
- Do not add a comment to every line or to simple, self-explanatory statements.
- If you change code that has a comment explaining it, update the comment too.

Good:
```java
// Express trains skip intermediate stations when updating their position.
```

Not needed:
```java
// Increment station index
stationIndex++;
```

## Object-Oriented Concepts

Use these where they naturally fit the problem. Do not force them in just to demonstrate a concept:

- Encapsulation (private fields, controlled access)
- Inheritance — `Train` as a base class, with `LocalTrain` and `ExpressTrain` as specific types
- Polymorphism — code that works with a general `Train` reference, regardless of which specific type it is
- Interfaces — only where genuinely useful (e.g. a shared "trackable" or "notifiable" behavior)
- Collections (`List`, `Map`) for storing groups of trains, stations, etc.
- Custom exceptions for expected error cases

## Rules for Making Changes

Treat this as a shared codebase other people also work on. Before writing code:

1. Touch only the files needed for the task you were given.
2. Do not refactor, rename, or restructure code you weren't asked to change.
3. Do not replace someone else's working implementation with a different approach just because you'd write it differently.
4. Do not invent new features, classes, or requirements that weren't asked for.
5. If you believe a change outside the requested scope is genuinely necessary, explain why before making it — do not make it silently.
6. Match the existing style and structure of the surrounding code.
7. Keep changes small enough that a person could review them in one sitting.

## Git Workflow

- Never commit directly to `master`. The protected branch accepts changes only through a Pull Request.
- Create a new branch for each feature, named after the feature (e.g. `route-planner`, `fare-calculator`).
- Keep one feature per branch — don't mix unrelated changes together.
- Create release tags only after the release changes have been merged into `master`.
- Before a PR is ready: the code compiles, existing features still work, only relevant files were changed, and the scope rules above were followed.

## The One Rule That Matters Most

If you are ever unsure whether something is in scope, allowed, or the right file to change — ask, instead of assuming. Guessing wrong here creates work for the whole team.