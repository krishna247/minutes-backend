package app.tasks.repository;

import app.tasks.model.ShareModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface ShareRepository extends JpaRepository<ShareModel, String> {
    ShareModel findByTaskId(String taskId);
    void deleteByTaskIdIn(Collection<String> taskId);
}
