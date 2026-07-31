package tech.buildrun.springsecurity.dtos.Chat;

import tech.buildrun.springsecurity.entities.Chat.ConversationType;

import java.util.UUID;

public record ConversationDTO(
        UUID conversationId,
        UUID creatorId,
        ConversationType type,
        String name
) {
}