package com.unicorn.service.dao;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.DriverException;
import com.unicorn.common.CassandraConfiguration;
import com.unicorn.service.domain.Flight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.core.CqlOperations;
import org.springframework.cassandra.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FlightDao {

    @Autowired
    private CassandraConfiguration cassandraConfiguration;

    @Autowired
    private CqlOperations cqlOperations;


    @SuppressWarnings("unchecked")
    public List<Flight> findAllFlights() {
        List<Flight> flightList = new ArrayList<>();

        String query = String.format("select * from %s.%s;", cassandraConfiguration.getKeyspaceName(), "flight");
        flightList = cqlOperations.query(query, new FlightRowMapper());
        return flightList;
    }

    static class FlightRowMapper implements RowMapper {

        private static final String FLIGHT_KEY = "flightkey";
        private static final String PNRS = "pnrs";

        @Override
        public Flight mapRow(Row row, int i) throws DriverException {
            Flight flight = new Flight();
            flight.setFlightKey(row.getString(FLIGHT_KEY));
            flight.setPnrs(row.getList(PNRS, String.class));
            return flight;
        }
    }
}
