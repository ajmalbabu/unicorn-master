package com.unicorn.service;

import com.unicorn.common.domain.FlightEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Properties;

/**
 * Publish flight events.
 */

@Service
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "kafka.flight.event")
public class FlightEventPublishService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlightEventPublishService.class);

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    @Value("${kafka.flight.event.topic:flightEventTopic}")
    private String flightEventTopic;

    @Value("${kafka.flight.event.partition:0}")
    private int flightEventTopicPartition;

    private Properties producerConfig = new Properties();

    private KafkaProducer<String, String> kafkaProducer;

    public Properties getProducerConfig() {
        return producerConfig;
    }

    public void setProducerConfig(Properties producerConfig) {
        this.producerConfig = producerConfig;
    }

    @PostConstruct
    public void postConstruct() {
        producerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        kafkaProducer = new KafkaProducer<String, String>(producerConfig);
    }

    public void publish(FlightEvent flightEvent) {

        LOGGER.info("Publish flight event: {} ", flightEvent);

        kafkaProducer.send(new ProducerRecord<String, String>(flightEventTopic, flightEventTopicPartition,
                flightEvent.getFlightKey(), flightEvent.getFlightMessage()));


    }


}
