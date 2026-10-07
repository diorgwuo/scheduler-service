package com.yuldashov.scheduler.service;

import com.yuldashov.scheduler.dto.SummarizationRequest;
import com.yuldashov.scheduler.dto.SummarizationResponse;
import com.yuldashov.scheduler.dto.TaskDto;
import com.yuldashov.scheduler.models.User;
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
public class SummarizationService {
    private final ReplyingKafkaTemplate<String, SummarizationRequest, SummarizationResponse> replyingKafkaTemplate;


    public SummarizationService(ReplyingKafkaTemplate<String, SummarizationRequest, SummarizationResponse> replyingKafkaTemplate) {
        this.replyingKafkaTemplate = replyingKafkaTemplate;
    }

    public String generateReport(User user, List<TaskDto> done, List<TaskDto> undone) throws ExecutionException, InterruptedException, TimeoutException {

        SummarizationRequest request = new SummarizationRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setUserId(user.getId());
        request.setUsername(user.getUsername());
        request.setEmail(user.getEmail());
        request.setDoneTasks(done);
        request.setUndoneTasks(undone);

        ProducerRecord<String, SummarizationRequest> record = new ProducerRecord<>(
                "summarization-request",
                request.getRequestId(),
                request
        );

        RequestReplyFuture<String, SummarizationRequest, SummarizationResponse> future =
                replyingKafkaTemplate.sendAndReceive(record);

        SummarizationResponse response = future.get(60, TimeUnit.SECONDS).value();

        return response.getReport();
    }

}
