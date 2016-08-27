package com.unicorn.service;

import com.unicorn.service.dao.FlightDao;
import com.unicorn.service.domain.Flight;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * This test shows how to access Cassandra layer using Mockito. cassandra-unit-test library
 * is not an active project any more, so mock all Dao layer code with Mockito.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringConfig.class)
public class FlightServiceTest {


    @Mock
    private FlightDao flightDao;

    @InjectMocks
    @Autowired
    private FlightService flightService;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(flightDao.findAllFlights()).thenReturn(asList(new Flight("200-2016:01:01:08:00:12:00-DAL-NYC", asList("WERT34", "TRTYY5"))));
    }


    @Test
    public void findAllFlights() throws Exception {

        // Given, When
        List<Flight> flights = flightService.findAllFlights();

        // Then
        assertThat(flights.size()).isEqualTo(1);
        assertThat(flights.get(0).getFlightKey()).isEqualTo("200-2016:01:01:08:00:12:00-DAL-NYC");
    }


}
