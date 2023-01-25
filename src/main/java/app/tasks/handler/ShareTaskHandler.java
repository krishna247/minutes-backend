package app.tasks.handler;

import app.tasks.model.ShareInputModel;
import app.tasks.model.ShareModel;
import app.tasks.repository.SessionRepository;
import app.tasks.repository.ShareRepository;
import app.tasks.repository.TaskRepository;
import app.tasks.utils.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
public class ShareTaskHandler {
    private final ShareRepository shareRepository;
    private final SessionRepository sessionRepository;
    private final TaskRepository taskRepository;
    private final AuthUtils authUtils;

    public ShareTaskHandler(ShareRepository shareRepository, AuthUtils authUtils,
                            SessionRepository sessionRepository, TaskRepository taskRepository){
        this.shareRepository = shareRepository;
        this.authUtils = authUtils;
        this.sessionRepository = sessionRepository;
        this.taskRepository = taskRepository;
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping("/shares")
    public List<ShareModel> getAllShares(@RequestHeader("Authorization") String sessionToken){
        authUtils.isAuthenticated(sessionToken, sessionRepository);
        return shareRepository.findAll();
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @PostMapping(value = "/share", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void share(@RequestBody ShareInputModel shareInputModel, @RequestHeader("Authorization") String sessionToken){
        String fromUserId = authUtils.isAuthenticated(sessionToken, sessionRepository);
        if(Objects.equals(taskRepository.findTaskById(shareInputModel.getTaskId()).getUserUuid(), fromUserId)){
            shareRepository.save(new ShareModel(null,shareInputModel.getToUserId(),shareInputModel.getTaskId(), new Date().getTime()));
        }
    }
}
