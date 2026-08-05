package tech.buildrun.springsecurity.dtos.Chat;

import java.util.UUID;

public record MessageReadDTO(
        UUID messageReadId,
        UUID messageId,
        UUID conversationId
) {
}