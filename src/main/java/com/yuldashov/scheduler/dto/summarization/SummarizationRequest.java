package com.yuldashov.scheduler.dto.summarization;

import com.yuldashov.scheduler.dto.task.TaskDto;

import java.util.List;

public record SummarizationRequest(
        String requestId,
        Long userId,
        String username,
        String email,
        List<TaskDto> doneTasks,
        List<TaskDto> undoneTasks) {}


