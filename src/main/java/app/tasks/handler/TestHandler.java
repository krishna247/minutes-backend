package app.tasks.handler;

import app.tasks.model.SessionId;
import app.tasks.model.SessionModel;
import app.tasks.repository.SessionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import static app.tasks.utils.AuthUtils.isAuthenticated;

@RestController
public class TestHandler {

    SessionRepository sessionRepository;
    public TestHandler(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }


    @GetMapping("/test")
    public SessionModel getTest(@RequestHeader("Authorization") String sessionToken){
        isAuthenticated(sessionToken,sessionRepository);
        return sessionRepository.findById(new SessionId("test_user", "test_device")).get();
    }
}
