package app.tasks.handler;

import app.tasks.model.Task;
import app.tasks.repository.SessionRepository;
import app.tasks.repository.TaskRepository;
import app.tasks.utils.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class TaskHandler {
    private final TaskRepository taskRepository;
    private final SessionRepository sessionRepository;
    private final AuthUtils authUtils;

    public TaskHandler(TaskRepository taskRepository, SessionRepository sessionRepository, AuthUtils authUtils) {
        this.taskRepository = taskRepository;
        this.sessionRepository = sessionRepository;
        this.authUtils = authUtils;
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/tasks")
    public List<Task> getTask(@RequestParam String userUuid, @RequestHeader("Authorization") String sessionToken) {
        authUtils.isAuthenticated(sessionToken, sessionRepository);
        return taskRepository.findByUserUuid(userUuid);
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/task/{taskId}")
    public Task getTaskByTaskId(@PathVariable String taskId, @RequestHeader("Authorization") String sessionToken) {
        authUtils.isAuthenticated(sessionToken, sessionRepository);
        return taskRepository.findTaskById(taskId);
    }


    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @PostMapping(value = "/task", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Map<String, String> createTask(@RequestBody Task taskInput, @RequestHeader("Authorization") String sessionToken) {
        authUtils.isAuthenticated(sessionToken, sessionRepository);
        taskInput.setId(UUID.randomUUID().toString());
        taskInput.setLastUpdateTs(System.currentTimeMillis());
        taskRepository.save(taskInput);
        return Map.of("id", taskInput.getId());
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @DeleteMapping(value = "/tasks", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void deleteTask(@RequestParam Collection<String> taskUuids, @RequestHeader("Authorization") String sessionToken) {
        authUtils.isAuthenticated(sessionToken, sessionRepository);
        taskRepository.deleteAllById(taskUuids);
    }

}
