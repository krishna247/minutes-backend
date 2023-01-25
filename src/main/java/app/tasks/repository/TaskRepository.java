package app.tasks.repository;

import app.tasks.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findByUserUuid(String userUuid);

    Task findTaskByIdAndUserUuid(String taskUuid, String userUuid);
    Task findTaskById(String taskId);
    void deleteByIdIn(Collection<String> taskIds);
}
