package com.unicorn.api;

import com.unicorn.service.FlightService;
import com.unicorn.service.domain.Flight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Show cases how to integrate with cassandra and retrieve some value from column family and return the
 * result as a REST API.
 */
@RestController
@RequestMapping("/v1")
@Profile({"local", "unit-test", "dev", "qa", "prod"})
public class CassandraAccessShowcaseRestApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraAccessShowcaseRestApi.class);

    @Autowired
    private FlightService flightService;


    /**
     * Retrieve All flights by querying cassandra column family.
     */
    @RequestMapping(value = "flight", method = RequestMethod.GET)
    public ResponseEntity<List<Flight>> allFlights() throws Exception {

        LOGGER.info("Retrieve all flights.");

        return new ResponseEntity<List<Flight>>(flightService.findAllFlights(), HttpStatus.OK);
    }

}
