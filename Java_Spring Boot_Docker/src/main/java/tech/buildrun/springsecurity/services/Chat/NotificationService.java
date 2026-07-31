package tech.buildrun.springsecurity.services.Chat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.buildrun.springsecurity.entities.Chat.Notification;
import tech.buildrun.springsecurity.entities.Chat.NotificationType;
import tech.buildrun.springsecurity.entities.User;
import tech.buildrun.springsecurity.repository.Chat.NotificationRepository;
import tech.buildrun.springsecurity.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            UserRepository userRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }


    // Criar uma notificação
    @Transactional
    public Notification createNotification(
            UUID userId,
            NotificationType notificationType,
            String title,
            String content,
            UUID referenceId
    ) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuário não encontrado."
                        )
                );

        Notification notification = new Notification();

        notification.setUser(user);
        notification.setNotificationType(notificationType);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setReferenceId(referenceId);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }


    // Buscar uma notificação pelo ID
    public Notification findById(
            UUID notificationId
    ) {

        return notificationRepository
                .findById(notificationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Notificação não encontrada."
                        )
                );
    }


    // Buscar todas as notificações de um usuário
    public List<Notification> findByUser(
            UUID userId
    ) {

        return notificationRepository
                .findByUser_UserIdOrderByCreatedAtDesc(
                        userId
                );
    }


    // Buscar notificações não lidas
    public List<Notification> findUnreadByUser(
            UUID userId
    ) {

        return notificationRepository
                .findByUser_UserIdAndReadFalseOrderByCreatedAtDesc(
                        userId
                );
    }


    // Buscar notificações lidas
    public List<Notification> findReadByUser(
            UUID userId
    ) {

        return notificationRepository
                .findByUser_UserIdAndReadTrueOrderByCreatedAtDesc(
                        userId
                );
    }


    // Contar notificações não lidas
    public long countUnread(
            UUID userId
    ) {

        return notificationRepository
                .countByUser_UserIdAndReadFalse(
                        userId
                );
    }


    // Contar todas as notificações
    public long countAll(
            UUID userId
    ) {

        return notificationRepository
                .countByUser_UserId(
                        userId
                );
    }


    // Buscar notificações por tipo
    public List<Notification> findByType(
            UUID userId,
            NotificationType notificationType
    ) {

        return notificationRepository
                .findByUser_UserIdAndNotificationTypeOrderByCreatedAtDesc(
                        userId,
                        notificationType
                );
    }


    // Buscar notificações por referência
    public List<Notification> findByReference(
            UUID userId,
            UUID referenceId
    ) {

        return notificationRepository
                .findByUser_UserIdAndReferenceIdOrderByCreatedAtDesc(
                        userId,
                        referenceId
                );
    }


    // Marcar uma notificação como lida
    @Transactional
    public Notification markAsRead(
            UUID notificationId,
            UUID userId
    ) {

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Notificação não encontrada."
                        )
                );

        // Garantir que a notificação pertence ao usuário
        if (!notification.getUser().getUserId().equals(userId)) {
            throw new RuntimeException(
                    "Você não tem permissão para alterar esta notificação."
            );
        }

        notification.setRead(true);

        return notificationRepository.save(notification);
    }


    // Marcar uma notificação como não lida
    @Transactional
    public Notification markAsUnread(
            UUID notificationId,
            UUID userId
    ) {

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Notificação não encontrada."
                        )
                );

        // Garantir que a notificação pertence ao usuário
        if (!notification.getUser().getUserId().equals(userId)) {
            throw new RuntimeException(
                    "Você não tem permissão para alterar esta notificação."
            );
        }

        notification.setRead(false);

        return notificationRepository.save(notification);
    }


    // Marcar todas as notificações como lidas
    @Transactional
    public void markAllAsRead(
            UUID userId
    ) {

        List<Notification> notifications =
                notificationRepository
                        .findByUser_UserIdAndReadFalseOrderByCreatedAtDesc(
                                userId
                        );

        for (Notification notification : notifications) {
            notification.setRead(true);
        }

        notificationRepository.saveAll(notifications);
    }


    // Apagar uma notificação
    @Transactional
    public void deleteNotification(
            UUID notificationId,
            UUID userId
    ) {

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Notificação não encontrada."
                        )
                );

        // Garantir que a notificação pertence ao usuário
        if (!notification.getUser().getUserId().equals(userId)) {
            throw new RuntimeException(
                    "Você não tem permissão para apagar esta notificação."
            );
        }

        notificationRepository.delete(notification);
    }
}