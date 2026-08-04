package tech.buildrun.springsecurity.dtos.Chat;

import tech.buildrun.springsecurity.entities.Chat.NotificationType;

import java.util.UUID;

public record NotificationCreateRequestDTO(

        UUID receiverId,

        NotificationType notificationType,

        String title,

        String content
) {
}
