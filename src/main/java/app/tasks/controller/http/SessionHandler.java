package app.tasks.controller.http;

import app.tasks.model.SessionModel;
import app.tasks.repository.SessionRepository;
import app.tasks.service.AuthService;
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
    private final AuthService authService;

    public SessionHandler(SessionRepository sessionRepository, AuthService authService) {

        this.sessionRepository = sessionRepository;
        this.authService = authService;
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
        String userId = authService.isAuthenticated(sessionToken);
        return sessionRepository.findByUserId(userId);
    }
}
