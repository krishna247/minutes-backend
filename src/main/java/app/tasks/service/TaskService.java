package app.tasks.service;

import app.tasks.enums.AccessType;
import app.tasks.model.ShareModel;
import app.tasks.model.SubTask;
import app.tasks.model.Task;
import app.tasks.model.websocket.TaskUpdateWSModel;
import app.tasks.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static app.tasks.constants.QueryConstants.GET_TASKS_AFTER_TS;

@Component
public class TaskService {

    private final TaskRepository taskRepository;
    private final QueryService queryService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final SubTaskService subTaskService;
    private final ShareService shareService;
    private final CacheService cacheService;

    public TaskService(TaskRepository taskRepository, QueryService queryService, SimpMessagingTemplate simpMessagingTemplate, SubTaskService subTaskService, ShareService shareService, CacheService cacheService){
        this.taskRepository = taskRepository;
        this.queryService = queryService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.subTaskService = subTaskService;
        this.shareService = shareService;
        this.cacheService = cacheService;
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
        cacheService.evictCacheMaxUpdateTs(userId);
        return Map.of("id",taskInput.getId(),"localId",localId,"lastUpdateTs",taskInput.getLastUpdateTs());
    }

    @Transactional
    public Task updateTask(String userId, Task taskInput) {
        List<Task> taskObj = taskRepository.getTaskByIdAndUserIdAndCheckAccess(taskInput.getId(), userId);
        if(taskObj.get(0).getIsDeleted()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Task is deleted");
        }
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
            task.setIsDeleted(false);
            task.setLastUpdateTs(lastUpdateTs);
            taskRepository.save(task);
            cacheService.evictCacheMaxUpdateTs(userId);
            return task;
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such task");
        }
    }

    public void updateLastUpdateTs(String taskId, String userId, boolean isDeleted, long lastUpdateTs){
        // isDeleted indicates if this task is deleted
        cacheService.evictCacheMaxUpdateTs(userId);
        taskRepository.updateLastUpdateTs(lastUpdateTs,taskId);
        simpMessagingTemplate.convertAndSend("/topic/task/"+taskId,new TaskUpdateWSModel(userId,taskId,isDeleted));
    }

    public void sendWSUpdate(String taskId, String userId, boolean isDeleted) {
        // isDeleted indicates if this task is deleted
        cacheService.evictCacheMaxUpdateTs(userId);
        simpMessagingTemplate.convertAndSend("/topic/task/"+taskId,new TaskUpdateWSModel(userId,taskId,isDeleted));
    }

//    public void sendWSCreate(String taskId, String userId, boolean isDeleted) {
//        // isDeleted indicates if this task is deleted
//        simpMessagingTemplate.convertAndSend("/topic/task/"+taskId,new TaskUpdateWSModel(userId,taskId,isDeleted));
//    }
    @Cacheable("maxUpdateTs")
    public Long getMaxUpdateTs(String userId){ return taskRepository.getMaxUpdateTs(userId);}

    public List<Map<String, Object>> getTasksAfterLastUpdateTs(long lastUpdateTs, String userId){
        return queryService.executeQueryResponse(GET_TASKS_AFTER_TS, Map.of("lastUpdateTs", lastUpdateTs,"userId",userId));
    }


    public Map<String, Long> performLocalSyncBulk(List<Task> tasks, Map<String,List<SubTask>> subTasksMap,
                                                  Map<String,List<ShareModel>> sharesMap, String userId){
        Map<String, Long> successTasks = new HashMap<>();
        for( Task task: tasks){
            String taskId = task.getId();
            try{
                long lastUpdateTs = performLocalSync(task, subTasksMap.get(taskId), sharesMap.get(taskId),userId);
                this.sendWSUpdate(task.getId(),userId,false);
                successTasks.put(taskId,lastUpdateTs);
            }
            catch (Exception e){
                System.out.println("Failed updating taskId:"+taskId);
                System.out.println(e.getMessage());
            }
        }
        return successTasks;
    }

    @Transactional
    public Long performLocalSync(Task task, List<SubTask> subTasks, List<ShareModel> shares, String userId){
        subTaskService.deleteAllSubTasksForTask(task.getId());
        for(SubTask subTask : subTasks) {
            queryService.persist(subTask);
        }
        shareService.deleteAllSharesForTask(task.getId());
        for(ShareModel share: shares){
            share.setTaskId(task.getId());
            queryService.persist(share);
        }
        Task newTask = this.updateTask(userId,task);
        return newTask.getLastUpdateTs();
    }

}
