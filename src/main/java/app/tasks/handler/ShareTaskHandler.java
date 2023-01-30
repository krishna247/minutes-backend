package app.tasks.handler;

import app.tasks.model.ShareInputModel;
import app.tasks.model.ShareModel;
import app.tasks.model.Task;
import app.tasks.repository.SessionRepository;
import app.tasks.repository.ShareRepository;
import app.tasks.repository.TaskRepository;
import app.tasks.utils.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        Task task = taskRepository.findTaskById(shareInputModel.getTaskId());
        boolean hasAccess = task.getShareModel().stream().anyMatch(s -> Objects.equals(s.getUserId(), fromUserId));
        if(hasAccess){
            System.out.println("User has access");
            task.getShareModel().add(new ShareModel(null,shareInputModel.getToUserId(),task.getId(), new Date().getTime(),"edit"));
            taskRepository.save(task);
            //            shareRepository.save(new ShareModel(null,shareInputModel.getToUserId(),task.getId(), new Date().getTime(),"edit"));
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User doesn't have access to task");
        }
    }
}
