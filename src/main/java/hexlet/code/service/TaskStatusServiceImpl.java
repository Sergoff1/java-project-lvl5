package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskStatusServiceImpl implements TaskStatusService {

    @Autowired
    private TaskStatusRepository statusRepository;

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
}
