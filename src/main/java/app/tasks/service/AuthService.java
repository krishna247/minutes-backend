package app.tasks.service;

import app.tasks.model.SessionModel;
import app.tasks.repository.SessionRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AuthService {
    final SessionRepository sessionRepository;

    public AuthService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Cacheable("sessions")
    public String isAuthenticated(String sessionToken) {
        List<SessionModel> result = sessionRepository.findBySessionToken(sessionToken);
        if (result.size() > 0) {
            System.out.println("Authorized");
            return result.get(0).getUserId();
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    @CacheEvict(value = "sessions", allEntries = true)
    @Scheduled(fixedRateString = "${cache.ttl}")
    public void emptyCache() {
        System.out.println("emptying sessions cache");
    }
}
