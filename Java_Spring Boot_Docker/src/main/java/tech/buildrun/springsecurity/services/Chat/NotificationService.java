package tech.buildrun.springsecurity.services.Chat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.buildrun.springsecurity.dtos.Chat.NotificationResponseDTO;
import tech.buildrun.springsecurity.entities.Chat.Notification;
import tech.buildrun.springsecurity.entities.Chat.NotificationType;
import tech.buildrun.springsecurity.entities.User;
import tech.buildrun.springsecurity.repository.Chat.NotificationRepository;
import tech.buildrun.springsecurity.repository.UserRepository;
import tech.buildrun.springsecurity.services.AuthenticatedUserService;
import tech.buildrun.springsecurity.websocket.WebSocketNotificationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final WebSocketNotificationService webSocketNotificationService;

    public NotificationService(
            NotificationRepository notificationRepository,
            UserRepository userRepository,
            AuthenticatedUserService authenticatedUserService,
            WebSocketNotificationService webSocketNotificationService
    ) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.webSocketNotificationService = webSocketNotificationService;
    }

    /*
    |--------------------------------------------------------------------------
    | Criar notificação
    |--------------------------------------------------------------------------
    */

    @Transactional
    public NotificationResponseDTO createNotification(
            UUID receiverId,
            NotificationType notificationType,
            String title,
            String content
    ) {

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() ->
                        new RuntimeException("Utilizador não encontrado."));

        Notification notification = new Notification();

        notification.setUser(receiver);
        notification.setNotificationType(notificationType);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notification = notificationRepository.save(notification);

        NotificationResponseDTO dto = toDTO(notification);

        webSocketNotificationService.sendNotification(receiver, dto);

        return dto;
    }

    /*
    |--------------------------------------------------------------------------
    | Minhas notificações
    |--------------------------------------------------------------------------
    */

    public List<NotificationResponseDTO> getMyNotifications() {

        User user = authenticatedUserService.getAuthenticatedUser();

        return notificationRepository
                .findByUser_UserIdOrderByCreatedAtDesc(user.getUserId())
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /*
    |--------------------------------------------------------------------------
    | Marcar como lida
    |--------------------------------------------------------------------------
    */

    @Transactional
    public NotificationResponseDTO markAsRead(UUID notificationId) {

        User user = authenticatedUserService.getAuthenticatedUser();

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() ->
                        new RuntimeException("Notificação não encontrada."));

        if (!notification.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Esta notificação não pertence ao utilizador autenticado.");
        }

        notification.setRead(true);

        notification = notificationRepository.save(notification);

        webSocketNotificationService.sendNotifications(user);

        return toDTO(notification);
    }

    /*
    |--------------------------------------------------------------------------
    | Marcar todas como lidas
    |--------------------------------------------------------------------------
    */

    @Transactional
    public void markAllAsRead() {

        User user = authenticatedUserService.getAuthenticatedUser();

        List<Notification> notifications =
                notificationRepository.findByUser_UserIdAndReadFalseOrderByCreatedAtDesc(user.getUserId());

        notifications.forEach(notification -> notification.setRead(true));

        notificationRepository.saveAll(notifications);

        webSocketNotificationService.sendNotifications(user);
    }
    // =========================================================
    // APAGAR NOTIFICAÇÃO
    // =========================================================
    @Transactional
    public void deleteNotification(UUID notificationId) {

        User user = authenticatedUserService.getAuthenticatedUser();

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() ->
                        new RuntimeException("Notificação não encontrada."));

        if (!notification.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Não tem permissão para apagar esta notificação.");
        }

        notificationRepository.delete(notification);

        webSocketNotificationService.sendNotifications(user);

    }

    // =========================================================
    // APAGAR TODAS NOTIFICAÇÕES
    // =========================================================
    @Transactional
    public void deleteAllNotifications() {
        User user = authenticatedUserService.getAuthenticatedUser();

        List<Notification> notifications = notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(user.getUserId());

        notificationRepository.deleteAll(notifications);

        webSocketNotificationService.sendNotifications(user);

    }
    /*
    |--------------------------------------------------------------------------
    | Converter Entity -> DTO
    |--------------------------------------------------------------------------
    */

    private NotificationResponseDTO toDTO(Notification notification) {

        return new NotificationResponseDTO(

                notification.getNotificationId(),

                notification.getUser().getUserId(),

                notification.getTitle(),

                notification.getContent(),

                notification.isRead(),

                notification.getCreatedAt()

        );

    }

}