package app.tasks.handler;

import app.tasks.model.HTTPModels.LoginPostRequestModel;
import app.tasks.model.SessionModel;
import app.tasks.model.User;
import app.tasks.repository.SessionRepository;
import app.tasks.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
public class LoginHandler {

    SessionRepository sessionRepository;
    UserRepository userRepository;

    public LoginHandler(SessionRepository sessionRepository, UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> login(@RequestBody LoginPostRequestModel request)
            throws FirebaseAuthException {

        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(request.getIdToken(), true);
        String sessionToken = UUID.randomUUID().toString();
        Optional<User> checkUser = userRepository.findById(decodedToken.getUid());

        if (checkUser.isEmpty()) {
            User user = new User(decodedToken.getUid(), decodedToken.getName(), decodedToken.getPicture(), null);
            userRepository.save(user);
        }
        sessionRepository.save(new SessionModel(decodedToken.getUid(), request.getDeviceId(),
                request.getDeviceInfo(), decodedToken.getEmail(),
                sessionToken, new Date().toInstant().toEpochMilli()));
        String username = checkUser.map(User::getUsername).orElse("");
        return Map.of("sessionToken", sessionToken, "username", username);
    }
}
