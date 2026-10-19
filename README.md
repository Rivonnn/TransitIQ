# TransitIQ

A Java OOP mini-project simulating a transit information system (inspired by M-Indicator/Chalo). Built with JavaFX for a
retro-styled graphical interface, with no database or network calls.

## Features

### Passenger Features

- Route planning between stations
- Fare calculation for different train types (Local, Express)
- Station validation and error handling
- Interactive train map visualization

### Admin Features

- Train delay management (delay individual trains) - UI built, implementation pending
- Network disruption handling (delay all trains simultaneously) - UI built, implementation pending
- Service suspension/resume functionality - UI built, implementation pending
- Real-time train status monitoring (with automatic refresh every second)
- Event log tracking and viewing (auto-refresh pending)

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
├── model/                 # Data classes (Train, Station, Line, TrainStatus, etc.)
├── service/               # Business logic (RoutePlanner, FareCalculator, PassengerService)
├── simulation/            # Time simulation (SimulationClock)
├── exception/             # Custom exceptions (InvalidStationException, NoRouteFoundException)
├── util/                  # Helpers (SeedData, Validator)
└── ui/                    # JavaFX screens
    ├── StartMenu.java
    ├── Passenger/
    │   ├── PassengerScreen.java
    │   ├── RoutesAndFare.java
    │   └── TrainMap.java
    ├── Admin/
    │   ├── AdminMenu.java
    │   ├── AdminScreen.java
    │   ├── Controls.java
    │   └── EventLog.java
    └── common/
        └── TrainLiveStatus.java

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
- JavaFX is included as a Maven dependency

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

### Option 3: Run the JAR Directly

After building (see below), run:

```powershell
java -jar target/TransitIQ-1.0-SNAPSHOT.jar
```

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

- **Controls tab**: Delay individual trains, trigger network disruptions, suspend/resume service
- **Train Status tab**: See current status, position, and delays for all trains in real-time
- **Event Log tab**: Review recent simulation events and admin actions

## Scope Notes

- JavaFX GUI (not terminal-based)
- In-memory data with hardcoded seed data
- Simple tick counter for time simulation
- No database, persistence, REST APIs, or web server
- Retro pixel-art styling with neon color palette
- Admin interface is structured but has TODOs remaining in Controls and EventLog tabs

## Contributing

Please review [CONTRIBUTING.md](CONTRIBUTING.md) for detailed guidelines on the development workflow, coding standards, Spotless formatting, and SpotBugs linting.
