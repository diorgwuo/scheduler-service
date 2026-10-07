package com.yuldashov.scheduler.config;

import com.yuldashov.scheduler.dto.SummarizationRequest;
import com.yuldashov.scheduler.dto.SummarizationResponse;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaRpcConfig {

    @Bean
    public ProducerFactory<String, SummarizationRequest> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public ConsumerFactory<String, SummarizationResponse> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "scheduler-group");
        config.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "com.yuldashov.summarizationserver.dto,com.yuldashov.scheduler.dto");
        config.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        config.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE,
                "com.yuldashov.scheduler.dto.SummarizationResponse");
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SummarizationResponse> listenerContainerFactory(
            ConsumerFactory<String, SummarizationResponse> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, SummarizationResponse> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public ReplyingKafkaTemplate<String, SummarizationRequest, SummarizationResponse> replyingKafkaTemplate(
            ProducerFactory<String, SummarizationRequest> producerFactory,
            ConcurrentKafkaListenerContainerFactory<String, SummarizationResponse> factory
    ) {
        ConcurrentMessageListenerContainer<String, SummarizationResponse> listenerContainer =
        factory.createContainer("summarization-reply");

        listenerContainer.getContainerProperties().setGroupId("scheduler-group");
        return new ReplyingKafkaTemplate<>(producerFactory, listenerContainer);
    }

}
