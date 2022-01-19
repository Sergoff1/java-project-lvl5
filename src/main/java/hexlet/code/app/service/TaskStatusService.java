package hexlet.code.app.service;

import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.model.TaskStatus;

public interface TaskStatusService {

    TaskStatus createStatus(TaskStatusDto statusData);

    TaskStatus updateStatus(Long id, TaskStatusDto statusData);
}
