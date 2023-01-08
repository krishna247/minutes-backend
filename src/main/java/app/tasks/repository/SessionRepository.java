package app.tasks.repository;

import app.tasks.model.SessionId;
import app.tasks.model.SessionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<SessionModel, SessionId> {
    Optional<SessionModel> findById(SessionId sessionId);
    List<SessionModel> findBySessionToken(String sessionToken);
}
