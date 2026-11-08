# TransitIQ

A Java OOP mini-project simulating a transit information system (inspired by M-Indicator/Chalo). Built with JavaFX for a
retro-styled graphical interface, backed by a Postgres database and a standalone Javalin REST API.

## Features

### Passenger Features

- Route planning between stations
- Fare calculation for different train types (Local, Express)
- Station validation and error handling
- Interactive train map visualization

### Admin Features

- Individual train delays with train ID lookup, positive-minute validation, and action feedback
- Network disruptions that delay every train, with custom durations or 5- and 10-minute presets
- Service suspension and resumption controls
- Adding and removing trains in the database from the UI (takes effect on next app start)
- Manual clearing of an individual train's active delay
- A live network overview showing simulation tick, train states, delayed trains, and service status
- Real-time train status monitoring with automatic refresh every second
- An event log that refreshes automatically, supports event-type and text filters, and exports the filtered entries to CSV

### API Features

- REST API over the `trains` table with full CRUD, served by Javalin on port 8080
- JSON request/response bodies, validated input, and consistent error responses
- Trains are loaded from Postgres at startup, falling back to built-in seed data if the database is unreachable

### System Features

- Train simulation with status tracking (WAITING, MOVING, ARRIVED)
- Support for Local and Express train types
- Multi-line transit system (Western, Central, Harbour)
- Real-time simulation clock with configurable tick intervals
- Retro pixel-art styled UI with neon color palette

## Architecture

```
src/
├── Main.java              # JavaFX Application entry point
├── Launcher.java          # Simple launcher for packaging
├── api/
│   └── TransitApi.java    # Javalin REST API (runs standalone on port 8080)
├── dao/
│   └── TrainDAO.java      # JDBC data access for the trains table
├── model/                 # Data classes (Train, Station, Line, TrainStatus, etc.)
├── service/               # Business logic (RoutePlanner, FareCalculator, PassengerService)
├── simulation/            # Time simulation (SimulationClock)
├── exceptions/            # Custom exceptions (InvalidStationException, NoRouteFoundException, DataAccessException)
├── util/                  # Helpers (SeedData, Validator, DBConnection)
└── ui/                    # JavaFX screens
    ├── StartMenu.java
    ├── passenger/
    │   ├── PassengerScreen.java
    │   ├── RoutesAndFare.java
    │   └── TrainMap.java
    ├── admin/
    │   ├── AdminScreen.java
    │   ├── Controls.java
    │   └── EventLog.java
    └── common/
        └── TrainLiveStatus.java

sql/
├── schema.sql             # Postgres schema (trains table)
└── seed.sql               # Default seed rows (L101, E201)

docs/
└── api/
    └── README.md          # REST API endpoint reference

src/main/resources/
├── pixel.css              # Retro neon pixel-art styling
├── PixelOperator-Bold.ttf # Retro pixel font
├── TransitIQ.ico          # Windows icon for installer
└── TransitIQ.png          # Application icon
```

## OOP Concepts Used

- **Encapsulation**: Private fields with controlled access
- **Inheritance**: `Train` base class with `LocalTrain` and `ExpressTrain` subclasses
- **Polymorphism**: Generic `Train` references
- **Custom Exceptions**: Expected error cases (e.g., `NoRouteFoundException`)

## Prerequisites

- **Java 25** or higher (configured in pom.xml)
- **Maven 3.6+** for building and packaging
- **PostgreSQL** (optional but recommended) for train persistence — the app falls back to built-in seed data if it's unreachable
- JavaFX, the PostgreSQL JDBC driver, Javalin, and Jackson are included as Maven dependencies

## Installation & Setup

### 1. Clone the Repository

```powershell
git clone https://github.com/Rivonnn/TransitIQ
cd TransitIQ
```

### 2. Verify Java Version

```powershell
java -version
```

Ensure you have Java 25 or higher installed.

### 3. Install Dependencies

Maven will automatically download JavaFX dependencies when you build:

```powershell
mvn clean install
```

### 4. Set Up the Database (Optional)

Trains are persisted in PostgreSQL. If you skip this step, the app still runs but uses built-in seed
trains (L101, E201) and admin add/remove-train actions will fail.

1. Create a database named `transitiq` in your Postgres instance.
2. Run `sql/schema.sql`, then `sql/seed.sql`, in that database (e.g. via pgAdmin's Query Tool or
   `psql -d transitiq -f sql/schema.sql`).
3. Connection settings default to `jdbc:postgresql://localhost:5432/transitiq` as user `postgres`
   with an empty password. Override with environment variables:

   | Variable                 | Default                              |
   | ------------------------ | ------------------------------------ |
   | `TRANSITIQ_DB_URL`       | `jdbc:postgresql://localhost:5432/transitiq` |
   | `TRANSITIQ_DB_USER`      | `postgres`                           |
   | `TRANSITIQ_DB_PASSWORD`  | *(empty)*                            |

   Use `env.example` as a template when setting these variables in your shell or local
   environment configuration. Replace any values as needed; the application does not load
   `.env` files automatically. Do not commit credentials.

## Running the Application

### Option 1: Using Maven

```powershell
mvn javafx:run
```

### Option 2: Using IntelliJ IDEA

1. **Open the project**
    - File → Open → Select the `TransitIQ` project directory

