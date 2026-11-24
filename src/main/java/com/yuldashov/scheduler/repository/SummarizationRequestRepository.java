package com.yuldashov.scheduler.repository;

import com.yuldashov.scheduler.models.SummarizationRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SummarizationRequestRepository extends JpaRepository<SummarizationRequestEntity, String> {
    Optional<String> findEmailByRequestId(String requestId);
}
