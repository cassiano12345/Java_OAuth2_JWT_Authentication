package tech.buildrun.springsecurity.dtos.Chat;

import tech.buildrun.springsecurity.entities.Chat.MessageType;

import java.time.LocalDateTime;
import java.util.UUID;

public record Message_listDTO(
        UUID messageId,

                              UUID senderId,

                              String senderName,

                              MessageType messageType,

                              String content,

                              LocalDateTime sentAt,

                              LocalDateTime editedAt,

                              LocalDateTime readAt,

                              boolean mine) {
}
