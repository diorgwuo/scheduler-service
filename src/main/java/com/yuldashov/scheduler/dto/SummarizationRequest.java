package com.yuldashov.scheduler.dto;

import com.yuldashov.scheduler.models.Task;

import java.util.List;

public class SummarizationRequest {
    private String requestId;
    private Long userId;
    private String username;
    private String email;
    private List<TaskDto> doneTasks;
    private List<TaskDto> undoneTasks;

    public SummarizationRequest() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<TaskDto> getDoneTasks() {
        return doneTasks;
    }

    public void setDoneTasks(List<TaskDto> doneTasks) {
        this.doneTasks = doneTasks;
    }

    public List<TaskDto> getUndoneTasks() {
        return undoneTasks;
    }

    public void setUndoneTasks(List<TaskDto> undoneTasks) {
        this.undoneTasks = undoneTasks;
    }
}
