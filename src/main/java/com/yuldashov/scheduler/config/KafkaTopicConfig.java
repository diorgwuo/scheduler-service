package com.yuldashov.scheduler.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        return new KafkaAdmin(config);
    }

    @Bean
    public NewTopic summarizationReply() {
        return TopicBuilder.name("summarization-reply")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic summarizationRequest() {
        return TopicBuilder.name("summarization-request")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
