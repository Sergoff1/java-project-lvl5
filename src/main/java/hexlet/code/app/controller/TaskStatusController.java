package hexlet.code.app.controller;

import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.service.TaskStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
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
import java.util.Optional;

import static hexlet.code.app.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;

@RestController
@AllArgsConstructor
@RequestMapping("${base-url}" + TASK_STATUS_CONTROLLER_PATH)
public class TaskStatusController {

    public static final String TASK_STATUS_CONTROLLER_PATH = "/statuses";

    private final TaskStatusRepository statusRepository;

    private final TaskStatusService statusService;

    private final TaskRepository taskRepository;

    @Operation(summary = "Get list of All Statuses")
    @ApiResponse(responseCode = "200", description = "List of all Statuses", content =
        @Content(mediaType = "application/json", schema = @Schema(implementation = TaskStatus.class)))
    @GetMapping
    public List<TaskStatus> getAll() {
        return statusRepository.findAll();
    }

    @Operation(summary = "Get specific Status by his id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status found", content =
                @Content(mediaType = "application/json", schema = @Schema(implementation = TaskStatus.class))),
            @ApiResponse(responseCode = "404", description = "No Statuses with such id")
    })
    @GetMapping(path = "/{id}")
    public TaskStatus getStatusById(
            @Parameter(description = "Id of Status to be found", required = true)
            @PathVariable final Long id) {

        return statusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No statuses with such id"));
    }

    @Operation(summary = "Create new Status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Status created", content =
                @Content(mediaType = "application/json", schema = @Schema(implementation = TaskStatus.class))),
            @ApiResponse(responseCode = "422", description = "Data validation failed", content =
                @Content(mediaType = "application/json"))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatus createStatus(
            @Parameter(description = "Data of Status to be created", required = true)
            @RequestBody @Valid TaskStatusDto statusDto) {

        return statusService.createStatus(statusDto);
    }


    @Operation(summary = "Update Status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated", content =
                @Content(mediaType = "application/json", schema = @Schema(implementation = TaskStatus.class))),
            @ApiResponse(responseCode = "422", description = "Data validation failed"),
            @ApiResponse(responseCode = "404", description = "No Statuses with such id")
    })
    @PutMapping(path = "/{id}")
    public TaskStatus updateStatus(
            @Parameter(description = "Id of Status to be updated", required = true)
            @RequestBody @Valid TaskStatusDto statusDto,
            @Parameter(description = "Status data to save", required = true)
            @PathVariable Long id) {

        return statusService.updateStatus(id, statusDto);
    }

    @Operation(summary = "Delete Status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status deleted"),
            @ApiResponse(responseCode = "404", description = "No Statuses with such id")
    })
    @DeleteMapping(path = "/{id}")
    public void deleteStatus(
            @Parameter(description = "Id of Status to be deleted", required = true)
            @PathVariable Long id) {

        Optional<Task> taskWithThisStatus = taskRepository.findFirst1ByTaskStatusId(id);

        if (taskWithThisStatus.isPresent()) {
            throw new DataIntegrityViolationException("Unable to delete the status associated with an existing task");
        }

        statusRepository.deleteById(id);
    }
}
