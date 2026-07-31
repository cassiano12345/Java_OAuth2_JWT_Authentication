package tech.buildrun.springsecurity.repository.Chat;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.buildrun.springsecurity.entities.Chat.Notification;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository
        extends JpaRepository<Notification, UUID> {

    // Buscar todas as notificações de um usuário
    List<Notification> findByUser_UserIdOrderByCreatedAtDesc(
            UUID userId
    );

    // Buscar somente notificações não lidas
    List<Notification> findByUser_UserIdAndReadFalseOrderByCreatedAtDesc(
            UUID userId
    );

    // Buscar somente notificações lidas
    List<Notification> findByUser_UserIdAndReadTrueOrderByCreatedAtDesc(
            UUID userId
    );

    // Contar notificações não lidas
    long countByUser_UserIdAndReadFalse(
            UUID userId
    );

    // Contar todas as notificações de um usuário
    long countByUser_UserId(
            UUID userId
    );

    // Buscar notificações por tipo
    List<Notification> findByUser_UserIdAndNotificationTypeOrderByCreatedAtDesc(
            UUID userId,
            tech.buildrun.springsecurity.entities.Chat.NotificationType notificationType
    );

    // Buscar notificações relacionadas a determinado objeto
    List<Notification> findByUser_UserIdAndReferenceIdOrderByCreatedAtDesc(
            UUID userId,
            UUID referenceId
    );
}