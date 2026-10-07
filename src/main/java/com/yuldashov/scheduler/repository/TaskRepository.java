package com.yuldashov.scheduler.repository;

import com.yuldashov.scheduler.models.Task;
import com.yuldashov.scheduler.models.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByUserIdAndStatusAndCompletedAtAfter(
            Long userId, TaskStatus status, LocalDateTime beforeTime);

    List<Task> findAllByUserIdAndStatus(
            Long userId, TaskStatus status);
}
