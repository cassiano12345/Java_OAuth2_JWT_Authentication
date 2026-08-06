package tech.buildrun.springsecurity.dtos.Chat;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConversationListItemDTO(

        UUID conversationId,

        UUID friendId,

        String friendName,

        boolean online,

        String lastMessage,

        LocalDateTime lastMessageDate

) {}
