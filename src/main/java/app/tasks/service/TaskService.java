package app.tasks.service;

import app.tasks.repository.TaskRepository;

public class TaskService {

    TaskRepository taskRepository;
    public TaskService(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }

    void updateLastUpdateTs(long lastUpdateTs,String taskId){
        taskRepository.updateLastUpdateTs(lastUpdateTs,taskId);
    }



}
