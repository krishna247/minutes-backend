package app.tasks.repository;

import app.tasks.model.ShareModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ShareRepository extends JpaRepository<ShareModel, String> {
    List<ShareModel> findByTaskIdInAndUserId(Collection<String> taskId, String userId);

    List<ShareModel> findByUserId(String userId);

    Optional<ShareModel> findByTaskIdAndUserId(String taskId, String userId);

    void deleteByTaskIdAndUserId(String taskId, String userId);

    @Query(value = "delete from sharing where sharing.task_id in (:taskIds)",nativeQuery = true)
    @Modifying
    @Transactional
    void deleteByTaskIdIn(Collection<String> taskIds);
}
