package app.tasks.controller.http;

import app.tasks.model.HTTPModels.SharePostInputModel;
import app.tasks.model.ShareModel;
import app.tasks.repository.ShareRepository;
import app.tasks.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
public class ShareTaskHandler {
    private final ShareRepository shareRepository;
    private final AuthService authService;

    public ShareTaskHandler(ShareRepository shareRepository, AuthService authService) {
        this.shareRepository = shareRepository;
        this.authService = authService;
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping("/shares")
    public List<ShareModel> getAllShares(@RequestHeader("Authorization") String sessionToken) {
        String userId = authService.isAuthenticated(sessionToken);
        return shareRepository.findByUserId(userId);
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @PostMapping(value = "/share", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void share(@RequestBody SharePostInputModel sharePostInputModel, @RequestHeader("Authorization") String sessionToken) {
        String fromUserId = authService.isAuthenticated(sessionToken);
        Optional<ShareModel> accessCheck = shareRepository.findByTaskIdAndUserId(sharePostInputModel.getTaskId(), fromUserId);
        if (accessCheck.isPresent() && Objects.equals(accessCheck.get().getAccessType(), sharePostInputModel.getAccessType())) {
            shareRepository.save(new ShareModel(sharePostInputModel.getToUserId(), sharePostInputModel.getTaskId(), new Date().getTime(), sharePostInputModel.getAccessType()));
            // TODO update last update ts of task
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User doesn't have access to task");
        }
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @DeleteMapping(value = "/share", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void delete(@RequestBody String taskId, @RequestHeader("Authorization") String sessionToken) {
        String userId = authService.isAuthenticated(sessionToken);
        shareRepository.deleteByTaskIdAndUserId(taskId, userId);
        // TODO update last update ts of task
    }

}