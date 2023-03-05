package app.tasks.controller.http;

import app.tasks.enums.AccessType;
import app.tasks.model.ShareModel;
import app.tasks.model.Task;
import app.tasks.repository.ShareRepository;
import app.tasks.repository.TaskRepository;
import app.tasks.service.AuthService;
import app.tasks.service.QueryService;
import app.tasks.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static app.tasks.constants.QueryConstants.GET_TASKS_WITH_ACCESS_JSON;
import static app.tasks.constants.QueryConstants.GET_TASK_WITH_ACCESS_SUBTASKS;

@RestController
public class TaskHandler {
    private final TaskRepository taskRepository;
    private final ShareRepository shareRepository;
    private final AuthService authService;
    private final QueryService queryService;
    private final TaskService taskService;

    public TaskHandler(TaskRepository taskRepository, AuthService authService, ShareRepository shareRepository, QueryService queryService, @Autowired TaskService taskService) {
        this.taskRepository = taskRepository;
        this.authService = authService;
        this.shareRepository = shareRepository;
        this.queryService = queryService;
        this.taskService = taskService;
    }

    @Operation(summary = "Get all tasks of a user with any level of access. Returns list of task and access attributes", security = {@SecurityRequirement(name = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get list of tasks and access attributes. Empty list if no tasks",content = @Content)
    })
    @GetMapping(value = "/tasks")
    public List<Map<String, Object>> getTasks(@RequestHeader("Authorization") String sessionToken) {
        String userId = authService.isAuthenticated(sessionToken);
        return queryService.executeQueryResponse(GET_TASKS_WITH_ACCESS_JSON, Map.of("userId", userId));
    }

    @Operation(summary = "Get a specific task with taskId if user has access",security = {@SecurityRequirement(name = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get tasks and access attributes of specific task",  content = @Content),
            @ApiResponse(responseCode = "404", description = "Fails if task doesn't exist or user doesn't have access",  content = @Content)
    })
    @GetMapping(value = "/task/{taskId}")
    public List<Map<String, Object>> getTaskByTaskId(@PathVariable String taskId, @RequestHeader("Authorization") String sessionToken) {
        String userId = authService.isAuthenticated(sessionToken);
        List<Map<String, Object>> result = queryService.executeQueryResponse(GET_TASK_WITH_ACCESS_SUBTASKS, Map.of("userId", userId, "taskId", taskId));
        if(result.size()>0){
            return result;
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such task");
        }
    }


    @Operation(summary = "Create task. User is automatically granted edit access to the task", security = {@SecurityRequirement(name = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns JSON containing of newly created taskId, providedId and lastUpdateTs for the task ",content = @Content)
    })
    @PostMapping(value = "/task", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public Map<String, Object> createTask(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "id, userId, lastUpdateTs and deleted will be overwritten by server")
                                              @RequestBody Task taskInput, @RequestHeader("Authorization") String sessionToken) {
        String userId = authService.isAuthenticated(sessionToken);
        return taskService.createTask(taskInput, userId);
    }

    @Operation(summary = "Create multiple tasks. User is automatically granted edit access to the tasks", security = {@SecurityRequirement(name = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns list of JSON with same format as POST /task",content = @Content)
    })
    @PostMapping(value = "/tasks", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public List<Map<String, Object>> createTasks(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "id, userId, lastUpdateTs and deleted will be overwritten by server")
                                          @RequestBody List<Task> taskInputs, @RequestHeader("Authorization") String sessionToken) {
        String userId = authService.isAuthenticated(sessionToken);
        List<Map<String,Object>> listMap = new ArrayList<>();

        for(Task taskInput : taskInputs){
            Map<String, Object> responseMap = taskService.createTask(taskInput,userId);
            listMap.add(responseMap);
        }
        return listMap;
    }

    @Operation(summary = "To update a task. Returns 404 if task not found or no access", security = {@SecurityRequirement(name = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "No return value",content = @Content),
            @ApiResponse(responseCode = "404", description = "Task not found or no access",content = @Content)
    })
    @PutMapping(value = "/task", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Long> updateTask(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Only id(taskId) is required. Any other field provided will updated if it matches type validation")
                               @RequestBody Task taskInput, @RequestHeader("Authorization") String sessionToken) {
        String userId = authService.isAuthenticated(sessionToken);

        Task task = taskService.updateTask(userId, taskInput);
        taskService.sendWSUpdate(task.getId(),userId,false);
        return Map.of("lastUpdateTs",task.getLastUpdateTs());
    }

    @Operation(summary = "To update many tasks. Returns 404 if task not found or no access", security = {@SecurityRequirement(name = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "No return value",content = @Content),
            @ApiResponse(responseCode = "404", description = "Task not found or no access",content = @Content)
    })
    @PutMapping(value = "/tasks", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> updateTasks(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Only id(taskId) is required. Any other field provided will updated if it matches type validation")
                                        @RequestBody List<Task> taskInputs, @RequestHeader("Authorization") String sessionToken) {
        String userId = authService.isAuthenticated(sessionToken);
        List<Map<String,Object>> listMap = new ArrayList<>();

        for(Task taskInput : taskInputs){
            Task task = taskService.updateTask(userId, taskInput);
            taskService.sendWSUpdate(task.getId(),userId,false);
            listMap.add(Map.of("taskId",task.getId(),"lastUpdateTs",task.getLastUpdateTs()));
        }
        return listMap;
    }

    @Operation(summary = "To delete a list of tasks. Tasks with edit access for the user are deleted. List of deleted tasks is returned", security = {@SecurityRequirement(name = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of deleted tasks is returned",content = @Content),
            @ApiResponse(responseCode = "400", description = "No tasks deleted",content = @Content)
    })
    @DeleteMapping(value = "/tasks", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public List<String> deleteTask(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "List of taskIds to be deleted. User must have edit access to the tasks")
                                       @RequestBody Collection<String> taskIds, @RequestHeader("Authorization") String sessionToken) {
        String userId = authService.isAuthenticated(sessionToken);
        List<ShareModel> shares = shareRepository.findByTaskIdInAndUserId(taskIds, userId);

        List<String> tasksWithAccess = new ArrayList<>();
        for (ShareModel s : shares) {
            if (s.getAccessType() == AccessType.OWN)
                tasksWithAccess.add(s.getTaskId());
        }
        if(tasksWithAccess.size()>0) {
            for(String taskId:tasksWithAccess) {
                taskRepository.deleteTask(taskId);
                taskService.updateLastUpdateTs(taskId, userId, true, new Date().getTime());
            }
            return tasksWithAccess;
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No tasks deleted");
    }
}
