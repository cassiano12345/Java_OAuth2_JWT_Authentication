package tech.buildrun.springsecurity.dtos.Chat;

import tech.buildrun.springsecurity.entities.Chat.ConversationMemberRole;

import java.util.UUID;

public record GrupoMembroDTO(UUID userId,
                             String username,
                             ConversationMemberRole role) {
}
