package com.yuldashov.scheduler.service;

import com.yuldashov.scheduler.dto.mail.EmailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final KafkaTemplate<String, EmailMessage> kafkaTemplate;
    private static final String TOPIC_NAME = "EMAIL_SENDING_TASKS";

    public void sendEmailMessage(EmailMessage message) {
        kafkaTemplate.send(TOPIC_NAME, message);
    }
}
