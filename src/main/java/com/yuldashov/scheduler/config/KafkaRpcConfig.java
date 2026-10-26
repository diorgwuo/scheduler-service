package com.yuldashov.scheduler.config;

import com.yuldashov.scheduler.dto.summarization.SummarizationRequest;
import com.yuldashov.scheduler.dto.summarization.SummarizationResponse;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${bootstrap.server}")
    private String bootstrapHost;
    @Value("${group.id}")
    private String groupId;
    @Value("${trusted.packages}")
    private String trustedPackages;
    @Value("${response.type}")
    private String responseType;
    @Value("${container.name}")
    private String containerName;

    @Bean
    public ProducerFactory<String, SummarizationRequest> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapHost);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public ConsumerFactory<String, SummarizationResponse> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapHost);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, trustedPackages);
        config.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        config.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE,
                responseType);
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
        factory.createContainer(containerName);

        listenerContainer.getContainerProperties().setGroupId(groupId);
        return new ReplyingKafkaTemplate<>(producerFactory, listenerContainer);
    }

}
