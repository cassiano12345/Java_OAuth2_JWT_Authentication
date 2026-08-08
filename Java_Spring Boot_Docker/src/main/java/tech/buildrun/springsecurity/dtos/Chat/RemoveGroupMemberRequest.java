package tech.buildrun.springsecurity.dtos.Chat;

import java.util.UUID;

public record RemoveGroupMemberRequest(UUID conversationId,
                                       UUID userId) {
}
