package app.tasks.service;

import app.tasks.repository.TaskRepository;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TaskService {

    TaskRepository taskRepository;
    public TaskService(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }

    public void updateLastUpdateTs(String taskId){
        taskRepository.updateLastUpdateTs(new Date().getTime(),taskId);
    }

    public Long getMaxUpdateTs(String userId){ return taskRepository.getMaxUpdateTs(userId);}



}
