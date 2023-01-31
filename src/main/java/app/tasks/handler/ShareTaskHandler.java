package app.tasks.handler;

import app.tasks.model.ShareModel;
import app.tasks.repository.SessionRepository;
import app.tasks.repository.ShareRepository;
import app.tasks.utils.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ShareTaskHandler {
    private final ShareRepository shareRepository;
    private final SessionRepository sessionRepository;
    private final AuthUtils authUtils;

    public ShareTaskHandler(ShareRepository shareRepository, AuthUtils authUtils,
                            SessionRepository sessionRepository){
        this.shareRepository = shareRepository;
        this.authUtils = authUtils;
        this.sessionRepository = sessionRepository;
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping("/shares")
    public List<ShareModel> getAllShares(@RequestHeader("Authorization") String sessionToken){
        authUtils.isAuthenticated(sessionToken, sessionRepository);
        return shareRepository.findAll();
    }

//    @Operation(security = {@SecurityRequirement(name = "Authorization")})
//    @PostMapping(value = "/share", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public void share(@RequestBody ShareInputModel shareInputModel, @RequestHeader("Authorization") String sessionToken){
//        String fromUserId = authUtils.isAuthenticated(sessionToken, sessionRepository);
//        Task task = taskRepository.findTaskById(shareInputModel.getTaskId());
//        boolean hasAccess = task.getShareModel().stream().anyMatch(s -> Objects.equals(s.getUserId(), fromUserId));
//        if(hasAccess){
//            System.out.println("User has access");
//            task.getShareModel().add(new ShareModel(null,shareInputModel.getToUserId(),task.getId(), new Date().getTime(),"edit"));
//            taskRepository.save(task);
//            //            shareRepository.save(new ShareModel(null,shareInputModel.getToUserId(),task.getId(), new Date().getTime(),"edit"));
//        }
//        else {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User doesn't have access to task");
//        }
//    }
}
