package tech.buildrun.springsecurity.dtos.Chat;

import tech.buildrun.springsecurity.entities.Chat.MessageType;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageResponseDTO(
        UUID messageId,
        UUID conversationId,
        UUID senderId,
        String username,
        MessageType messageType,
        String content,
        LocalDateTime sentAt,
        LocalDateTime editedAt,
        LocalDateTime deletedAt
) {
}