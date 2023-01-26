package app.tasks.handler;

import app.tasks.model.ShareModel;
import app.tasks.model.Task;
import app.tasks.repository.SessionRepository;
import app.tasks.repository.ShareRepository;
import app.tasks.repository.TaskRepository;
import app.tasks.utils.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class TaskHandler {
    private final TaskRepository taskRepository;
    private final SessionRepository sessionRepository;
    private final AuthUtils authUtils;
    private final ShareRepository shareRepository;

    public TaskHandler(TaskRepository taskRepository, SessionRepository sessionRepository, AuthUtils authUtils, ShareRepository shareRepository) {
        this.taskRepository = taskRepository;
        this.sessionRepository = sessionRepository;
        this.authUtils = authUtils;
        this.shareRepository = shareRepository;
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/tasks")
    public List<Task> getTask(@RequestHeader("Authorization") String sessionToken) {
        String userId = authUtils.isAuthenticated(sessionToken, sessionRepository);
        // TODO extend for sharing model
        return taskRepository.findByUserUuid(userId);
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/task/{taskId}")
    public Task getTaskByTaskId(@PathVariable String taskId, @RequestHeader("Authorization") String sessionToken) {
        String userId = authUtils.isAuthenticated(sessionToken, sessionRepository);
        // TODO extend for sharing model
        return taskRepository.findTaskByIdAndUserUuid(taskId, userId);
    }


    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @PostMapping(value = "/task", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public Map<String, String> createTask(@RequestBody Task taskInput, @RequestHeader("Authorization") String sessionToken) {
        String userId = authUtils.isAuthenticated(sessionToken, sessionRepository);
        String taskId = UUID.randomUUID().toString();
        // TODO taskId use auto gen

        taskInput.setId(taskId);
        taskInput.setUserUuid(userId);
        taskInput.setLastUpdateTs(System.currentTimeMillis());
        taskRepository.save(taskInput);
        shareRepository.save(new ShareModel(null,userId, taskId, new Date().getTime()));

        return Map.of("id", taskInput.getId());
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @DeleteMapping(value = "/tasks", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public void deleteTask(@RequestBody Collection<String> taskUuids, @RequestHeader("Authorization") String sessionToken) {
        authUtils.isAuthenticated(sessionToken, sessionRepository);
        // TODO check that all tasks can be deleted by current user
        taskRepository.deleteByIdIn(taskUuids);
        shareRepository.deleteByTaskIdIn(taskUuids);
    }

}
