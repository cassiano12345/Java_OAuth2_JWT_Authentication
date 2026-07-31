package tech.buildrun.springsecurity.dtos.Chat;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record AttachmentDTO(
        UUID attachmentId,
        UUID messageId,
        MultipartFile file
) {
}