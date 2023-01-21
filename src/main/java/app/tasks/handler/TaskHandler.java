package app.tasks.handler;

import app.tasks.model.Task;
import app.tasks.repository.SessionRepository;
import app.tasks.repository.TaskRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static app.tasks.utils.AuthUtils.isAuthenticated;

@RestController
public class TaskHandler {
    private final TaskRepository taskRepository;
    private final SessionRepository sessionRepository;

    public TaskHandler(TaskRepository taskRepository, SessionRepository sessionRepository) {
        this.taskRepository = taskRepository;
        this.sessionRepository = sessionRepository;
    }

    @Operation(security = { @SecurityRequirement(name = "Authorization") })
    @GetMapping(value = "/task")
    public List<Task> getTask(@RequestParam String userUuid, @RequestHeader("Authorization") String sessionToken) {
        isAuthenticated(sessionToken,sessionRepository);
        return taskRepository.findByUserUuid(userUuid);
    }

    @Operation(security = { @SecurityRequirement(name = "Authorization") })
    @PostMapping(value = "/task",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> createTask(@RequestBody Task taskInput, @RequestHeader("Authorization") String sessionToken) {
        isAuthenticated(sessionToken,sessionRepository);
        taskInput.setId(UUID.randomUUID().toString());
        taskInput.setLastUpdateTs(System.currentTimeMillis());
        taskRepository.save(taskInput);
        return Map.of("id", taskInput.getId());
    }

    @Operation(security = { @SecurityRequirement(name = "Authorization") })
    @DeleteMapping(value = "/task")
    public void deleteTask(@RequestParam String taskUuid, @RequestHeader("Authorization") String sessionToken) {
        isAuthenticated(sessionToken,sessionRepository);
        taskRepository.deleteAllById(List.of(taskUuid));
    }

}
