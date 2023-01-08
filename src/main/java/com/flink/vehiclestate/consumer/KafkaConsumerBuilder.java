package com.flink.vehiclestate.consumer;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.*;

public class KafkaConsumerBuilder {

    public static KafkaSource<String> buildConsumer(ParameterTool parameterTool) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "node01:9092,node02:9092,node03:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "vehicle_state_job");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 5000);

        properties.putAll(parameterTool.getProperties());

        List<String> topics = Collections.singletonList("vehicle_heartbeat");

        return KafkaSource.<String>builder()
            .setProperties(properties)
            .setTopics(topics)
            .setValueOnlyDeserializer(new SimpleStringSchema())
            .build();
    }
}
