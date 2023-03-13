package app.tasks.service;

import app.tasks.repository.SubTaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SubTaskService {
    @Autowired SubTaskRepository subTaskRepository;
//    @Autowired TaskService taskService;

    @Transactional
    public void deleteSubTasks(List<String> subTaskIds){
        subTaskRepository.deleteAllByIdInBatch(subTaskIds);
    }

    @Transactional
    public void deleteAllSubTasksForTask(String taskId){
        // only to be used for offline-online sync. No WS update is sent since that is sent via Task update
        // no check is made for access since task update is in same transaction and T will fail if task update fails.
        subTaskRepository.deleteAllByTaskId(taskId);

    }

}
