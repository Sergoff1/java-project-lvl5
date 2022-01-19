package hexlet.code.app.controller;

import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.service.TaskStatusServiceImpl;
import lombok.AllArgsConstructor;
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

import static hexlet.code.app.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;

@RestController
@AllArgsConstructor
@RequestMapping("{$base-url}" + TASK_STATUS_CONTROLLER_PATH)
public class TaskStatusController {

    public static final String TASK_STATUS_CONTROLLER_PATH = "/statuses";

    private final TaskStatusRepository statusRepository;

    private final TaskStatusServiceImpl statusService;

    @GetMapping
    public List<TaskStatus> getAll() {
        return statusRepository.findAll();
    }

    @GetMapping(path = "/{id}")
    public TaskStatus getStatusById(@PathVariable final Long id) {
        return statusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No statuses with such id"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatus createStatus(@RequestBody @Valid TaskStatusDto statusDto) {
        return statusService.createStatus(statusDto);
    }


    @PutMapping(path = "/{id}")
    public TaskStatus updateStatus(@RequestBody @Valid TaskStatusDto statusDto, @PathVariable Long id) {
        return statusService.updateStatus(id, statusDto);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteStatus(@PathVariable Long id) {
        statusRepository.deleteById(id);
    }
}
