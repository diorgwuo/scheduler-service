package com.yuldashov.scheduler.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "summarization_request")
@NoArgsConstructor
@AllArgsConstructor
public class SummarizationRequestEntity {
    @Id
    @Column(name = "request_id")
    private String requestId;
    private String email;
}
