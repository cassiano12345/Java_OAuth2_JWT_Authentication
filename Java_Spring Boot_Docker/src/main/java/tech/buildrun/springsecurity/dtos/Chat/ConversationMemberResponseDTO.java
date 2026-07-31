package tech.buildrun.springsecurity.dtos.Chat;

import tech.buildrun.springsecurity.entities.Chat.ConversationMemberRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConversationMemberResponseDTO(
        UUID memberId,
        UUID userId,
        String username,
        ConversationMemberRole role,
        LocalDateTime joinedAt
) {
}
