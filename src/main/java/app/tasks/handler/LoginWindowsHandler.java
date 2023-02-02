package app.tasks.handler;

import app.tasks.model.SessionModel;
import app.tasks.repository.SessionRepository;
import app.tasks.repository.UserRepository;
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
    UserRepository userRepository;

    public LoginWindowsHandler(SessionRepository sessionRepository, UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/login-windows")
    public Map<String, String> login(@RequestParam String state) {
        String loginState = state.split("\\|")[0];
        Optional<SessionModel> sessionModel = sessionRepository.findByLoginState(loginState);
        if (sessionModel.isPresent()) {
            String username = userRepository.findById(sessionModel.get().getUserId()).get().getUsername();
            return Map.of("sessionToken", sessionModel.get().getSessionToken(),
                    "username", username==null ? "" : username);
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Invalid state");
    }
}