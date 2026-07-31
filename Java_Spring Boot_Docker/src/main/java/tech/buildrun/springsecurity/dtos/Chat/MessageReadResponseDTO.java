package tech.buildrun.springsecurity.dtos.Chat;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageReadResponseDTO(
        UUID messageReadId,
        UUID messageId,
        UUID userId,
        String username,
        LocalDateTime readAt
) {
}