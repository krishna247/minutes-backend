package app.tasks.repository;

import app.tasks.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findByUserId(String userUuid);
    List<Task> findByShareModelUserId(String userId);
    Task findByIdAndShareModelUserId(String taskId, String userId);

    Task findTaskByIdAndUserId(String taskUuid, String userId);
    Task findTaskById(String taskId);
    void deleteByIdIn(Collection<String> taskIds);

    String GET_TASKS_WITH_ACCESS = """
                select t
                from Task as t
                where t.shareModel.userId = :userId
            """;

//    @Query(
//            value = GET_TASKS_WITH_ACCESS,
//            nativeQuery = false)
//    List<Task> getTasksWithAccess(@Param("userId") String userId); //
}
