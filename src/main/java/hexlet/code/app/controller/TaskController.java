package hexlet.code.app.controller;

import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.model.Task;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

import static hexlet.code.app.controller.TaskController.TASK_CONTROLLER_PATH;


@RestController
@AllArgsConstructor
@RequestMapping("{$base-url}" + TASK_CONTROLLER_PATH)
public class TaskController {

    public static final String TASK_CONTROLLER_PATH = "/tasks";

    private static final String ONLY_OWNER_BY_ID = """
            @userRepository.findById(#id).get().getEmail() == authentication.getName()
        """;

    private final TaskRepository taskRepository;

    private final TaskService taskService;

    @GetMapping
    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    @GetMapping(path = "/{id}")
    public Task getTaskById(@PathVariable Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No Tasks with such id"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@RequestBody @Valid TaskDto taskData) {
        return taskService.createTask(taskData);
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public Task updateTask(@PathVariable Long id, @RequestBody @Valid TaskDto taskData) {
        return taskService.updateTask(id, taskData);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
    }
}
