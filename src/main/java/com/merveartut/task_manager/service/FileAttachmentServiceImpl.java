package com.merveartut.task_manager.service;

import com.merveartut.task_manager.model.FileAttachment;
import com.merveartut.task_manager.model.Task;
import com.merveartut.task_manager.repository.CommentRepository;
import com.merveartut.task_manager.repository.FileAttachmentRepository;
import com.merveartut.task_manager.repository.TaskRepository;
import com.merveartut.task_manager.repository.UserRepository;
import com.merveartut.task_manager.service.exception.FileAttachmentNotFoundException;
import com.merveartut.task_manager.service.exception.TaskNotFoundException;
import com.merveartut.task_manager.service.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class FileAttachmentServiceImpl implements FileAttachmentService{

    private final FileAttachmentRepository fileAttachmentRepository;
    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    public FileAttachmentServiceImpl(FileAttachmentRepository fileAttachmentRepository, TaskRepository taskRepository, UserRepository userRepository) {
        this.fileAttachmentRepository = fileAttachmentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }
    @Override
    public FileAttachment createFileAttachment(UUID taskId, FileAttachment fileAttachment) {
        Optional<Task> task = taskRepository.findById(taskId);
        if (task.isPresent()) {
            fileAttachment.setTask(task.get());
            return fileAttachmentRepository.save(fileAttachment);
        } else {
            throw new TaskNotFoundException();
        }
    }

    @Override
    public List<FileAttachment> listFileAttachments() {
        return fileAttachmentRepository.findAll();
    }

    @Override
    public FileAttachment getFileAttachmentById(UUID id) {
        return fileAttachmentRepository.findById(id)
                .orElseThrow(() -> new FileAttachmentNotFoundException());
    }

    @Override
    public List<FileAttachment> getFileAttachmentsByUserId(UUID userId) throws UserNotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }
        return fileAttachmentRepository.findByUserId(userId);
    }

    @Override
    public List<FileAttachment> getFileAttachmentsByTaskId(UUID taskId) throws TaskNotFoundException {
        if (!taskRepository.existsById(taskId)) {
            throw new TaskNotFoundException();
        }
        return fileAttachmentRepository.findByTaskId(taskId);
    }

}
