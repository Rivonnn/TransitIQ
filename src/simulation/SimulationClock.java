package simulation;

import model.Train;
import model.TrainStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * The simulation engine. On every tick (one simulated minute) it loops through
 * the trains and advances their state:
 *   - a delayed train is held for the tick and its delay drops by 1
 *   - WAITING -> departs
 *   - MOVING  -> moves one station; arrives when it reaches the last station
 *   - ARRIVED -> turns around: back to station 0, WAITING
 *
 * Runs on its own thread. The admin menu changes trains from another thread,
 * so every method that touches train state is synchronized on this clock.
 */
public class SimulationClock implements Runnable {

    private static final int MAX_LOG_ENTRIES = 200;

    private final List<Train> trains;
    private final int lastStationIndex;
    private final long tickMillis;
    private final List<String> eventLog = new ArrayList<>();

    private volatile boolean running = false;
    private boolean suspended = false;
    private int tickCount = 0;
    private Thread thread;

    /**
     * @param trains           the shared list of trains to simulate
     * @param lastStationIndex index of the final station (trains arrive here)
     * @param tickMillis       real-time milliseconds per tick
     */
    public SimulationClock(List<Train> trains, int lastStationIndex, long tickMillis) {
        this.trains = trains;
        this.lastStationIndex = lastStationIndex;
        this.tickMillis = tickMillis;
    }

    // ---------- thread control ----------

    public synchronized void start() {
        if (running) {
            return;
        }
        running = true;
        thread = new Thread(this, "simulation-clock");
        thread.setDaemon(true); // does not keep the JVM alive after the menus exit
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        if (thread != null) {
            thread.interrupt();
        }
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        while (running) {
            tick();
            try {
                Thread.sleep(tickMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    // ---------- the tick ----------

    public synchronized void tick() {
        tickCount++;
        if (suspended) {
            return;
        }
        for (Train train : trains) {
            if (train.getDelayMinutes() > 0) {
                train.setDelayMinutes(train.getDelayMinutes() - 1);
                if (train.getDelayMinutes() == 0) {
                    log(train.getId() + " delay cleared");
                }
                continue;
            }
            switch (train.getStatus()) {
                case WAITING:
                    train.depart();
                    log(train.getId() + " departed");
                    break;
                case MOVING:
                    train.updatePosition();
                    if (train.getCurrentStationIndex() >= lastStationIndex) {
                        train.arrive();
                        log(train.getId() + " arrived at final station");
                    }
                    break;
                case ARRIVED:
                    train.setCurrentStationIndex(0);
                    train.setStatus(TrainStatus.WAITING);
                    log(train.getId() + " turned around, waiting at station 0");
                    break;
            }
        }
    }

    // ---------- admin operations ----------

    /** Adds delay to one train. Returns false if no train has that id. */
    public synchronized boolean delayTrain(String trainId, int minutes) {
        for (Train train : trains) {
            if (train.getId().equalsIgnoreCase(trainId)) {
                train.setDelayMinutes(train.getDelayMinutes() + minutes);
                log("ADMIN: " + train.getId() + " delayed by " + minutes + " min");
                return true;
            }
        }
        return false;
    }

    /** Disruption: adds delay to every train. */
    public synchronized void delayAllTrains(int minutes) {
        for (Train train : trains) {
            train.setDelayMinutes(train.getDelayMinutes() + minutes);
        }
        log("ADMIN: network disruption, all trains delayed by " + minutes + " min");
    }

    /** Disruption: freezes (or resumes) every train. */
    public synchronized void setSuspended(boolean suspended) {
        this.suspended = suspended;
        log("ADMIN: service " + (suspended ? "SUSPENDED" : "RESUMED"));
    }

    public synchronized boolean isSuspended() {
        return suspended;
    }

    // ---------- read-only views (safe to call from other threads) ----------

    public synchronized int getTickCount() {
        return tickCount;
    }

    public synchronized List<String> getStatusReport() {
        List<String> lines = new ArrayList<>();
        for (Train t : trains) {
            lines.add(t.getId() + "  status=" + t.getStatus()
                    + "  station index=" + t.getCurrentStationIndex()
                    + "  delay=" + t.getDelayMinutes() + " min");
        }
        return lines;
    }

    public synchronized List<String> getEventLog() {
        return new ArrayList<>(eventLog);
    }

    private void log(String message) {
        eventLog.add("[tick " + tickCount + "] " + message);
        if (eventLog.size() > MAX_LOG_ENTRIES) {
            eventLog.remove(0);
        }
    }
}
