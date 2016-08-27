package com.unicorn.service.domain;


import java.util.List;

public class Flight {

    private String flightKey;
    private List<String> pnrs;

    public Flight() {
    }

    public Flight(String flightKey, List<String> pnrs) {
        this.flightKey = flightKey;
        this.pnrs = pnrs;
    }

    public String getFlightKey() {
        return flightKey;
    }

    public void setFlightKey(String flightKey) {
        this.flightKey = flightKey;
    }

    public List<String> getPnrs() {
        return pnrs;
    }

    public void setPnrs(List<String> pnrs) {
        this.pnrs = pnrs;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "flightKey='" + flightKey + '\'' +
                ", pnrs=" + pnrs +
                '}';
    }
}
