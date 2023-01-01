package app.tasks.handler;

import app.tasks.model.SubTask;
import app.tasks.repository.SubTaskRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class SubTaskHandler {
    private final SubTaskRepository subTaskRepository;

    public SubTaskHandler(SubTaskRepository subTaskRepository) {
        this.subTaskRepository = subTaskRepository;
    }

    @PostMapping(value = "/subtask",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> createSubTask(@RequestBody SubTask subTaskInput) {
        subTaskInput.setId(UUID.randomUUID().toString());
        subTaskInput.setLastUpdateTs(System.currentTimeMillis());
        subTaskRepository.save(subTaskInput);
        return Map.of("id", subTaskInput.getId());
    }

    @DeleteMapping(value = "/subtask")
    public void deleteSubTasks(@RequestParam List<String> subTaskUuids) {
        subTaskRepository.deleteAllById(subTaskUuids);
    }

}
