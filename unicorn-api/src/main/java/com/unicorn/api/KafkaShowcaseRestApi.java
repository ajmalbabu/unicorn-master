package com.unicorn.api;

import com.unicorn.common.domain.FlightEvent;
import com.unicorn.service.FlightEventPublishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Show cases how to integrate with Kafka by invoking a REST API
 * 1. Which will send a message to topic in Kafka Broker.
 * 2. And an internal lister listens for the send message.
 */
@RestController
@RequestMapping("/v1")
public class KafkaShowcaseRestApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaShowcaseRestApi.class);

    @Autowired
    private FlightEventPublishService flightEventPublishService;

    /**
     * Send the message to the Kafka service for publishing.
     */
    @RequestMapping(value = "flight", method = RequestMethod.POST)
    public ResponseEntity<String> submitFlightEvent(@RequestBody FlightEvent flightEvent) throws Exception {

        LOGGER.info("API received the flight event.");

        flightEventPublishService.publish(flightEvent);

        return new ResponseEntity<String>("Flight Event published.", HttpStatus.OK);
    }

}
