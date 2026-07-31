package tech.buildrun.springsecurity.dtos.Chat;

import java.util.UUID;

public record FriendshipRequestDTO(
        UUID requesterId,
        UUID addresseeId
) {
}
