package tech.buildrun.springsecurity.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tech.buildrun.springsecurity.dtos.Chat.Message_listDTO;
import tech.buildrun.springsecurity.entities.User;

import java.util.List;
import java.util.UUID;

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
    public void pedido_de_amizade_aceite(User user, Object resposta) {

        messagingTemplate.convertAndSendToUser(

                user.getUsername(),

                "/queue/notifications/amizade_aceite",

                resposta

        );
    }

    public void pedido_de_amizade_recebido(User user, Object resposta) {

        messagingTemplate.convertAndSendToUser(

                user.getUsername(),

                "/queue/notifications",

                resposta

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

    public void sendOnlineFriends(String username) {

        messagingTemplate.convertAndSendToUser(

                username,

                "/queue/friends",

                "refresh"

        );
    }
    public void sendOnlineFriends_amizade_aceite(String username, Object nova_lista) {

        messagingTemplate.convertAndSendToUser(

                username,

                "/queue/friends/amizade_aceite",

                nova_lista

        );
    }

    /*
    |--------------------------------------------------------------------------
    | Mensagens
    |--------------------------------------------------------------------------
    */

    public void sendPrivateMessage(User receiver) {

        messagingTemplate.convertAndSendToUser(

                receiver.getUsername(),

                "/queue/messages/mensagens",

                "refresh"

        );

    }

    public void sendPrivateM(User receiver, Object mensagens_nlidas) {

        messagingTemplate.convertAndSendToUser(

                receiver.getUsername(),

                "/queue/messages/privadas",

                mensagens_nlidas

        );

    }

    public void atualizacao_num_msglidas(User receiver, List<Message_listDTO> num_mensagens_lidas) {

        messagingTemplate.convertAndSendToUser(

                receiver.getUsername(),

                "/queue/messages/atualizacao_num_msglidas",

                num_mensagens_lidas

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
