package hexlet.code.app.service;

import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public Task createTask(TaskDto taskData) {
        final Task task = new Task();

        final User author = userService.getCurrentUser();
        final TaskStatus status = statusRepository.getById(taskData.getTaskStatusId());
        final User executor = userRepository.getById(taskData.getExecutorId());

        task.setName(taskData.getName());
        task.setDescription(taskData.getDescription());
        task.setTaskStatus(status);
        task.setAuthor(author);
        task.setExecutor(executor);
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Long id, TaskDto newTaskData) {
        final Task task = taskRepository.getById(id);

        final TaskStatus status = statusRepository.getById(newTaskData.getTaskStatusId());
        final User executor = userRepository.getById(newTaskData.getExecutorId());

        task.setName(newTaskData.getName());
        task.setDescription(newTaskData.getDescription());
        task.setTaskStatus(status);
        task.setExecutor(executor);
        return taskRepository.save(task);
    }
}
