package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TaskStatusServiceImpl implements TaskStatusService {

    @Autowired
    private TaskStatusRepository statusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public TaskStatus createStatus(TaskStatusDto statusData) {
        final TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(statusData.getName());
        return statusRepository.save(taskStatus);
    }

    @Override
    public TaskStatus updateStatus(Long id, TaskStatusDto statusData) {
        final TaskStatus taskStatus = statusRepository.getById(id);
        taskStatus.setName(statusData.getName());
        return statusRepository.save(taskStatus);
    }

    @Override
    public void deleteStatus(Long id) {
        Optional<Task> taskWithThisStatus = taskRepository.findFirst1ByTaskStatusId(id);

        if (taskWithThisStatus.isPresent()) {
            throw new DataIntegrityViolationException("Unable to delete the status associated with an existing task");
        }
        statusRepository.deleteById(id);
    }
}
