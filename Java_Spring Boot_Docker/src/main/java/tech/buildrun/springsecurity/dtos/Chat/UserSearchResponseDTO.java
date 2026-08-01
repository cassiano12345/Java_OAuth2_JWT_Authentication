package tech.buildrun.springsecurity.dtos.Chat;

import tech.buildrun.springsecurity.entities.Chat.FriendshipStatus;

import java.util.UUID;

public record UserSearchResponseDTO(

        UUID userId,

        String username,

        String friendshipStatus

) {
}
