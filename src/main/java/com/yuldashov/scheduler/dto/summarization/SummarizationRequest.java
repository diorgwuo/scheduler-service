package com.yuldashov.scheduler.dto.summarization;

import com.yuldashov.scheduler.models.Task;
import java.util.List;

public record SummarizationRequest(
        String requestId,
        Long userId,
        String username,
        List<Task> doneTasks,
        List<Task> undoneTasks) {}


