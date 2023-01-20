package app.tasks.handler;

import app.tasks.model.SessionModel;
import app.tasks.repository.SessionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;

@RestController
public class LoginWindowsHandler {
    SessionRepository sessionRepository;

    public LoginWindowsHandler(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @GetMapping("/login-windows")
    public Map<String, String> login(@RequestParam String state) {

        Optional<SessionModel> sessionModel = sessionRepository.findByDeviceId(state);
        if (sessionModel.isPresent()) {
            return Map.of("sessionToken", sessionModel.get().getSessionToken());
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Invalid state");
    }
}