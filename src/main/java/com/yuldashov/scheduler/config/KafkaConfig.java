package com.yuldashov.scheduler.config;

import com.yuldashov.scheduler.dto.mail.EmailMessage;
import com.yuldashov.scheduler.dto.summarization.SummarizationRequest;
import com.yuldashov.scheduler.dto.summarization.SummarizationResponse;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value("${bootstrap.server}")
    String bootstrapServer;
    @Value("${group.id}")
    private String groupId;
    @Value("${trusted.packages}")
    private String trustedPackages;
    @Value("${response.type}")
    private String responseType;

    @Bean
    public ProducerFactory<String, EmailMessage> emailProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean(name = "emailKafkaTemplate")
    public KafkaTemplate<String, EmailMessage> emailKafkaTemplate() {
        return new KafkaTemplate<>(emailProducerFactory());
    }


    @Bean
    public ProducerFactory<String, SummarizationRequest> requestProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean(name = "requestKafkaTemplate")
    public KafkaTemplate<String,SummarizationRequest> requestKafkaTemplate() {
        return new KafkaTemplate<>(requestProducerFactory());
    }

    @Bean
    public ConsumerFactory<String, SummarizationResponse> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, trustedPackages);
        config.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        config.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE,
                responseType);
        return new DefaultKafkaConsumerFactory<>(config);
    }
}
