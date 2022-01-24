package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository statusRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private LabelRepository labelRepository;

    @Override
    public Task createTask(TaskDto taskData) {
        final Task task = new Task();

        final User author = userService.getCurrentUser();
        final TaskStatus status = statusRepository.getById(taskData.getTaskStatusId());
        final List<Label> labels = labelRepository.findAllById(taskData.getLabelIds());

        final Long executorId = taskData.getExecutorId();
        if (executorId != null) {
            task.setExecutor(userRepository.getById(executorId));
        }

        task.setName(taskData.getName());
        task.setDescription(taskData.getDescription());
        task.setTaskStatus(status);
        task.setAuthor(author);
        task.setLabels(labels);
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Long id, TaskDto newTaskData) {
        final Task task = taskRepository.getById(id);

        final TaskStatus status = statusRepository.getById(newTaskData.getTaskStatusId());
        final List<Label> labels = labelRepository.findAllById(newTaskData.getLabelIds());

        final Long executorId = newTaskData.getExecutorId();
        if (executorId != null) {
            task.setExecutor(userRepository.getById(executorId));
        }

        task.setName(newTaskData.getName());
        task.setDescription(newTaskData.getDescription());
        task.setTaskStatus(status);
        task.setLabels(labels);
        return taskRepository.save(task);
    }
}
