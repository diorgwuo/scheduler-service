package com.yuldashov.scheduler.service;

import com.yuldashov.scheduler.dto.summarization.SummarizationRequest;
import com.yuldashov.scheduler.dto.summarization.SummarizationResponse;
import com.yuldashov.scheduler.dto.task.TaskDto;
import com.yuldashov.scheduler.models.User;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class SummarizationService {
    private static final String TOPIC_NAME = "summarization-request";
    private final ReplyingKafkaTemplate<String, SummarizationRequest, SummarizationResponse> replyingKafkaTemplate;

    public String generateReport(User user, List<TaskDto> done, List<TaskDto> undone) throws ExecutionException, InterruptedException, TimeoutException {

        SummarizationRequest request = new SummarizationRequest(
                UUID.randomUUID().toString(),
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                done,
                undone
        );

        ProducerRecord<String, SummarizationRequest> record = new ProducerRecord<>(
                TOPIC_NAME,
                request.requestId(),
                request
        );

        RequestReplyFuture<String, SummarizationRequest, SummarizationResponse> future =
                replyingKafkaTemplate.sendAndReceive(record);

        SummarizationResponse response = future.get(60, TimeUnit.SECONDS).value();

        return response.report();
    }

}
