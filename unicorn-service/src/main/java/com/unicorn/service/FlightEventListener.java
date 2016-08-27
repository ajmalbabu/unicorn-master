package com.unicorn.service;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Properties;

@Service
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "kafka.flight.event")
public class FlightEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlightEventListener.class);

    @Value("${kafka.bootstrap.servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${kafka.flight.event.topic:flightEventTopic}")
    private String flightEventTopic;

    @Value("${kafka.flight.event.partition:0}")
    private int flightEventTopicPartition;

    @Value("${kafka.flight.event.pollIntervalMs:0}")
    private long pollIntervalMs;

    @Value("${global.shutDownHook:false}")
    private boolean shutDownHook;

    private Properties consumerConfig = new Properties();

    private KafkaConsumer<String, String> kafkaConsumer;

    public Properties getConsumerConfig() {
        return consumerConfig;
    }

    public void setConsumerConfig(Properties consumerConfig) {
        this.consumerConfig = consumerConfig;
    }

    @PostConstruct
    public void postConstruct() throws InterruptedException {


        consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        kafkaConsumer = new KafkaConsumer<String, String>(consumerConfig);

        // Subscribe to all partition of topic. use 'assign' to subscribe to specific partition.
        kafkaConsumer.subscribe(Arrays.asList(flightEventTopic));

        registerListener(kafkaConsumer);
    }

    private void registerListener(final KafkaConsumer<String, String> consumer) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                listen(consumer);
            }
        }).start();

    }

    private void listen(KafkaConsumer<String, String> consumer) {

        while (!shutDownHook) {

            ConsumerRecords<String, String> records = consumer.poll(pollIntervalMs);

            for (ConsumerRecord<String, String> record : records) {

                LOGGER.info("Received flight event key: {}, offset: {}, message: {}"
                        , record.key(), record.offset(), record.value());
            }

        }
    }
}

