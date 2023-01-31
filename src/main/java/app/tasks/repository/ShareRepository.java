package app.tasks.repository;

import app.tasks.model.ShareModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ShareRepository extends JpaRepository<ShareModel, String> {
    void deleteByTaskIdIn(Collection<String> taskId);
    List<ShareModel> findByTaskIdInAndUserId(Collection<String> taskId, String userId);
}
