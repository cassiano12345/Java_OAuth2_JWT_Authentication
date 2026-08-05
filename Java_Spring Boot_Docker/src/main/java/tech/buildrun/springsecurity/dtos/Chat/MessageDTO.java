package tech.buildrun.springsecurity.dtos.Chat;

import tech.buildrun.springsecurity.entities.Chat.MessageType;

import java.util.UUID;

public record MessageDTO(
        UUID messageId,
        UUID conversationId,
        MessageType messageType,
        String content
) {
}