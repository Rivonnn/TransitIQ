# TransitIQ

A Java OOP mini-project simulating a transit information system (inspired by M-Indicator/Chalo). Runs entirely in the terminal with no database or network calls.

## Features

### Passenger Features
- Route planning between stations
- Fare calculation for different train types (Local, Express)
- Station validation and error handling

### Admin Features
- Train delay management (delay individual trains)
- Network disruption handling (delay all trains simultaneously)
- Service suspension/resume functionality
- Real-time train status monitoring
- Event log tracking and viewing

### System Features
- Train simulation with status tracking (WAITING, MOVING, ARRIVED)
- Support for Local and Express train types
- Multi-line transit system (Western, Central, Harbour)
- Real-time simulation clock with configurable tick intervals

## Architecture

```
src/
├── Main.java              # Entry point
├── model/                 # Data classes (Train, Station, Line, etc.)
├── service/               # Business logic (RoutePlanner, FareCalculator)
├── simulation/            # Time simulation (SimulationClock)
├── exception/             # Custom exceptions
├── util/                  # Helpers (SeedData, Validator)
└── ui/                    # Terminal menus (PassengerMenu, AdminMenu)
```

## OOP Concepts Used

- **Encapsulation**: Private fields with controlled access
- **Inheritance**: `Train` base class with `LocalTrain` and `ExpressTrain` subclasses
- **Polymorphism**: Generic `Train` references
- **Custom Exceptions**: Expected error cases (e.g., `NoRouteFoundException`)

## Running

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Terminal/Command Prompt

### Compilation and Execution

**From the project root directory:**

```bash
# Compile all Java files
javac -d bin src/**/*.java

# Run the application
java -cp bin Main
```

**Alternative (single command):**

```bash
# Compile and run in one step
javac -d bin src/**/*.java && java -cp bin Main
```

**For Windows (Command Prompt):**
```cmd
javac -d bin src\**\*.java
java -cp bin Main
```

**For Windows (PowerShell):**
```powershell
javac -d bin src/**/*.java
java -cp bin Main
```

The application starts with a main menu where you can choose between:
1. **Passenger Menu** - Search routes and check fares
2. **Admin Menu** - Manage train delays, view status, and control service
3. **Exit** - Shut down the application

### Passenger Menu Usage
- **Search route**: Enter source and destination station names to see the full route
- **Check fare**: Enter stations and train type (local/express) to calculate fare

### Admin Menu Usage
- **Delay a train**: Enter train ID and delay minutes to hold a specific train
- **Trigger network disruption**: Apply delay to all trains simultaneously
- **Suspend/resume service**: Freeze or resume all train operations
- **View train status**: See current status, position, and delays for all trains
- **View event log**: Review recent simulation events and admin actions

## Scope Notes

- Terminal input/output only (using `Scanner`)
- In-memory data with hardcoded seed data
- Simple tick counter for time simulation
- No database, persistence, REST APIs, or graphical interface
