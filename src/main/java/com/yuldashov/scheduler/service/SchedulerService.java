package com.yuldashov.scheduler.service;

import com.yuldashov.scheduler.dto.EmailMessage;
import com.yuldashov.scheduler.dto.SummarizationRequest;
import com.yuldashov.scheduler.dto.TaskDto;
import com.yuldashov.scheduler.models.TaskStatus;
import com.yuldashov.scheduler.repository.TaskRepository;
import com.yuldashov.scheduler.repository.UserRepository;
import com.yuldashov.scheduler.models.Task;
import com.yuldashov.scheduler.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Service
public class SchedulerService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final EmailService emailService;
    private final SummarizationService summarizationService;

    @Autowired
    public SchedulerService(UserRepository userRepository, TaskRepository taskRepository, EmailService emailService, SummarizationService summarizationService) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.emailService = emailService;
        this.summarizationService = summarizationService;
    }

    @Scheduled(cron = "0 */4 * * * *")
    public void createReports() {
        LocalDateTime beginTime = LocalDateTime.now().minusDays(1);
        List<User> users = userRepository.findUsersWithTasks(beginTime);

        for (User user : users) {
            try {
                generateReportForUser(user, beginTime);
            } catch (Exception e) {
            }
        }
    }

    private void generateReportForUser(User user, LocalDateTime beginTime) throws ExecutionException, InterruptedException, TimeoutException {
        List<Task> doneTasks = taskRepository.findAllByUserIdAndStatusAndCompletedAtAfter(
                user.getId(), TaskStatus.DONE, beginTime);
        List<Task> undoneTasks = taskRepository.findAllByUserIdAndStatus(user.getId(), TaskStatus.UNDONE);

        List<TaskDto> doneTasksDto = mapToDto(doneTasks);
        List<TaskDto> undoneTasksDto = mapToDto(undoneTasks);

        String report = summarizationService.generateReport(user, doneTasksDto, undoneTasksDto);

        EmailMessage message = new EmailMessage(
                user.getEmail(),
                "Отчет за сутки",
                report
        );

        emailService.sendEmailMessage(message);

    }

    private List<TaskDto> mapToDto(List<Task> tasks) {
        List<TaskDto> tasksDto = new ArrayList<>();
        for (Task task : tasks) {
            TaskDto dto = toDto(task);
            tasksDto.add(dto);
        }
        return tasksDto;
    }

    private TaskDto toDto(Task task) {
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus().name());
        dto.setCompletedAt(task.getCompletedAt());
        return dto;
    }

}
