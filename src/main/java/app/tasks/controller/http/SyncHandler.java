package app.tasks.controller.http;

import app.tasks.service.AuthService;
import app.tasks.service.TaskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SyncHandler {

    AuthService authService;
    TaskService taskService;
    public SyncHandler(AuthService authService, TaskService taskService){
        this.authService = authService;
        this.taskService = taskService;
    }

    @GetMapping(value = "/getMaxUpdateTs")
    public Map<String,Long> getMaxUpdateTs(@RequestHeader("Authorization") String sessionToken){
        String userId = authService.isAuthenticated(sessionToken);
        return Map.of("maxUpdateTs",taskService.getMaxUpdateTs(userId));
    }
}
