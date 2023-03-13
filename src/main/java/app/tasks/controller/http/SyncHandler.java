package app.tasks.controller.http;

import app.tasks.model.ShareModel;
import app.tasks.model.SubTask;
import app.tasks.model.Task;
import app.tasks.service.AuthService;
import app.tasks.service.TaskService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
public class SyncHandler {

    private final AuthService authService;
    private final TaskService taskService;
    private final ObjectMapper objectMapper;

    public SyncHandler(AuthService authService, TaskService taskService, ObjectMapper objectMapper){
        this.authService = authService;
        this.taskService = taskService;
        this.objectMapper = objectMapper;
    }

    @GetMapping(value = "/getMaxUpdateTs")
    @Operation(summary = "Get max of lastUpdateTs of all tasks user has access to",security = {@SecurityRequirement(name = "Authorization")})
    public Map<String,Long> getMaxUpdateTs(@RequestHeader("Authorization") String sessionToken){
        String userId = authService.isAuthenticated(sessionToken);
        Long maxUpdateTs = taskService.getMaxUpdateTs(userId);
        return Map.of("maxUpdateTs", Objects.requireNonNullElse(maxUpdateTs, 0L));
    }

    @GetMapping(value = "/taskAfterTs")
    @Operation(summary = "Get list of tasks updated after provided timestamp",security = {@SecurityRequirement(name = "Authorization")})
    public List<Map<String, Object>> getTasksAfterTs(@RequestHeader("Authorization") String sessionToken, long timestamp){
        String userId = authService.isAuthenticated(sessionToken);
        return taskService.getTasksAfterLastUpdateTs(timestamp,userId);
    }

    // create endpoint to accept task JSON containing subtasks and access info. Accepts list of full task models, loops over
    // for each task, build subtask, task and sharing objects. update task, drop & replace subtasks, drop & replace access
    @PostMapping(value = "/syncLocalUpdates", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Local updates sync",security = {@SecurityRequirement(name = "Authorization")})
    public Map<String, Long> syncLocalChanges(@RequestBody List<Map<String,Object>> inputs, @RequestHeader("Authorization") String sessionToken) throws IOException {
        String userId = authService.isAuthenticated(sessionToken);
        List<Task> tasks = new ArrayList<>();
        Map<String,List<SubTask>> subTasksMap = new HashMap<>();
        Map<String,List<ShareModel>> sharesMap = new HashMap<>();

        for(var input : inputs){
            String taskId = (String) input.get("id");
            Task task = objectMapper.convertValue(input,Task.class);
            List<ShareModel> shares = objectMapper.convertValue(input.get("shares"), new TypeReference<List<ShareModel>>(){});
            List<SubTask> subTasks = objectMapper.convertValue(input.get("subtasks"), new TypeReference<List<SubTask>>(){});
            tasks.add(task);
            subTasksMap.put(taskId,subTasks);
            sharesMap.put(taskId,shares);
        }
        return taskService.performLocalSyncBulk(tasks,subTasksMap,sharesMap,userId);
    }
}
