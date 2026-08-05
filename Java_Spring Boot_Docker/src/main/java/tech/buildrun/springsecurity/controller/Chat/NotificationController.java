package tech.buildrun.springsecurity.controller.Chat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.buildrun.springsecurity.dtos.Chat.NotificationCreateRequestDTO;
import tech.buildrun.springsecurity.dtos.Chat.NotificationDTO;
import tech.buildrun.springsecurity.dtos.Chat.NotificationResponseDTO;
import tech.buildrun.springsecurity.services.Chat.NotificationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /*
    |--------------------------------------------------------------------------
    | Criar notificação
    |--------------------------------------------------------------------------
    */
/*
    @PostMapping("/create")
    public ResponseEntity<NotificationResponseDTO> createNotification(
            @RequestBody NotificationCreateRequestDTO dto
    ) {

        return ResponseEntity.ok(

                notificationService.createNotification(

                        dto.receiverId(),
                        dto.notificationType(),
                        dto.title(),
                        dto.content()
                )

        );

    }
*/
    /*
    |--------------------------------------------------------------------------
    | As minhas notificações
    |--------------------------------------------------------------------------
    */

    @GetMapping("/my")
    public ResponseEntity<List<NotificationResponseDTO>> getMyNotifications() {

        return ResponseEntity.ok(

                notificationService.getMyNotifications()

        );

    }

    /*
    |--------------------------------------------------------------------------
    | Marcar como lida
    |--------------------------------------------------------------------------
    */

    @PutMapping("/mark-as-read")
    public ResponseEntity<NotificationResponseDTO> markAsRead(
            @RequestBody NotificationDTO dto
    ) {

        return ResponseEntity.ok(

                notificationService.markAsRead(
                        dto.notificationId()
                )

        );

    }

    /*
    |--------------------------------------------------------------------------
    | Marcar todas como lidas
    |--------------------------------------------------------------------------
    */

    @PutMapping("/mark-all-as-read")
    public ResponseEntity<Void> markAllAsRead() {

        notificationService.markAllAsRead();

        return ResponseEntity.noContent().build();

    }
        /*
    |--------------------------------------------------------------------------
    | Apagar uma notificação
    |--------------------------------------------------------------------------
    */
        @DeleteMapping("/{notificationId}")
        public ResponseEntity<Void> deleteNotification(
                @PathVariable UUID notificationId
        ) {

            notificationService.deleteNotification(notificationId);

            return ResponseEntity.noContent().build();

        }

    /*
|--------------------------------------------------------------------------
| Apagar todas notificações
|--------------------------------------------------------------------------
*/
    @DeleteMapping("/delete-all")
    public ResponseEntity<Void> deleteAllNotifications() {

        notificationService.deleteAllNotifications();

        return ResponseEntity.noContent().build();

    }

}