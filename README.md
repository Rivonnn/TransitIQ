# TransitIQ

A Java OOP mini-project simulating a transit information system (inspired by M-Indicator/Chalo). Runs entirely in the terminal with no database or network calls.

## Features

- Route planning between stations
- Fare calculation for different train types
- Train simulation with status tracking (WAITING, MOVING, ARRIVED)
- Support for Local and Express train types
- Multi-line transit system (Western, Central, Harbour)

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

```bash
javac src/*.java src/**/*.java
java src/Main
```

## Scope Notes

- Terminal input/output only (using `Scanner`)
- In-memory data with hardcoded seed data
- Simple tick counter for time simulation
- No database, persistence, REST APIs, or graphical interface
