package com.yuldashov.scheduler.service;

import com.yuldashov.scheduler.dto.mail.EmailMessage;
import com.yuldashov.scheduler.dto.mail.EmailType;
import com.yuldashov.scheduler.dto.summarization.SummarizationResponse;
import com.yuldashov.scheduler.models.TaskStatus;
import com.yuldashov.scheduler.repository.SummarizationRequestRepository;
import com.yuldashov.scheduler.repository.UserRepository;
import com.yuldashov.scheduler.models.Task;
import com.yuldashov.scheduler.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {
    private static final String TOPIC_RESPONSE = "summarization-reply";
    private static final String GROUP_ID = "scheduler-group";
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final SummarizationService summarizationService;
    private final SummarizationRequestRepository requestRepository;

    @Scheduled(cron = "0 */4 * * * *")
    public void createReports() {
        LocalDateTime beginTime = LocalDateTime.now().minusDays(1);
        List<User> users = userRepository.findUsersWithTasks(beginTime);

        List<Task> allTasks = users.stream()
                .flatMap(user -> user.getTasks().stream())
                .toList();

        Map<Long, List<Task>> doneByUser = allTasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .collect(Collectors.groupingBy(task -> task.getUser().getId()));

        Map<Long, List<Task>> undoneByUser = allTasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.UNDONE)
                .collect(Collectors.groupingBy(t -> t.getUser().getId()));

        for (User user : users) {
            try {
                summarizationService.generateReport(user, doneByUser.getOrDefault(user.getId(), List.of()), undoneByUser.getOrDefault(user.getId(), List.of()));
            } catch (Exception e) {
                log.error("Не удалось отправить запрос к LLM для user: " + user.getId(), e);
            }
        }
    }

    private void generateReportForUser(User user, List<Task> allDoneTask, List<Task> allUndoneTask)  {
        summarizationService.generateReport(user, allDoneTask, allUndoneTask);
    }

    @KafkaListener(topics = TOPIC_RESPONSE, groupId = GROUP_ID)
    public void sendReportForUser(SummarizationResponse response) {
        String email = requestRepository.findEmailByRequestId(response.requestId())
                .orElseThrow(() -> new RuntimeException("Не найден такой запрос с requestId: " + response.requestId()));
        emailService.sendEmailMessage(
                new EmailMessage(email, EmailType.REPORT, response.report()));
    }
}
