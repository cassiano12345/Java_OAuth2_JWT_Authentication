package tech.buildrun.springsecurity.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tech.buildrun.springsecurity.entities.User;

@Service
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationService(SimpMessagingTemplate messagingTemplate) {

        this.messagingTemplate = messagingTemplate;

    }

    /*
    |--------------------------------------------------------------------------
    | Utilizador conectado
    |--------------------------------------------------------------------------
    */

    public void userConnected(User user) {

        System.out.println(user.getUsername() + " conectou-se ao WebSocket.");

    }

    /*
    |--------------------------------------------------------------------------
    | Notificações
    |--------------------------------------------------------------------------
    */

    public void sendNotifications(User user) {

        messagingTemplate.convertAndSendToUser(

                user.getUsername(),

                "/queue/notifications",

                "refresh"

        );

    }

    /*
    |--------------------------------------------------------------------------
    | Pedidos de amizade
    |--------------------------------------------------------------------------
    */

    public void sendFriendRequests(User user) {

        messagingTemplate.convertAndSendToUser(

                user.getUsername(),

                "/queue/friendships",

                "refresh"

        );

    }

    /*
    |--------------------------------------------------------------------------
    | Amigos Online
    |--------------------------------------------------------------------------
    */

    public void sendOnlineFriends(User user) {

        messagingTemplate.convertAndSendToUser(

                user.getUsername(),

                "/queue/friends",

                "refresh"

        );

    }

    /*
    |--------------------------------------------------------------------------
    | Mensagem privada
    |--------------------------------------------------------------------------
    */

    public void sendPrivateMessage(User receiver, Object message) {

        messagingTemplate.convertAndSendToUser(

                receiver.getUsername(),

                "/queue/messages",

                message

        );

    }

    /*
    |--------------------------------------------------------------------------
    | Nova notificação
    |--------------------------------------------------------------------------
    */

    public void sendNotification(User receiver, Object notification) {

        messagingTemplate.convertAndSendToUser(

                receiver.getUsername(),

                "/queue/notifications",

                notification

        );

    }

    /*
    |--------------------------------------------------------------------------
    | Pedido de amizade recebido
    |--------------------------------------------------------------------------
    */

    public void sendFriendRequest(User receiver, Object request) {

        messagingTemplate.convertAndSendToUser(

                receiver.getUsername(),

                "/queue/friendships",

                request

        );

    }

    /*
    |--------------------------------------------------------------------------
    | Evento para um grupo
    |--------------------------------------------------------------------------
    */

    public void sendGroupEvent(String groupId, Object payload) {

        messagingTemplate.convertAndSend(

                "/topic/groups/" + groupId,

                payload

        );

    }

}
