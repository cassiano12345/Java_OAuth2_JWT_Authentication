package tech.buildrun.springsecurity.dtos.Chat;

import tech.buildrun.springsecurity.entities.Chat.FriendshipStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record FriendshipResponseDTO(
        UUID friendshipId,
        UUID requesterId,
        String requesterUsername,
        UUID addresseeId,
        String addresseeUsername,
        FriendshipStatus status,
        LocalDateTime createdAt
) {
}
