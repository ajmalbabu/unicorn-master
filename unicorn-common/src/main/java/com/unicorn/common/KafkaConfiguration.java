package com.unicorn.common;

//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.annotation.EnableKafka;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.*;
//import org.springframework.kafka.listener.AbstractMessageListenerContainer;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//@EnableKafka
public class KafkaConfiguration {
//
//    @Value("${kafka.host:localhost}")
//    private String kafkaHost;
//
//    @Value("${kafka.port:9042}")
//    private int kafkaPort;
//
//    @Bean
//    public ProducerFactory producerFactory(Class keySerializer, Class valueSerializer) {
//        return new DefaultKafkaProducerFactory<>(producerConfigs(keySerializer, valueSerializer));
//    }
//
//    @Bean
//    public Map<String, Object> producerConfigs(Class keySerializer,
//                                               Class valueSerializer) {
//
//        Map<String, Object> props = new HashMap<>();
//
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHostPort());
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
//
//        return props;
//    }
//
//    private String kafkaHostPort() {
//        return kafkaHost + ":" + kafkaPort;
//    }
//
//    @Bean
//    @Qualifier(value = "flightEventKafkaTemplate")
//    public KafkaTemplate<String, String> flightEventKafkaTemplate() {
//        return new KafkaTemplate<String, String>(producerFactory(StringSerializer.class, StringSerializer.class));
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
//
//        ConcurrentKafkaListenerContainerFactory<String, String> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory(StringDeserializer.class, StringDeserializer.class));
//        factory.getContainerProperties().setAckMode(AbstractMessageListenerContainer.AckMode.MANUAL_IMMEDIATE);
//        return factory;
//    }
//
//    @Bean
//    public ConsumerFactory<String, String> consumerFactory(Class keyDeserializer,
//                                                           Class valueDeserializer) {
//        return new DefaultKafkaConsumerFactory<>(consumerConfigs(keyDeserializer, valueDeserializer));
//    }
//
//    @Bean
//    public Map<String, Object> consumerConfigs(Class keyDeserializer,
//                                               Class valueDeserializer) {
//
//        Map<String, Object> props = new HashMap<>();
//
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHostPort());
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
//
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group1");
//        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
//
//
//        return props;
//    }

}
