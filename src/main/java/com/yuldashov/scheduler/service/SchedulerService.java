package com.yuldashov.scheduler.service;

import com.yuldashov.scheduler.dto.mail.EmailMessage;
import com.yuldashov.scheduler.dto.task.TaskDto;
import com.yuldashov.scheduler.models.TaskStatus;
import com.yuldashov.scheduler.repository.TaskRepository;
import com.yuldashov.scheduler.repository.UserRepository;
import com.yuldashov.scheduler.models.Task;
import com.yuldashov.scheduler.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final static String TITLE = "Отчет за сутки";
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final EmailService emailService;
    private final SummarizationService summarizationService;

    @Scheduled(cron = "0 */4 * * * *")
    public void createReports() {
        LocalDateTime beginTime = LocalDateTime.now().minusDays(1);
        List<User> users = userRepository.findUsersWithTasks(beginTime);
        List<Long> usersId = users.stream().map(User::getId).toList();

        List<Task> allUndoneTask = taskRepository.findAllByUserIdInAndStatus(usersId, TaskStatus.UNDONE);
        List<Task> allDoneTask = taskRepository.findAllByUserIdInAndStatusAndCompletedAtAfter(usersId, TaskStatus.DONE, beginTime);

        Map<Long, List<Task>> doneByUser = allDoneTask.stream()
                .collect(Collectors.groupingBy(t -> t.getUser().getId()));
        Map<Long, List<Task>> undoneByUser = allUndoneTask.stream()
                .collect(Collectors.groupingBy(t -> t.getUser().getId()));

        for (User user : users) {
            try {
                generateReportForUser(user,
                        doneByUser.getOrDefault(user.getId(), List.of()),
                        undoneByUser.getOrDefault(user.getId(), List.of()));
            } catch (Exception e) {
            }
        }
    }

    private void generateReportForUser(User user, List<Task> allDoneTask, List<Task> allUndoneTask) throws ExecutionException, InterruptedException, TimeoutException {
        List<TaskDto> doneTasksDto = mapToDto(allDoneTask);
        List<TaskDto> undoneTasksDto = mapToDto(allUndoneTask);

        String report = summarizationService.generateReport(user, doneTasksDto, undoneTasksDto);
        EmailMessage message = new EmailMessage(
                user.getEmail(),
                TITLE,
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
        return new TaskDto(
                task.getId(), task.getTitle(),
                task.getDescription(),
                task.getStatus().name(),
                task.getCompletedAt()
        );
    }

}
