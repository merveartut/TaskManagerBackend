package com.merveartut.task_manager.service;

import com.merveartut.task_manager.enums.TaskPriority;
import com.merveartut.task_manager.enums.TaskState;
import com.merveartut.task_manager.model.Project;
import com.merveartut.task_manager.model.Task;
import com.merveartut.task_manager.model.User;
import com.merveartut.task_manager.repository.ProjectRepository;
import com.merveartut.task_manager.repository.TaskRepository;
import com.merveartut.task_manager.repository.UserRepository;
import com.merveartut.task_manager.service.exception.NotAllowedStateException;
import com.merveartut.task_manager.service.exception.ProjectNotFoundException;
import com.merveartut.task_manager.service.exception.TaskNotFoundException;
import com.merveartut.task_manager.service.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService{

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    private final ProjectRepository projectRepository;

    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }
    @Override
    public Task createTask(Task task) throws ProjectNotFoundException, UserNotFoundException{
        Project project = projectRepository.findById(task.getProject().getId())
                .orElseThrow(() -> new ProjectNotFoundException());
        User assignee = userRepository.findById(task.getAssignee().getId())
                        .orElseThrow(() -> new UserNotFoundException());
        task.setState(TaskState.BACKLOG);
        task.setAssignee(assignee);
        task.setProject(project);
        return taskRepository.save(task);
    }

    @Override
    public List<Task> listTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Task getTaskById(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException());
    }

    @Override
    public TaskState getTaskState(UUID id) throws TaskNotFoundException {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException());
        return task.getState();
    }

    @Override
    public List<Task> getTasksByProjectId(UUID projectId) throws ProjectNotFoundException {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException();
        }
        return taskRepository.findByProjectId(projectId);
    }

    @Override
    public List<Task> getTasksByState(TaskState state) {
        return taskRepository.findByState(state);
    }

    @Override
    public List<Task> getTasksByPriority(TaskPriority priority) {
        return taskRepository.findByPriority(priority);
    }

    @Override
    public List<Task> getTasksByAssignee(UUID userId) throws UserNotFoundException{
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }
        return taskRepository.findByAssigneeId(userId);
    }


    @Override
    public Task updateTask(UUID id, Task task) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException();
        }
        return taskRepository.save(task);
    }

    @Override
    public Task setTaskState(UUID id, TaskState state, String reason) throws TaskNotFoundException {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException());
        if (task.getState() == TaskState.COMPLETED) {
            throw new NotAllowedStateException("Illegal State Exception");
        }
        switch (state) {
            case BACKLOG:
            case IN_ANALYSIS:
            case IN_DEVELOPMENT:
            case COMPLETED:
                if (isValidAction(task.getState(), state)) {
                    task.setState(state);
                } else {
                    throw new NotAllowedStateException("Cannot change state from COMPLETED to " + state);
                }
                break;
            case CANCELLED:

                if (reason == null || reason.trim().isEmpty()) {
                    throw new IllegalArgumentException("Reason is required for CANCELLED state.");
                }
                task.setState(TaskState.CANCELLED);
                task.setReason(reason);
                break;
            case BLOCKED:
                if (reason == null || reason.trim().isEmpty()) {
                    throw new IllegalArgumentException("Reason is required for BLOCKED state.");
                }
                if (task.getState() == TaskState.IN_ANALYSIS || task.getState() == TaskState.IN_DEVELOPMENT) {
                    task.setState(TaskState.BLOCKED);
                    task.setReason(reason);
                } else {
                    throw new NotAllowedStateException("Illegal State Exception");
                }
                break;
            default:
                throw new NotAllowedStateException("Illegal State Exception");
        }
        return taskRepository.save(task);
    }

    @Override
    public Task setTaskAssignee(UUID id, UUID userId) throws TaskNotFoundException, UserNotFoundException {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());
            task.setAssignee(user);

    return taskRepository.save(task);
    }

    @Override
    public Task setTaskPriority(UUID id, TaskPriority priority) throws TaskNotFoundException {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException());
            task.setPriority(priority);
            return taskRepository.save(task);
    }


    private boolean isValidAction(TaskState currentState, TaskState newState) {
        switch (currentState) {
            case BACKLOG:
                return newState == TaskState.IN_ANALYSIS || newState == TaskState.COMPLETED || newState == TaskState.CANCELLED;
            case IN_ANALYSIS:
                return newState == TaskState.IN_DEVELOPMENT || newState == TaskState.BLOCKED || newState == TaskState.COMPLETED || newState == TaskState.CANCELLED;
            case IN_DEVELOPMENT:
                return newState == TaskState.COMPLETED || newState == TaskState.BLOCKED || newState == TaskState.CANCELLED;
            case BLOCKED:
                return newState == TaskState.IN_ANALYSIS || newState == TaskState.IN_DEVELOPMENT || newState == TaskState.CANCELLED;
            case CANCELLED:
                return false;
            case COMPLETED:
                return false;
            default:
                return false;
        }
    }
}
