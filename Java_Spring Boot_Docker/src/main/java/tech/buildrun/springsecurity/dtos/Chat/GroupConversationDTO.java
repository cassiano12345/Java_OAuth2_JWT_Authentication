package tech.buildrun.springsecurity.dtos.Chat;

import java.time.LocalDateTime;
import java.util.UUID;

public record GroupConversationDTO(UUID conversationId,
                                   String groupName,
                                   String lastMessage,
                                   LocalDateTime lastMessageAt,
                                   long unreadCount) {
}
