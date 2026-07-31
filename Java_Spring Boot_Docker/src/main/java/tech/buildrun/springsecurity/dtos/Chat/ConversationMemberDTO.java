package tech.buildrun.springsecurity.dtos.Chat;

import java.util.UUID;

public record ConversationMemberDTO(
        UUID conversationId,
        UUID userId
) {
}