package model;

/**
 * Base class representing a train in the transit system.
 * Holds the train's state (WAITING, MOVING, ARRIVED) and core attributes
 * like speed, fare, and position. Subclasses define specific stop behavior.
 */
public class Train {
    private final String id;
    private final int speed;
    private TrainStatus status;
    private int currentStationIndex;
    private int delayMinutes;
    private double baseFare;

    /**
     * Constructs a new train with the given attributes.
     * Trains start in WAITING state at station index 0 with no delay.
     */
    public Train(String id, int speed, double baseFare) {
        this.id = id;
        this.speed = speed;
        this.baseFare = baseFare;
        this.status = TrainStatus.WAITING;
        this.currentStationIndex = 0;
        this.delayMinutes = 0;
    }

    public String getId() {
        return id;
    }

    public int getSpeed() {
        return speed;
    }

    public TrainStatus getStatus() {
        return status;
    }

    public int getCurrentStationIndex() {
        return currentStationIndex;
    }

    public int getDelayMinutes() {
        return delayMinutes;
    }

    public double getBaseFare() {
        return baseFare;
    }

    public void setStatus(TrainStatus status) {
        this.status = status;
    }

    public void setCurrentStationIndex(int currentStationIndex) {
        this.currentStationIndex = currentStationIndex;
    }

    public void setDelayMinutes(int delayMinutes) {
        this.delayMinutes = delayMinutes;
    }

    public void setBaseFare(double baseFare) {
        this.baseFare = baseFare;
    }

    /**
     * Transitions the train from WAITING to MOVING state.
     * Only valid if the train is currently WAITING.
     */
    public void depart() {
        if (status == TrainStatus.WAITING) {
            status = TrainStatus.MOVING;
        }
    }

    /**
     * Transitions the train from MOVING to ARRIVED state.
     * Only valid if the train is currently MOVING.
     */
    public void arrive() {
        if (status == TrainStatus.MOVING) {
            status = TrainStatus.ARRIVED;
        }
    }

    /**
     * Advances the train to the next station when MOVING.
     * Subclasses override this to implement specific stop behavior
     * (e.g., express trains skipping intermediate stations).
     */
    public void updatePosition() {
        if (status == TrainStatus.MOVING) {
            currentStationIndex++;
        }
    }
}
