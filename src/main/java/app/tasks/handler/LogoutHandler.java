package app.tasks.handler;

import app.tasks.repository.SessionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogoutHandler {
    private SessionRepository sessionRepository;
    public LogoutHandler(SessionRepository sessionRepository){
        this.sessionRepository = sessionRepository;
    }

    @Operation(security = { @SecurityRequirement(name = "Authorization") })
    @GetMapping("/logout")
    public void logout(@RequestHeader("Authorization") String sessionToken){
        sessionRepository.deleteBySessionToken(sessionToken);
    }
}
