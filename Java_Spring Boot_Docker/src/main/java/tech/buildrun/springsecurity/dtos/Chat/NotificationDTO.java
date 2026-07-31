package tech.buildrun.springsecurity.dtos.Chat;

import tech.buildrun.springsecurity.entities.Chat.NotificationType;

import java.util.UUID;

public record NotificationDTO(
        UUID notificationId,
        UUID userId,
        NotificationType notificationType,
        String title,
        String content,
        UUID referenceId
) {
}