package app.tasks.controller.http;

import app.tasks.service.AuthService;
import app.tasks.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@RestController
public class SyncHandler {

    AuthService authService;
    TaskService taskService;
    public SyncHandler(AuthService authService, TaskService taskService){
        this.authService = authService;
        this.taskService = taskService;
    }

    @GetMapping(value = "/getMaxUpdateTs")
    @Operation(summary = "Get max of lastUpdateTs of all tasks user has access to",security = {@SecurityRequirement(name = "Authorization")})
    public Map<String,Long> getMaxUpdateTs(@RequestHeader("Authorization") String sessionToken){
        String userId = authService.isAuthenticated(sessionToken);
        Long maxUpdateTs = taskService.getMaxUpdateTs(userId);
        return Map.of("maxUpdateTs", Objects.requireNonNullElse(maxUpdateTs, 0L));
    }
}