2. **Configure JDK (if needed)**
    - File → Project Structure → Project → Set Project SDK to JDK 25 or higher

3. **Run the application**
    - Right-click on `src/main/java/Main.java`
    - Select "Run 'Main.main ()'"
    - Or press `Shift + F10` with Main.java open

4. **Optional: Create a run configuration**
    - Run → Edit Configurations
    - Click "+" → Application
    - Name: "TransitIQ"
    - Main class: "Main"
    - Working directory: `$PROJECT_DIR$`
    - Save and run using the configuration

### Using the Admin Controls

From the start screen, choose **Admin Menu** and use the **Controls**, **Train Status**, and **Event Log** tabs.

- **Delay a train:** enter a train ID and a positive delay in minutes. The seeded trains are **L101** and **E201**.
- **Clear a delay:** enter the train ID to remove its remaining delay immediately.
- **Add a train:** enter an ID, type (LOCAL/EXPRESS), speed, base fare, and stop indexes for express
  trains. Changes are written to the database and appear in the simulation after a restart.
- **Remove a train:** enter the train ID and confirm. Also takes effect on the next app start.
- **Network disruption:** enter a positive number of minutes to delay every train, or use the 5- or 10-minute preset.
- **Service:** use the button to suspend or resume train movement. The network overview updates every second.
- **Event Log:** choose an event category and/or search text to filter entries. **Export filtered CSV** saves the entries currently shown.

### Option 3: Run the JAR Directly

After building (see below), run:

```powershell
java -jar target/TransitIQ-1.0-SNAPSHOT.jar
```

### Running the REST API

The Javalin API is a standalone process — it does **not** start with the JavaFX app. It requires the
database to be set up first (see step 4 above).

```powershell
mvn compile exec:java
```

The server listens on `http://localhost:8080` until you stop it (Ctrl+C). Endpoints live under
`/api/trains` (GET/POST/PUT/DELETE). See [docs/api/README.md](docs/api/README.md) for the full
endpoint reference.

## Code Quality, Formatting & Linting

TransitIQ uses Maven plugins to enforce style consistency, clean imports, and perform static analysis:

### 1. Code Formatting (Spotless + Google Java Format)
- **Check formatting** (checks compliance without modifying files):
  ```powershell
  mvn spotless:check
  ```
- **Apply formatting** (automatically reformats all Java source files and removes unused imports):
  ```powershell
  mvn spotless:apply
  ```

### 2. Static Analysis & Code Linting (SpotBugs)
- **Run SpotBugs analysis**:
  ```powershell
  mvn spotbugs:check
  ```
- **Launch SpotBugs GUI** (inspect detailed bug patterns in an interactive window):
  ```powershell
  mvn spotbugs:gui
  ```

### 3. Compiler Warnings
- **Compile with all lint warnings enabled (`-Xlint:all`)**:
  ```powershell
  mvn clean compile
  ```

### 4. Full Verification Pipeline
Run formatting checks, compiler linting, SpotBugs analysis, and shaded packaging all together:
```powershell
mvn clean verify
```

**Note**: The Maven Shade plugin is configured to exclude duplicate signature and license files to prevent warnings during the build process.

## Building for Distribution

### Build a Fat JAR (Single Executable)

```powershell
mvn clean package
```

This creates a shaded JAR at `target/TransitIQ-1.0-SNAPSHOT.jar` with all dependencies bundled. The Maven Shade plugin is configured to exclude duplicate signature and license files to prevent warnings.

### Create an MSI Installer

To create a Windows MSI installer, you'll need to use **jpackage** (available in JDK 14+). Here's how to use it:

#### 1. Build the Application

```powershell
mvn clean package
```

#### 2. Create MSI with jpackage (Simple Method)

```powershell
jpackage `
  --name TransitIQ `
  --type msi `
  --input target `
  --main-jar TransitIQ-1.0-SNAPSHOT.jar `
  --main-class Launcher `
  --dest installers `
  --icon src/main/resources/TransitIQ.ico `
  --app-version 1.0 `
  --vendor "TransitIQ Team" `
  --description "Transit Information System"
```

This will create an MSI installer in the `installers/` directory.

## Application Usage

The application starts with a main menu where you can choose between:

1. **Passenger Menu** - Search routes, check fares, view train map
2. **Admin Menu** - Manage train delays, view status, and control service
3. **Exit** - Shut down the application

### Passenger Menu Usage

- **Search route**: Enter source and destination station names to see the full route
- **Check fare**: Enter stations and train type (local/express) to calculate fare
- **Train Map**: View the visual network map with stations and track connections

### Admin Menu Usage

- **Controls tab**: Delay individual trains, trigger network disruptions, suspend/resume service,
  and add/remove trains in the database
- **Train Status tab**: See current status, position, and delays for all trains in real-time
- **Event Log tab**: Review recent simulation events and admin actions

## Scope Notes

- JavaFX GUI plus a standalone Javalin REST API
- Trains persisted in PostgreSQL via JDBC; stations, lines, and routes remain hardcoded seed data
- Simple tick counter for time simulation
- No REST endpoints beyond the trains CRUD API, and no web-based UI
- Retro pixel-art styling with neon color palette

## Contributing

Please review [CONTRIBUTING.md](CONTRIBUTING.md) for detailed guidelines on the development workflow, coding standards, Spotless formatting, and SpotBugs linting.
