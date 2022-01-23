package hexlet.code.app.service;

import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
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
        final User executor = userRepository.getById(taskData.getExecutorId());
        final List<Label> labels = labelRepository.findAllById(taskData.getLabelIds());

        task.setName(taskData.getName());
        task.setDescription(taskData.getDescription());
        task.setTaskStatus(status);
        task.setAuthor(author);
        task.setExecutor(executor);
        task.setLabels(labels);
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Long id, TaskDto newTaskData) {
        final Task task = taskRepository.getById(id);

        final TaskStatus status = statusRepository.getById(newTaskData.getTaskStatusId());
        final User executor = userRepository.getById(newTaskData.getExecutorId());
        final List<Label> labels = labelRepository.findAllById(newTaskData.getLabelIds());

        task.setName(newTaskData.getName());
        task.setDescription(newTaskData.getDescription());
        task.setTaskStatus(status);
        task.setExecutor(executor);
        task.setLabels(labels);
        return taskRepository.save(task);
    }
}
