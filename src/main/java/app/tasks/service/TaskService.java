package app.tasks.service;

import app.tasks.model.websocket.TaskUpdateWSModel;
import app.tasks.repository.TaskRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

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

    public void updateLastUpdateTs(String taskId, String userId, boolean isDeleted){
        // isDeleted indicates if this task is deleted
        taskRepository.updateLastUpdateTs(new Date().getTime(),taskId);
        simpMessagingTemplate.convertAndSend("/topic/task/"+taskId,new TaskUpdateWSModel(userId,taskId,isDeleted));
    }

    public void sendWSUpdate(String taskId, String userId, boolean isDeleted) {
        // isDeleted indicates if this task is deleted
        simpMessagingTemplate.convertAndSend("/topic/task/"+taskId,new TaskUpdateWSModel(userId,taskId,isDeleted));
    }

    public Long getMaxUpdateTs(String userId){ return taskRepository.getMaxUpdateTs(userId);}

    public List<Map<String, Object>> getTasksAfterLastUpdateTs(long lastUpdateTs, String userId){
        return queryService.executeQueryResponse(GET_TASKS_AFTER_TS, Map.of("lastUpdateTs", lastUpdateTs,"userId",userId));
    }


}
