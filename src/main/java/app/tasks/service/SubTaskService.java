package app.tasks.service;

import app.tasks.repository.SubTaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SubTaskService {
    @Autowired SubTaskRepository subTaskRepository;
    @Autowired TaskService taskService;

    @Transactional
    public void deleteSubTasks(List<String> subTaskIds, String taskId, String userId, long lastUpdateTs){
        subTaskRepository.deleteAllByIdInBatch(subTaskIds);
        taskService.updateLastUpdateTs(taskId, userId, false,lastUpdateTs);
    }

}
