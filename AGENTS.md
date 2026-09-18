# TransitIQ: AGENTS.md

This file tells an AI agent how to work on this codebase. Follow it exactly. If something you're asked to do conflicts with this file, stop and point out the conflict instead of guessing.

## What This Project Is

TransitIQ is a Java OOP mini-project simulating a transit info system (inspired by M-Indicator/Chalo). It runs entirely in the terminal, with no database and no network calls.

## Current Scope — Do Not Exceed It

Only build what's listed below. Do not add anything from the "Not Allowed" list, even if it seems like a natural improvement.

**Allowed:**
- Terminal input/output using `Scanner`
- In-memory data using `List` / `Map`
- Hardcoded seed data written directly in code
- A simple tick counter to simulate time passing

**Not allowed, until the team explicitly says otherwise:**
- Databases or persistence (e.g. MySQL, JDBC)
- REST APIs or any network calls
- A graphical interface (JavaFX, Swing, web UI)
- Real multithreading or synchronization

If a task seems to require one of these, stop and flag it instead of implementing it.

## Where Code Goes

Place new code based on what it *does*, not what feature it's for. Each folder has one job:

- **model** — Data classes that represent real-world things in the system (a train, a station, a passenger, etc.). These classes hold data and basic behavior about themselves. They must not read terminal input or print output.
- **service** — Logic that operates on model classes to answer a question or perform a calculation (e.g. finding a route, calculating a fare). No terminal input or output here either.
- **simulation** — Code that advances the system over time (the tick counter and whatever reacts to each tick).
- **exception** — Custom error types for expected failure cases (e.g. an invalid station name, no route found).
- **util** — Shared helper code, such as seed data setup or input validation, that doesn't belong to one specific class.
- **Main** — The entry point. This is the only place that should directly use `Scanner` and print menus to the terminal.

Before creating a new file, check if an existing file already does this job. Add to it instead of duplicating logic.

## Code Style Rules

- Keep every class focused on one clear responsibility.
- Keep all fields `private`.
- Use full, descriptive names — no abbreviations that aren't obvious.
- Prefer returning early over nesting `if` statements deeply.
- Keep methods short. If a method is doing several distinct things, split it.
- Use `enum` for any value that represents a fixed set of states (e.g. a train's status).
- Validate input and handle expected errors with the custom exceptions — don't let invalid input crash the program.
- Never put `Scanner` calls or `System.out.print` statements inside `model` or `service` classes.

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

- Never commit directly to `main`. All changes go through a Pull Request.
- Create a new branch for each feature, named after the feature (e.g. `route-planner`, `fare-calculator`).
- Keep one feature per branch — don't mix unrelated changes together.
- Before a PR is ready: the code compiles, existing features still work, only relevant files were changed, and the scope rules above were followed.

## The One Rule That Matters Most

If you are ever unsure whether something is in scope, allowed, or the right file to change — ask, instead of assuming. Guessing wrong here creates work for the whole team.