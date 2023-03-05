package app.tasks.repository;

import app.tasks.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.Future;

import static app.tasks.constants.QueryConstants.GET_MAX_UPDATE_TS;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    @Query(value = "select t from Task t where t.userId = :userId and t.id = :taskId and exists(select s from ShareModel s where s.taskId = :taskId and s.userId = :userId and s.accessType in (app.tasks.enums.AccessType.EDIT,app.tasks.enums.AccessType.OWN))")
    List<Task> getTaskByIdAndUserIdAndCheckAccess(String taskId, String userId);
    @Modifying
    @Query(value = "update task set last_update_ts=:lastUpdateTs where id=:taskId",nativeQuery = true)
    void updateLastUpdateTs(long lastUpdateTs,String taskId);

    @Modifying
    @Query(value = "update task set is_deleted=TRUE where id=:taskId",nativeQuery = true)
    void deleteTask(String taskId);

    @Query(value = GET_MAX_UPDATE_TS,nativeQuery = true)
    Long getMaxUpdateTs(String userId);

    @Async
    @Query(value = GET_MAX_UPDATE_TS,nativeQuery = true)
    Future<Long> getMaxUpdateTsAsync(String userId);

}
