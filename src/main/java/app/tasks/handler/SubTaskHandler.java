package app.tasks.handler;

import app.tasks.model.SubTask;
import app.tasks.repository.SessionRepository;
import app.tasks.repository.SubTaskRepository;
import app.tasks.service.QueryService;
import app.tasks.utils.AuthUtils;
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

import java.util.*;

@RestController
public class SubTaskHandler {

    private final SubTaskRepository subTaskRepository;
    private final SessionRepository sessionRepository;
    private final AuthUtils authUtils;
    private final QueryService queryService;

    public SubTaskHandler(SubTaskRepository subTaskRepository, SessionRepository sessionRepository, AuthUtils authUtils, QueryService queryService) {
        this.subTaskRepository = subTaskRepository;
        this.sessionRepository = sessionRepository;
        this.authUtils = authUtils;
        this.queryService = queryService;
    }

    @Operation(summary = "Create multiple subtasks. Accepts a list of subtasks",security = {@SecurityRequirement(name = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns list of subTaskIds just created",content = @Content)
    })
    @PostMapping(value = "/subtask", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public List<String> createSubTask( @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Id and LastUpdateTs are overwritten by server. Operation is all or nothing(if any insert fails, all fail")
            @RequestBody List<SubTask> subTaskInputs, @RequestHeader("Authorization") String sessionToken) {
        authUtils.isAuthenticated(sessionToken, sessionRepository);
        for (SubTask subTaskInput : subTaskInputs){
            subTaskInput.setId(UUID.randomUUID().toString());
            subTaskInput.setLastUpdateTs(new Date().getTime());
            queryService.persist(subTaskInput,SubTask.class);
        }
        return subTaskInputs.stream().map(SubTask::getId).toList();
    }

    @Operation(summary = "Update multiple subtasks. Accepts a list of subtask models",security = {@SecurityRequirement(name = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns list of subTaskIds updated",content = @Content),
            @ApiResponse(responseCode = "404", description = "No subTasks updated",content = @Content)
    })
    @PutMapping(value = "/subtask", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public List<String> updateTasks( @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Id is required to update and check access. Operation is all or nothing")
                                       @RequestBody List<SubTask> subTaskInputs, @RequestHeader("Authorization") String sessionToken) {
        String userId = authUtils.isAuthenticated(sessionToken, sessionRepository);
        List<String> subTaskIds = subTaskInputs.stream().map(SubTask::getId).toList();
        List<SubTask> subTasks = subTaskRepository.getSubTaskWithAccess(subTaskIds,userId);
        System.out.println(subTasks);

        Map<String,SubTask> subTaskInputsMap = new HashMap<>();
        subTaskInputs.forEach(s -> subTaskInputsMap.put(s.getId(),s));

        if(subTasks.size()>0){
            for(SubTask subTask: subTasks){
                SubTask subTaskInput = subTaskInputsMap.get(subTask.getId());

                subTask.setLastUpdateTs(new Date().getTime());
                subTask.setText(subTaskInput.getText() == null ? subTask.getText() : subTaskInput.getText());
                subTask.setCompleted(subTaskInput.getCompleted() == null ? subTask.getCompleted() : subTaskInput.getCompleted());
                subTask.setParent(subTaskInput.getParent() == null ? subTask.getParent() : subTaskInput.getParent());
                queryService.update(subTask,SubTask.class);
            }
            return subTasks.stream().map(SubTask::getId).toList();
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such subtasks exist or user doesn't have access");
        }
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @DeleteMapping(value = "/subtask")
    public void deleteSubTasks(@RequestParam List<String> subTaskUuids, @RequestHeader("Authorization") String sessionToken) {
        authUtils.isAuthenticated(sessionToken, sessionRepository);
        subTaskRepository.deleteAllById(subTaskUuids);
    }

}
