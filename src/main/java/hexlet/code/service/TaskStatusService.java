package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;

public interface TaskStatusService {

    TaskStatus createStatus(TaskStatusDto statusData);

    TaskStatus updateStatus(Long id, TaskStatusDto statusData);
}
