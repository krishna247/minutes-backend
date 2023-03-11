package app.tasks.controller.http;

import app.tasks.enums.AccessType;
import app.tasks.model.ShareModel;
import app.tasks.model.SubTask;
import app.tasks.repository.ShareRepository;
import app.tasks.repository.SubTaskRepository;
import app.tasks.service.AuthService;
import app.tasks.service.QueryService;
import app.tasks.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.Serializable;
import java.util.*;

@RestController
public class SubTaskHandler {

    private final SubTaskRepository subTaskRepository;
    private final AuthService authService;
    private final QueryService queryService;
    private final TaskService taskService;
    private final ShareRepository shareRepository;

    public SubTaskHandler(SubTaskRepository subTaskRepository, AuthService authService, QueryService queryService, TaskService taskService, ShareRepository shareRepository) {
        this.subTaskRepository = subTaskRepository;
        this.authService = authService;
        this.queryService = queryService;
        this.taskService = taskService;
        this.shareRepository = shareRepository;
    }

    @Operation(summary = "Create multiple subtasks. Accepts a list of subtasks",security = {@SecurityRequirement(name = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns list of subTaskIds just created",content = @Content),
            @ApiResponse(responseCode = "400", description = "If more than TaskId is present in list of inputs",content = @Content),
            @ApiResponse(responseCode = "401", description = "No access to task or task doesnt exist",content = @Content)
    })
    @PostMapping(value = "/subtask", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public Map<String, Serializable> createSubTask(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Id and LastUpdateTs are overwritten by server. Operation is all or nothing(if any insert fails, all fail")
            @RequestBody List<SubTask> subTaskInputs, @RequestHeader("Authorization") String sessionToken) {
        String userId = authService.isAuthenticated(sessionToken);

        if(subTaskInputs.stream().map(SubTask::getTaskId).distinct().toList().size()!=1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Request must involve single task");

        Optional<ShareModel> accessCheck = shareRepository.findByTaskIdAndUserId(subTaskInputs.get(0).getTaskId(), userId);
        long lastUpdateTs = new Date().getTime();
        if(accessCheck.isPresent() && (accessCheck.get().getAccessType() == AccessType.EDIT | accessCheck.get().getAccessType() == AccessType.OWN)) {
            for (SubTask subTaskInput : subTaskInputs) {
                subTaskInput.setId(UUID.randomUUID().toString());
                subTaskInput.setLastUpdateTs(lastUpdateTs);
                queryService.persist(subTaskInput);
            }

            taskService.updateLastUpdateTs(subTaskInputs.get(0).getTaskId(), userId, false,lastUpdateTs);
            return Map.of("taskId",accessCheck.get().getTaskId(),"lastUpdateTs",lastUpdateTs);
        }
        else
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"User doesn't have access to task");
    }

    @Operation(summary = "Update multiple subtasks. Accepts a list of subtask models",security = {@SecurityRequirement(name = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns list of subTaskIds updated",content = @Content),
            @ApiResponse(responseCode = "400", description = "Inputs must have only one distinct taskId",content = @Content),
            @ApiResponse(responseCode = "404", description = "No subTasks updated",content = @Content)
    })
    @PutMapping(value = "/subtask", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public Map<String, Object> updateTasks(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Id is required to update and check access. Operation is all or nothing")
                                       @RequestBody List<SubTask> subTaskInputs, @RequestHeader("Authorization") String sessionToken) {
        String userId = authService.isAuthenticated(sessionToken);

        List<String> subTaskIds = subTaskInputs.stream().map(SubTask::getId).toList();
        List<SubTask> subTasks = subTaskRepository.getSubTaskWithAccess(subTaskIds,userId,List.of("EDIT","OWN"));
        if(subTasks.stream().map(SubTask::getTaskId).distinct().toList().size()!=1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Request must involve single task");

        Map<String,SubTask> subTaskInputsMap = new HashMap<>();
        long lastUpdateTs = new Date().getTime();
        subTaskInputs.forEach(s -> subTaskInputsMap.put(s.getId(),s));

        if(subTasks.size()>0){
            for(SubTask subTask: subTasks){
                SubTask subTaskInput = subTaskInputsMap.get(subTask.getId());

                subTask.setLastUpdateTs(lastUpdateTs);
                subTask.setDescription(subTaskInput.getDescription() == null ? subTask.getDescription() : subTaskInput.getDescription());
                subTask.setCompleted(subTaskInput.getCompleted() == null ? subTask.getCompleted() : subTaskInput.getCompleted());
                subTask.setParent(subTaskInput.getParent() == null ? subTask.getParent() : subTaskInput.getParent());
                queryService.update(subTask);
            }
            taskService.updateLastUpdateTs(subTaskInputs.get(0).getTaskId(), userId, false,lastUpdateTs);
            return Map.of("subTaskIds",subTasks.stream().map(SubTask::getId).toList(),"lastUpdateTs",lastUpdateTs);
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such subtasks exist or user doesn't have access");
        }
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns list of subTaskIds updated and lastUpdateTs of associated task",content = @Content),
            @ApiResponse(responseCode = "400", description = "Inputs must have only one distinct taskId",content = @Content),
    })

    @DeleteMapping(value = "/subtask")
    public Map<String, Object> deleteSubTasks(@RequestParam List<String> subTaskIds, @RequestHeader("Authorization") String sessionToken) {
        String userId = authService.isAuthenticated(sessionToken);

        List<SubTask> subTasks = subTaskRepository.getSubTaskWithAccess(subTaskIds,userId, List.of("OWN"));
        if(subTasks.stream().map(SubTask::getTaskId).distinct().toList().size()>1){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Request must involve single task");
        }

        if(subTasks.size()>0) {
            long lastUpdateTs = new Date().getTime();
            subTaskRepository.deleteAllByIdInBatch(subTasks.stream().map(SubTask::getId).toList());
            taskService.updateLastUpdateTs(subTasks.get(0).getTaskId(), userId, false,lastUpdateTs);
            return Map.of("subTaskIds",subTasks.stream().map(SubTask::getId).toList(),"lastUpdateTs",lastUpdateTs);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Request must involve single task");
    }

}
