package com.yuldashov.scheduler.service;

import com.yuldashov.scheduler.dto.EmailMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final KafkaTemplate<String, EmailMessage> kafkaTemplate;


    public EmailService(KafkaTemplate<String, EmailMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEmailMessage(EmailMessage message) {
        kafkaTemplate.send("EMAIL_SENDING_TASKS", message);
    }
}
