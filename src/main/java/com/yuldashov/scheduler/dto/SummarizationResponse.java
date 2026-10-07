package com.yuldashov.scheduler.dto;

public class SummarizationResponse {
    private String requestId;
    private String report;

    public SummarizationResponse() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }
}
