package app.tasks.handler;

import app.tasks.model.SessionModel;
import app.tasks.repository.SessionRepository;
import app.tasks.utils.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SessionHandler {
    private final SessionRepository sessionRepository;
    private final AuthUtils authUtils;

    public SessionHandler(SessionRepository sessionRepository, AuthUtils authUtils) {

        this.sessionRepository = sessionRepository;
        this.authUtils = authUtils;
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping("/logout")
    @Transactional
    public void logout(@RequestHeader("Authorization") String sessionToken) {
        sessionRepository.deleteBySessionToken(sessionToken);
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping("/sessions")
    public List<SessionModel> getActiveSessions(@RequestHeader("Authorization") String sessionToken) {
        String userId = authUtils.isAuthenticated(sessionToken, sessionRepository);
        return sessionRepository.findByUserId(userId);
    }
}
