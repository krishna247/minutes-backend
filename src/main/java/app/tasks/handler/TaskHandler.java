package app.tasks.handler;

import app.tasks.model.ShareModel;
import app.tasks.model.Task;
import app.tasks.repository.SessionRepository;
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
    public TaskHandler(TaskRepository taskRepository, SessionRepository sessionRepository, AuthUtils authUtils) {
        this.taskRepository = taskRepository;
        this.sessionRepository = sessionRepository;
        this.authUtils = authUtils;
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/tasks")
    public List<Task> getTask(@RequestHeader("Authorization") String sessionToken) {
        String userId = authUtils.isAuthenticated(sessionToken, sessionRepository);
        return taskRepository.findByShareModelUserId(userId);
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/task/{taskId}")
    public Task getTaskByTaskId(@PathVariable String taskId, @RequestHeader("Authorization") String sessionToken) {
        String userId = authUtils.isAuthenticated(sessionToken, sessionRepository);
        return taskRepository.findByIdAndShareModelUserId(taskId, userId);
    }


    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @PostMapping(value = "/task", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public Map<String, String> createTask(@RequestBody Task taskInput, @RequestHeader("Authorization") String sessionToken) {
        String userId = authUtils.isAuthenticated(sessionToken, sessionRepository);
        String taskId = UUID.randomUUID().toString();

        taskInput.setId(taskId);
        taskInput.setUserId(userId);
        taskInput.setLastUpdateTs(System.currentTimeMillis());
        ShareModel shareModel = new ShareModel(null,userId, taskInput.getId(),new Date().getTime(),"edit");
        taskInput.setShareModel(List.of(shareModel));
        Task t  = taskRepository.save(taskInput);
//        shareRepository.save(new ShareModel(null,userId, taskId, new Date().getTime(), t));

        return Map.of("id", taskInput.getId());
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @DeleteMapping(value = "/tasks", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public void deleteTask(@RequestBody Collection<String> taskUuids, @RequestHeader("Authorization") String sessionToken) {

        // TODO check that all tasks can be deleted by current user
        taskRepository.deleteByIdIn(taskUuids);
//        shareRepository.deleteByTaskIdIn(taskUuids);
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping("/test")
    public List<Task> test(@RequestHeader("Authorization") String sessionToken){
        String userId = authUtils.isAuthenticated(sessionToken, sessionRepository);
        return taskRepository.findByShareModelUserId(userId);
    }

}
