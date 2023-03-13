package app.tasks.controller.http;

import app.tasks.model.httpModels.SharePostInputModel;
import app.tasks.model.ShareModel;
import app.tasks.repository.ShareRepository;
import app.tasks.service.AuthService;
import app.tasks.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.Serializable;
import java.util.*;

@RestController
public class ShareTaskHandler {
    @Autowired
    private ShareRepository shareRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private TaskService taskService;

//    public ShareTaskHandler(ShareRepository shareRepository, AuthService authService, TaskService taskService) {
//        this.shareRepository = shareRepository;
//        this.authService = authService;
//        this.taskService = taskService;
//    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping("/shares")
    public List<ShareModel> getAllShares(@RequestHeader("Authorization") String sessionToken) {
        String userId = authService.isAuthenticated(sessionToken);
        return shareRepository.findByUserId(userId);
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @PostMapping(value = "/share", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, ? extends Serializable> share(@RequestBody SharePostInputModel sharePostInputModel, @RequestHeader("Authorization") String sessionToken) {
        String fromUserId = authService.isAuthenticated(sessionToken);
        Optional<ShareModel> accessCheck = shareRepository.findByTaskIdAndUserId(sharePostInputModel.getTaskId(), fromUserId);
        System.out.println(accessCheck);
        if (accessCheck.isPresent() && Objects.equals(accessCheck.get().getAccessType(), sharePostInputModel.getAccessType())) {
            long lastUpdateTs = new Date().getTime();
            shareRepository.save(new ShareModel(sharePostInputModel.getToUserId(), sharePostInputModel.getTaskId(), lastUpdateTs, sharePostInputModel.getAccessType()));
            taskService.updateLastUpdateTs(sharePostInputModel.getTaskId(), fromUserId, false,lastUpdateTs);
            return Map.of("taskId",accessCheck.get().getTaskId(),"lastUpdateTs",lastUpdateTs);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User doesn't have access to task");
        }
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @DeleteMapping(value = "/share", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, ? extends Serializable> delete(@RequestBody String taskId, @RequestHeader("Authorization") String sessionToken) {
        String userId = authService.isAuthenticated(sessionToken);
        Optional<ShareModel> accessCheck = shareRepository.findByTaskIdAndUserId(taskId, userId);
        if (accessCheck.isPresent()) {
            long lastUpdateTs = new Date().getTime();
            shareRepository.deleteByTaskIdAndUserId(taskId, userId);
            taskService.updateLastUpdateTs(taskId, userId, false,lastUpdateTs);
            return Map.of("taskId",taskId,"lastUpdateTs",lastUpdateTs);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User doesn't have access to task");
    }

}
