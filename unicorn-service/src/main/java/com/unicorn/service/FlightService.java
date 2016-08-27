package com.unicorn.service;


import com.unicorn.service.dao.FlightDao;
import com.unicorn.service.domain.Flight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightService {

    @Autowired
    private FlightDao flightDao;

    public List<Flight> findAllFlights() {
        return flightDao.findAllFlights();
    }
}
