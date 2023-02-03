package app.tasks.repository;

import app.tasks.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    @Query(value = "select t from Task t where t.userId = :userId and t.id = :taskId and exists(select s from ShareModel s where s.taskId = :taskId and s.userId = :userId and s.accessType='edit')")
    List<Task> getTaskByIdAndUserIdAndCheckAccess(String taskId, String userId);
}
