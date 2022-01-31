package hexlet.code.service;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class LabelServiceImpl implements LabelService {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public Label createLabel(LabelDto labelData) {
        final Label label = new Label();

        label.setName(labelData.getName());
        return labelRepository.save(label);
    }

    @Override
    public Label updateLabel(Long id, LabelDto labelData) {
        final Label label = labelRepository.getById(id);

        label.setName(labelData.getName());
        return labelRepository.save(label);
    }

    @Override
    public void deleteLabel(Long id) {
        final Label label = labelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No labels with such id"));
        List<Task> tasksWithCurrentLabel = taskRepository.findByLabels(label);

        if (!tasksWithCurrentLabel.isEmpty()) {
            throw new DataIntegrityViolationException("Unable to delete the label associated with an existing task");
        }
        labelRepository.deleteById(id);
    }
}
