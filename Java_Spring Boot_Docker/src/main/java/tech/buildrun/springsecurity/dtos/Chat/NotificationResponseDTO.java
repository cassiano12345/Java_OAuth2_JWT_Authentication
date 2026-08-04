package tech.buildrun.springsecurity.dtos.Chat;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponseDTO(

        UUID notificationId,

        UUID receiverId,

        String title,

        String content,

        boolean read,

        LocalDateTime createdAt

) {
}