package tech.buildrun.springsecurity.websocket;


import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import tech.buildrun.springsecurity.entities.User;


@Controller
public class WebSocketNotificationController {

    private final WebSocketNotificationService notificationService;

    public WebSocketNotificationController(WebSocketNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Utilizador informa o servidor que ficou online.
     */
    @MessageMapping("/presence.online")
    public void userOnline(UsernamePasswordAuthenticationToken authentication) {

        User user = (User) authentication.getPrincipal();

        notificationService.userConnected(user);

    }

    /**
     * O cliente pede para atualizar as notificações.
     */
    @MessageMapping("/notifications.refresh")
    public void refreshNotifications(UsernamePasswordAuthenticationToken authentication) {

        User user = (User) authentication.getPrincipal();

        notificationService.sendNotifications(user);

    }

    /**
     * Atualiza pedidos de amizade.
     */
    @MessageMapping("/friendships.refresh")
    public void refreshFriendships(UsernamePasswordAuthenticationToken authentication) {

        User user = (User) authentication.getPrincipal();

        notificationService.sendFriendRequests(user);

    }

    /**
     * Atualiza lista de amigos online.
     */
    @MessageMapping("/friends.online")
    public void refreshOnlineFriends(UsernamePasswordAuthenticationToken authentication) {

        User user = (User) authentication.getPrincipal();

        notificationService.sendOnlineFriends(user);

    }

}
