package app.tasks.service;

import app.tasks.repository.TaskRepository;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static app.tasks.constants.QueryConstants.GET_TASKS_AFTER_TS;

@Component
public class TaskService {

    TaskRepository taskRepository;
    QueryService queryService;
    public TaskService(TaskRepository taskRepository, QueryService queryService){
        this.taskRepository = taskRepository;
        this.queryService = queryService;
    }

    public void updateLastUpdateTs(String taskId){
        taskRepository.updateLastUpdateTs(new Date().getTime(),taskId);
    }

    public Long getMaxUpdateTs(String userId){ return taskRepository.getMaxUpdateTs(userId);}

    public List<Map<String, Object>> getTasksAfterLastUpdateTs(long lastUpdateTs, String userId){
        return queryService.executeQueryResponse(GET_TASKS_AFTER_TS, Map.of("lastUpdateTs", lastUpdateTs,"userId",userId));
    }


}
