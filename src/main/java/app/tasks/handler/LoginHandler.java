package app.tasks.handler;

import app.tasks.model.SessionModel;
import app.tasks.model.User;
import app.tasks.repository.SessionRepository;
import app.tasks.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@RestController
public class LoginHandler {

    SessionRepository sessionRepository;
    UserRepository userRepository;

    public LoginHandler(SessionRepository sessionRepository, UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestParam String idToken, @RequestParam String deviceId, @RequestParam String deviceInfo)
            throws FirebaseAuthException {

        long a = new Date().toInstant().toEpochMilli();
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken, true);
        String sessionToken = UUID.randomUUID().toString();

        if (!userRepository.existsById(decodedToken.getUid())) {
            userRepository.save(new User(decodedToken.getUid(), decodedToken.getName(), decodedToken.getPicture()));
        }
        sessionRepository.save(new SessionModel(decodedToken.getUid(), deviceId, deviceInfo, decodedToken.getEmail(),
                sessionToken, new Date().toInstant().toEpochMilli()));


        long b = new Date().toInstant().toEpochMilli();
        System.out.println((a - b) / 1000.0);
        return Map.of("sessionToken", sessionToken);
    }
}
