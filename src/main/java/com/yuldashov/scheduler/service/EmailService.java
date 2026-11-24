package com.yuldashov.scheduler.service;

import com.yuldashov.scheduler.dto.mail.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final KafkaTemplate<String, EmailMessage> emailKafkaTemplate;
    private static final String TOPIC_NAME = "EMAIL_SENDING_TASKS";

    @Async("emailTaskExecutor")
    public void sendEmailMessage(EmailMessage message) {
        emailKafkaTemplate.send(TOPIC_NAME, message)
                .whenComplete((result, ex) -> {
                        if (ex != null) {
                        log.error("Не удалось отправить сообщение в Kafka", ex);
                    }
                });
    }
}
