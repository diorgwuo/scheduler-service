package com.yuldashov.scheduler.repository;

import com.yuldashov.scheduler.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT DISTINCT u FROM User u WHERE EXISTS (SELECT 1 FROM Task t WHERE t.user= u AND ((t.status = 'DONE' AND t.completedAt >= :beforeTime) OR t.status = 'UNDONE'))")
    List<User> findUsersWithTasks(LocalDateTime beforeTime);
}
