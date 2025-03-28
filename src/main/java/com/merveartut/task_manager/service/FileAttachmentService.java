package com.merveartut.task_manager.service;

import com.merveartut.task_manager.model.FileAttachment;

import java.util.List;
import java.util.UUID;

public interface FileAttachmentService {
    FileAttachment createFileAttachment(UUID taskId, FileAttachment fileAttachment);

    List<FileAttachment> listFileAttachments();

    FileAttachment getFileAttachmentById(UUID id);

    List<FileAttachment> getFileAttachmentsByUserId(UUID userId);

    List<FileAttachment> getFileAttachmentsByTaskId(UUID taskId);

}
