package app.tasks.repository;

import app.tasks.model.SessionModel;
import app.tasks.model.idModel.SessionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<SessionModel, SessionId> {
    List<SessionModel> findBySessionToken(String sessionToken);

    Optional<SessionModel> findByLoginState(String loginState);

    void deleteBySessionToken(String sessionToken);

    List<SessionModel> findByUserId(String userId);
}
