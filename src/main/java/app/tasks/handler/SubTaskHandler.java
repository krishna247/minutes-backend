package app.tasks.handler;

import app.tasks.model.SubTask;
import app.tasks.repository.SessionRepository;
import app.tasks.repository.SubTaskRepository;
import app.tasks.utils.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class SubTaskHandler {

    private final SubTaskRepository subTaskRepository;
    private final SessionRepository sessionRepository;
    private final AuthUtils authUtils;

    public SubTaskHandler(SubTaskRepository subTaskRepository, SessionRepository sessionRepository, AuthUtils authUtils) {
        this.subTaskRepository = subTaskRepository;
        this.sessionRepository = sessionRepository;
        this.authUtils = authUtils;
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @PostMapping(value = "/subtask", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Map<String, String> createSubTask(@RequestBody SubTask subTaskInput, @RequestHeader("Authorization") String sessionToken) {
        authUtils.isAuthenticated(sessionToken, sessionRepository);
        subTaskInput.setId(UUID.randomUUID().toString());
        subTaskInput.setLastUpdateTs(System.currentTimeMillis());
        subTaskRepository.save(subTaskInput);
        return Map.of("id", subTaskInput.getId());
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @DeleteMapping(value = "/subtask")
    public void deleteSubTasks(@RequestParam List<String> subTaskUuids, @RequestHeader("Authorization") String sessionToken) {
        authUtils.isAuthenticated(sessionToken, sessionRepository);
        subTaskRepository.deleteAllById(subTaskUuids);
    }

}
