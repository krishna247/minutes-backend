package app.tasks.service;

import app.tasks.enums.AccessType;
import app.tasks.model.ShareModel;
import app.tasks.model.Task;
import app.tasks.model.websocket.TaskUpdateWSModel;
import app.tasks.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.Future;

import static app.tasks.constants.QueryConstants.GET_TASKS_AFTER_TS;

@Component
public class TaskService {

    private final TaskRepository taskRepository;
    private final QueryService queryService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public TaskService(TaskRepository taskRepository, QueryService queryService, SimpMessagingTemplate simpMessagingTemplate){
        this.taskRepository = taskRepository;
        this.queryService = queryService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Transactional
    public Map<String, Object> createTask(Task taskInput, String userId){
        String localId = taskInput.getId() == null ? "" : taskInput.getId();

        // overwrite server assigned attributes
        taskInput.setId(UUID.randomUUID().toString());
        taskInput.setUserId(userId);
        taskInput.setLastUpdateTs(new Date().getTime());
        taskInput.setIsDeleted(false);

        queryService.persist(taskInput);
        queryService.persist(new ShareModel(userId, taskInput.getId(), taskInput.getLastUpdateTs(), AccessType.OWN));
        return Map.of("id",taskInput.getId(),"localId",localId,"lastUpdateTs",taskInput.getLastUpdateTs());
    }

    @Transactional
    public Task updateTask(String userId, Task taskInput) {
        List<Task> taskObj = taskRepository.getTaskByIdAndUserIdAndCheckAccess(taskInput.getId(), userId);
        if (taskObj.size() == 1) {
            Long lastUpdateTs = new Date().getTime();

            Task task = taskObj.get(0);
            task.setDeadlineDate(taskInput.getDeadlineDate() == null ? task.getDeadlineDate() : taskInput.getDeadlineDate());
            task.setPriority(taskInput.getPriority() == null ? task.getPriority() : taskInput.getPriority());
            task.setTags(taskInput.getTags() == null ? task.getTags() : taskInput.getTags());
            task.setRepeatFreq(taskInput.getRepeatFreq() == null ? task.getRepeatFreq() : taskInput.getRepeatFreq());
            task.setDescription(taskInput.getDescription() == null ? task.getDescription() : taskInput.getDescription());
            task.setIsStarred(taskInput.getIsStarred() == null ? task.getIsStarred() : taskInput.getIsStarred());
            task.setIsDone(taskInput.getIsDone() == null ? task.getIsDone() : taskInput.getIsDone());
            task.setIsDeleted(taskInput.getIsDeleted() == null ? task.getIsDeleted() : taskInput.getIsDeleted());
            task.setLastUpdateTs(lastUpdateTs);
            taskRepository.save(task);
            return task;
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such task");
        }
    }

    public void updateLastUpdateTs(String taskId, String userId, boolean isDeleted, long lastUpdateTs){
        // isDeleted indicates if this task is deleted
        taskRepository.updateLastUpdateTs(lastUpdateTs,taskId);
        simpMessagingTemplate.convertAndSend("/topic/task/"+taskId,new TaskUpdateWSModel(userId,taskId,isDeleted));
    }

    public void sendWSUpdate(String taskId, String userId, boolean isDeleted) {
        // isDeleted indicates if this task is deleted
        simpMessagingTemplate.convertAndSend("/topic/task/"+taskId,new TaskUpdateWSModel(userId,taskId,isDeleted));
    }

//    public void sendWSCreate(String taskId, String userId, boolean isDeleted) {
//        // isDeleted indicates if this task is deleted
//        simpMessagingTemplate.convertAndSend("/topic/task/"+taskId,new TaskUpdateWSModel(userId,taskId,isDeleted));
//    }
    public Long getMaxUpdateTs(String userId){ return taskRepository.getMaxUpdateTs(userId);}

    public Future<Long> getMaxUpdateTsAsync(String userId){ return taskRepository.getMaxUpdateTsAsync(userId);}

    public List<Map<String, Object>> getTasksAfterLastUpdateTs(long lastUpdateTs, String userId){
        return queryService.executeQueryResponse(GET_TASKS_AFTER_TS, Map.of("lastUpdateTs", lastUpdateTs,"userId",userId));
    }

}
