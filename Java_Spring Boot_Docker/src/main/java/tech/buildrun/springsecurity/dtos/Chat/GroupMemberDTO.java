package tech.buildrun.springsecurity.dtos.Chat;

import tech.buildrun.springsecurity.entities.Chat.ConversationMemberRole;

import java.util.UUID;

public record GroupMemberDTO(
        UUID id,
        String name,
        ConversationMemberRole role
) {}
