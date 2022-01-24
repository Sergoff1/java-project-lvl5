package hexlet.code.controller;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
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

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.NoSuchElementException;

import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;


@RestController
@AllArgsConstructor
@Transactional
@RequestMapping("${base-url}" + TASK_CONTROLLER_PATH)
public class TaskController {

    public static final String TASK_CONTROLLER_PATH = "/tasks";

    private static final String ONLY_OWNER_BY_ID = """
            @taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.getName()
        """;

    private final TaskRepository taskRepository;

    private final TaskService taskService;

    @Operation(summary = "Get list of Tasks by predicate")
    @ApiResponse(responseCode = "200", description = "List of Tasks by predicate", content =
        @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class)))
    @GetMapping
    public Iterable<Task> getAll(
            @Parameter(description = "predicate")
            @QuerydslPredicate(root = Task.class) Predicate predicate) {
        return taskRepository.findAll(predicate);
    }

    @Operation(summary = "Get specific Task by it id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task found", content =
                @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "404", description = "No Tasks with such id")
    })
    @GetMapping(path = "/{id}")
    public Task getTaskById(
            @Parameter(description = "Id of Task to be found", required = true)
            @PathVariable Long id) {

        return taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No Tasks with such id"));
    }

    @Operation(summary = "Create new Task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created", content =
                @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "422", description = "Data validation failed", content =
                @Content(mediaType = "application/json"))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(
            @Parameter(description = "Data of Task to be created", required = true)
            @RequestBody @Valid TaskDto taskData) {

        return taskService.createTask(taskData);
    }

    @Operation(summary = "Update Task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated", content =
                @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "422", description = "Data validation failed"),
            @ApiResponse(responseCode = "404", description = "No Tasks with such id")
    })
    @PutMapping(path = "/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public Task updateTask(
            @Parameter(description = "Id of Task to be updated", required = true)
            @PathVariable Long id,
            @Parameter(description = "Task data to save", required = true)
            @RequestBody @Valid TaskDto taskData) {

        return taskService.updateTask(id, taskData);
    }

    @Operation(summary = "Delete Task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task deleted"),
            @ApiResponse(responseCode = "404", description = "No Tasks with such id")
    })
    @DeleteMapping(path = "/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void deleteTask(
            @Parameter(description = "Id of Task to be deleted", required = true)
            @PathVariable Long id) {

        taskRepository.deleteById(id);
    }
}
