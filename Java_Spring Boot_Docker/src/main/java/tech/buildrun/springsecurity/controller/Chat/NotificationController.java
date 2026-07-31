package tech.buildrun.springsecurity.controller.Chat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tech.buildrun.springsecurity.dtos.Chat.NotificationDTO;
import tech.buildrun.springsecurity.dtos.Chat.NotificationResponseDTO;
import tech.buildrun.springsecurity.entities.Chat.Notification;
import tech.buildrun.springsecurity.services.Chat.NotificationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService
    ) {
        this.notificationService = notificationService;
    }


    // Criar uma notificação
    @PostMapping("/create")
    public ResponseEntity<NotificationResponseDTO> createNotification(
            @RequestBody NotificationDTO dto
    ) {

        Notification notification =
                notificationService.createNotification(
                        dto.userId(),
                        dto.notificationType(),
                        dto.title(),
                        dto.content(),
                        dto.referenceId()
                );

        return ResponseEntity.ok(
                toResponseDTO(notification)
        );
    }


    // Buscar uma notificação
    @PostMapping("/find")
    public ResponseEntity<NotificationResponseDTO> findById(
            @RequestBody NotificationDTO dto
    ) {

        Notification notification =
                notificationService.findById(
                        dto.notificationId()
                );

        return ResponseEntity.ok(
                toResponseDTO(notification)
        );
    }


    // Buscar todas as notificações de um usuário
    @PostMapping("/find-by-user")
    public ResponseEntity<List<NotificationResponseDTO>> findByUser(
            @RequestBody NotificationDTO dto
    ) {

        List<Notification> notifications =
                notificationService.findByUser(
                        dto.userId()
                );

        return ResponseEntity.ok(
                notifications.stream()
                        .map(this::toResponseDTO)
                        .toList()
        );
    }


    // Buscar notificações não lidas
    @PostMapping("/find-unread")
    public ResponseEntity<List<NotificationResponseDTO>> findUnreadByUser(
            @RequestBody NotificationDTO dto
    ) {

        List<Notification> notifications =
                notificationService.findUnreadByUser(
                        dto.userId()
                );

        return ResponseEntity.ok(
                notifications.stream()
                        .map(this::toResponseDTO)
                        .toList()
        );
    }


    // Buscar notificações lidas
    @PostMapping("/find-read")
    public ResponseEntity<List<NotificationResponseDTO>> findReadByUser(
            @RequestBody NotificationDTO dto
    ) {

        List<Notification> notifications =
                notificationService.findReadByUser(
                        dto.userId()
                );

        return ResponseEntity.ok(
                notifications.stream()
                        .map(this::toResponseDTO)
                        .toList()
        );
    }


    // Contar notificações não lidas
    @PostMapping("/count-unread")
    public ResponseEntity<Long> countUnread(
            @RequestBody NotificationDTO dto
    ) {

        long count =
                notificationService.countUnread(
                        dto.userId()
                );

        return ResponseEntity.ok(count);
    }


    // Contar todas as notificações
    @PostMapping("/count-all")
    public ResponseEntity<Long> countAll(
            @RequestBody NotificationDTO dto
    ) {

        long count =
                notificationService.countAll(
                        dto.userId()
                );

        return ResponseEntity.ok(count);
    }


    // Buscar notificações por tipo
    @PostMapping("/find-by-type")
    public ResponseEntity<List<NotificationResponseDTO>> findByType(
            @RequestBody NotificationDTO dto
    ) {

        List<Notification> notifications =
                notificationService.findByType(
                        dto.userId(),
                        dto.notificationType()
                );

        return ResponseEntity.ok(
                notifications.stream()
                        .map(this::toResponseDTO)
                        .toList()
        );
    }


    // Buscar notificações por referência
    @PostMapping("/find-by-reference")
    public ResponseEntity<List<NotificationResponseDTO>> findByReference(
            @RequestBody NotificationDTO dto
    ) {

        List<Notification> notifications =
                notificationService.findByReference(
                        dto.userId(),
                        dto.referenceId()
                );

        return ResponseEntity.ok(
                notifications.stream()
                        .map(this::toResponseDTO)
                        .toList()
        );
    }


    // Marcar como lida
    @PutMapping("/mark-as-read")
    public ResponseEntity<NotificationResponseDTO> markAsRead(
            @RequestBody NotificationDTO dto
    ) {

        Notification notification =
                notificationService.markAsRead(
                        dto.notificationId(),
                        dto.userId()
                );

        return ResponseEntity.ok(
                toResponseDTO(notification)
        );
    }


    // Marcar como não lida
    @PutMapping("/mark-as-unread")
    public ResponseEntity<NotificationResponseDTO> markAsUnread(
            @RequestBody NotificationDTO dto
    ) {

        Notification notification =
                notificationService.markAsUnread(
                        dto.notificationId(),
                        dto.userId()
                );

        return ResponseEntity.ok(
                toResponseDTO(notification)
        );
    }


    // Marcar todas como lidas
    @PutMapping("/mark-all-as-read")
    public ResponseEntity<Void> markAllAsRead(
            @RequestBody NotificationDTO dto
    ) {

        notificationService.markAllAsRead(
                dto.userId()
        );

        return ResponseEntity.noContent().build();
    }


    // Apagar uma notificação
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteNotification(
            @RequestBody NotificationDTO dto
    ) {

        notificationService.deleteNotification(
                dto.notificationId(),
                dto.userId()
        );

        return ResponseEntity.noContent().build();
    }


    // Converter Entity para Response DTO
    private NotificationResponseDTO toResponseDTO(
            Notification notification
    ) {

        return new NotificationResponseDTO(
                notification.getNotificationId(),
                notification.getUser().getUserId(),
                notification.getNotificationType(),
                notification.getTitle(),
                notification.getContent(),
                notification.getReferenceId(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}