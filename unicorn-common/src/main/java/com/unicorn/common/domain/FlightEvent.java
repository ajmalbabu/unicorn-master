package com.unicorn.common.domain;

public class FlightEvent {
    private String flightKey;
    private String flightMessage;

    public FlightEvent() {
    }

    public FlightEvent(String flightKey, String flightMessage) {
        this.flightKey = flightKey;
        this.flightMessage = flightMessage;
    }

    public String getFlightKey() {
        return flightKey;
    }

    public String getFlightMessage() {
        return flightMessage;
    }

    public void setFlightKey(String flightKey) {
        this.flightKey = flightKey;
    }

    public void setFlightMessage(String flightMessage) {
        this.flightMessage = flightMessage;
    }

    @Override
    public String toString() {
        return "FlightEvent{" +
                "flightKey='" + flightKey + '\'' +
                ", flightMessage='" + flightMessage + '\'' +
                '}';
    }
}
