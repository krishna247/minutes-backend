package app.tasks.handler;

import app.tasks.model.Task;
import app.tasks.repository.TaskRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class TaskHandler {
    private final TaskRepository taskRepository;

    public TaskHandler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping(value = "/task")
    public List<Task> getTask(@RequestParam String userUuid) {
        return taskRepository.findByUserUuid(userUuid);
    }

    @PostMapping(value = "/task",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> createTask(@RequestBody Task taskInput) {
        taskInput.setId(UUID.randomUUID().toString());
        taskInput.setLastUpdateTs(System.currentTimeMillis());
        taskRepository.save(taskInput);
        return Map.of("id", taskInput.getId());
    }

    @DeleteMapping(value = "/task")
    public void deleteTask(@RequestParam String taskUuid) {
        taskRepository.deleteAllById(List.of(taskUuid));
    }




}
