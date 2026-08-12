package com.finops.cloudcost;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic cloudMetricsTopic() {
        // This forces Spring Boot to automatically create the channel inside Docker Kafka on startup
        return TopicBuilder.name("cloud-metrics")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
