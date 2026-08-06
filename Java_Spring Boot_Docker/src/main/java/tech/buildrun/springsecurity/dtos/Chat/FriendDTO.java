package tech.buildrun.springsecurity.dtos.Chat;

import java.util.UUID;

public record FriendDTO(

        UUID userId,

        String username,

        UUID conversationId,

        boolean online
) {
}
