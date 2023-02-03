package app.tasks.handler;

import app.tasks.model.ShareModel;
import app.tasks.model.Task;
import app.tasks.repository.SessionRepository;
import app.tasks.repository.ShareRepository;
import app.tasks.repository.TaskRepository;
import app.tasks.service.QueryService;
import app.tasks.utils.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static app.tasks.constants.QueryConstants.GET_TASKS_WITH_ACCESS;
import static app.tasks.constants.QueryConstants.GET_TASK_WITH_ACCESS;

@RestController
public class TaskHandler {
    private final TaskRepository taskRepository;
    private final SessionRepository sessionRepository;
    private final ShareRepository shareRepository;
    private final AuthUtils authUtils;
    @Autowired
    QueryService queryService;

    public TaskHandler(TaskRepository taskRepository, SessionRepository sessionRepository, AuthUtils authUtils, ShareRepository shareRepository) {
        this.taskRepository = taskRepository;
        this.sessionRepository = sessionRepository;
        this.authUtils = authUtils;
        this.shareRepository = shareRepository;
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/tasks")
    public List<Map<String, Object>> getTask(@RequestHeader("Authorization") String sessionToken) {
        String userId = authUtils.isAuthenticated(sessionToken, sessionRepository);
        return queryService.executeQueryResponse(GET_TASKS_WITH_ACCESS, Map.of("userId", userId));
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/task/{taskId}")
    public List<Map<String, Object>> getTaskByTaskId(@PathVariable String taskId, @RequestHeader("Authorization") String sessionToken) {
        String userId = authUtils.isAuthenticated(sessionToken, sessionRepository);
        return queryService.executeQueryResponse(GET_TASK_WITH_ACCESS, Map.of("userId", userId, "taskId", taskId));
    }


    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @PostMapping(value = "/task", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public Map<String, String> createTask(@RequestBody Task taskInput, @RequestHeader("Authorization") String sessionToken) {
        String userId = authUtils.isAuthenticated(sessionToken, sessionRepository);
        String taskId = UUID.randomUUID().toString();

        // overwrite server assigned attributes
        taskInput.setId(taskId);
        taskInput.setUserId(userId);
        taskInput.setLastUpdateTs(System.currentTimeMillis());

        taskRepository.save(taskInput);
        shareRepository.save(new ShareModel(userId, taskId, new Date().getTime(), "edit"));

        return Map.of("id", taskInput.getId());
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @PutMapping(value = "/task", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public void updateTask(@RequestBody Task taskInput, @RequestHeader("Authorization") String sessionToken) {
        String userId = authUtils.isAuthenticated(sessionToken, sessionRepository);
        List<Task> taskObj = taskRepository.getTaskByIdAndUserIdAndCheckAccess(taskInput.getId(),userId);
        if(taskObj.size()==1){
            Task task = taskObj.get(0);
            task.setDeadlineDate(taskInput.getDeadlineDate() == null ? task.getDeadlineDate() : taskInput.getDeadlineDate());
            task.setPriority(taskInput.getPriority() == null ? task.getPriority() : taskInput.getPriority());
            task.setTags(taskInput.getTags() == null ? task.getTags() : taskInput.getTags());
            task.setRepeatFreq(taskInput.getRepeatFreq() == null ? task.getRepeatFreq() : taskInput.getRepeatFreq());
            task.setDescription(taskInput.getDescription() == null ? task.getDescription() : taskInput.getDescription());
            task.setIsStarred(taskInput.getIsStarred() == null ? task.getIsStarred() : taskInput.getIsStarred());
            task.setIsDone(taskInput.getIsDone() == null ? task.getIsDone() : taskInput.getIsDone());
            task.setLastUpdateTs(new Date().getTime());
            taskRepository.save(task);
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such task");
        }
    }



    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @DeleteMapping(value = "/tasks", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public void deleteTask(@RequestBody Collection<String> taskIds, @RequestHeader("Authorization") String sessionToken) {
        String userId = authUtils.isAuthenticated(sessionToken, sessionRepository);
        List<ShareModel> shares = shareRepository.findByTaskIdInAndUserId(taskIds, userId);

        List<String> tasksWithAccess = new ArrayList<>();
        for (ShareModel s : shares) {
            if (Objects.equals(s.getAccessType(), "edit"))
                tasksWithAccess.add(s.getTaskId());
        }
        taskRepository.deleteAllById(tasksWithAccess);
        shareRepository.deleteByTaskIdIn(tasksWithAccess);
    }

//    @Operation(security = {@SecurityRequirement(name = "Authorization")})
//    @GetMapping("/test")
//    public List<Task> test(@RequestHeader("Authorization") String sessionToken){
//        String userId = authUtils.isAuthenticated(sessionToken, sessionRepository);
//        return (List<Task>) taskRepository.findTaskById(userId);
//    }

}
