package com.yuldashov.scheduler.service;

import com.yuldashov.scheduler.dto.summarization.SummarizationRequest;
import com.yuldashov.scheduler.models.SummarizationRequestEntity;
import com.yuldashov.scheduler.models.Task;
import com.yuldashov.scheduler.models.User;
import com.yuldashov.scheduler.repository.SummarizationRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SummarizationService {
    private static final String TOPIC_REQUEST = "summarization-request";
    private final KafkaTemplate<String, SummarizationRequest> requestKafkaTemplate;
    private final SummarizationRequestRepository requestRepository;

    public void generateReport(User user, List<Task> done, List<Task> undone) {
        String requestId = UUID.randomUUID().toString();
        requestRepository.save(new SummarizationRequestEntity(requestId, user.getEmail()));
        SummarizationRequest request = new SummarizationRequest(
                requestId,
                user.getId(),
                user.getUsername(),
                done,
                undone
        );
        requestKafkaTemplate.send(TOPIC_REQUEST, request);
    }
}
