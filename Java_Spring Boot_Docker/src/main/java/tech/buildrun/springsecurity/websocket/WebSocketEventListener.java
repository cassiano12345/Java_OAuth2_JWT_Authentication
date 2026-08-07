package tech.buildrun.springsecurity.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import tech.buildrun.springsecurity.dtos.Chat.FriendDTO;
import tech.buildrun.springsecurity.entities.User;
import tech.buildrun.springsecurity.repository.UserRepository;
import tech.buildrun.springsecurity.services.Chat.FriendshipService;
import tech.buildrun.springsecurity.services.PresenceService;

import java.util.List;

@Component
public class WebSocketEventListener {

    private static final Logger logger =
            LoggerFactory.getLogger(WebSocketEventListener.class);
    private final PresenceService presenceService;
    private final WebSocketNotificationService webSocketNotificationService;
    private final FriendshipService friendshipService;
    private final UserRepository userRepository;
    public WebSocketEventListener(PresenceService presenceService, WebSocketNotificationService webSocketNotificationService, FriendshipService friendshipService, UserRepository userRepository) {
        this.presenceService = presenceService;
        this.webSocketNotificationService = webSocketNotificationService;
        this.friendshipService = friendshipService;
        this.userRepository = userRepository;
    }

    @EventListener
    public void handleSessionConnect(SessionConnectEvent event) {

        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) accessor.getUser();

        if (authentication == null) {
            logger.warn("Tentativa de conexão WebSocket sem autenticação.");
            return;
        }

        User user = (User) authentication.getPrincipal();

        logger.info("WebSocket CONNECT -> {}", user.getUsername());

    }

    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {

        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) accessor.getUser();

        if (authentication == null) {
            return;
        }

        User user = (User) authentication.getPrincipal();

        presenceService.setOnline(user.getUserId());
        friendshipService.getFriendUsernames(user).forEach(
                webSocketNotificationService::sendOnlineFriends
        );

        logger.info("WebSocket CONNECTED -> {}", user.getUsername());

    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {

        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) accessor.getUser();

        if (authentication == null) {
            return;
        }

        User user = (User) authentication.getPrincipal();
        presenceService.setOffline(user.getUserId());
        friendshipService.getFriendUsernames(user).forEach(
                webSocketNotificationService::sendOnlineFriends
        );
        logger.info("WebSocket DISCONNECTED -> {}", user.getUsername());

        // Aqui futuramente:
        // onlineUserService.userDisconnected(user);
        // notificationService.notifyFriendsUserOffline(user);

    }

}