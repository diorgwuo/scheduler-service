package com.yuldashov.scheduler.dto.summarization;

import com.yuldashov.scheduler.models.Task;
import java.util.List;

public record UserReportData (
        Long id,
        String username,
        List<Task> done,
        List<Task> undone) {}
