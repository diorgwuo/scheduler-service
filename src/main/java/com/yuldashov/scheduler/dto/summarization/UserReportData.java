package com.yuldashov.scheduler.dto.summarization;

import com.yuldashov.scheduler.dto.task.TaskDto;

import java.util.List;

public record UserReportData (
        Long id,
        String username,
        List<TaskDto> done,
        List<TaskDto> undone) {}
