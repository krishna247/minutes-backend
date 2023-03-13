package app.tasks.repository;

import app.tasks.model.SubTask;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static app.tasks.constants.QueryConstants.GET_SUBTASKS_WITH_ACCESS;

@Repository
public interface SubTaskRepository extends JpaRepository<SubTask, String> {

    @Query(value = GET_SUBTASKS_WITH_ACCESS, nativeQuery = true)
    List<SubTask> getSubTaskWithAccess(List<String> subTaskIds, String userId, List<String> accessTypes);

    @Query(value = "delete from sub_task where task_id=:taskId",nativeQuery = true)
    @Modifying
    @Transactional
    void deleteAllByTaskId(String taskId);

}
