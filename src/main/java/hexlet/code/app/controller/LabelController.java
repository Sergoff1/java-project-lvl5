package hexlet.code.app.controller;

import hexlet.code.app.dto.LabelDto;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.service.LabelService;
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

import static hexlet.code.app.controller.LabelController.LABEL_CONTROLLER_PATH;

@AllArgsConstructor
@RestController
@RequestMapping("{base-url}" + LABEL_CONTROLLER_PATH)
public class LabelController {

    public static final String LABEL_CONTROLLER_PATH = "/labels";

    private final LabelService labelService;

    private final LabelRepository labelRepository;

    private final TaskRepository taskRepository;

    @GetMapping
    public List<Label> getAll() {
       return labelRepository.findAll();
    }

    @GetMapping(path = "/{id}")
    public Label getLabelById(@PathVariable final Long id) {
        return labelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No labels with such id"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Label createLabel(@RequestBody @Valid LabelDto labelData) {
        return labelService.createLabel(labelData);
    }

    @PutMapping(path = "/{id}")
    public Label updateLabel(@PathVariable Long id, @RequestBody @Valid LabelDto labelData) {
        return labelService.updateLabel(id, labelData);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteLabel(@PathVariable Long id) {
        final Label label = labelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No labels with such id"));
        List<Task> tasksWithCurrentLabel = taskRepository.findByLabels(label);

        if (!tasksWithCurrentLabel.isEmpty()) {
            throw new DataIntegrityViolationException("Unable to delete the label associated with an existing task");
        }
        labelRepository.deleteById(id);
    }
}
