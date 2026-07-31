package tech.buildrun.springsecurity.dtos.Chat;

import java.time.LocalDateTime;
import java.util.UUID;

public record AttachmentResponseDTO(
        UUID attachmentId,
        UUID messageId,
        String fileName,
        String fileUrl,
        String fileType,
        Long fileSize,
        LocalDateTime uploadedAt
) {
}