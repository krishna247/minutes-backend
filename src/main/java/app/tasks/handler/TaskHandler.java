package app.tasks.handler;

import app.tasks.model.Task;
import app.tasks.repository.TaskRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class TaskHandler {
    private final TaskRepository taskRepository;

    public TaskHandler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping(value = "/task")
    @ResponseBody
    public List<Task> getTask(@RequestParam String userUuid){
        List<Task> taskList = taskRepository.findByUserUuid(userUuid);
        return taskList;
    }
}
