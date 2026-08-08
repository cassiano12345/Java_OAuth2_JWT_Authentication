package tech.buildrun.springsecurity.dtos.Chat;

import tech.buildrun.springsecurity.entities.Chat.ConversationMemberRole;

import java.util.UUID;

public record Add_member_conversationDTO(UUID conversationId,
                                         UUID userId,
                                         ConversationMemberRole role) {
}
