package tech.buildrun.springsecurity.services.Chat;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import tech.buildrun.springsecurity.entities.Chat.FRIENDSHIP;
import tech.buildrun.springsecurity.entities.Chat.FriendshipStatus;
import tech.buildrun.springsecurity.entities.Chat.NotificationType;
import tech.buildrun.springsecurity.entities.User;
import tech.buildrun.springsecurity.repository.FriendshipRepository;
import tech.buildrun.springsecurity.repository.UserRepository;
import tech.buildrun.springsecurity.services.AuthenticatedUserService;
import tech.buildrun.springsecurity.websocket.WebSocketNotificationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final WebSocketNotificationService webSocketNotificationService;
    private final NotificationService notificationService;
    public FriendshipService(
            FriendshipRepository friendshipRepository,
            UserRepository userRepository,
            AuthenticatedUserService authenticatedUserService,
            WebSocketNotificationService webSocketNotificationService,
            NotificationService notificationService
    ) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.webSocketNotificationService = webSocketNotificationService;
        this.notificationService = notificationService;
    }

    // =========================================================
    // ENVIAR PEDIDO DE AMIZADE
    // =========================================================

    @Transactional
    public FRIENDSHIP sendFriendRequest(UUID addresseeId) {

        User requester = authenticatedUserService.getAuthenticatedUser();

        if (requester.getUserId().equals(addresseeId)) {
            throw new RuntimeException(
                    "Você não pode enviar um pedido para si próprio."
            );
        }

        User addressee = userRepository.findById(addresseeId)
                .orElseThrow(() ->
                        new RuntimeException("Utilizador destinatário não encontrado.")
                );

        boolean exists = friendshipRepository
                .existsByRequesterAndAddressee(requester, addressee);

        if (exists) {
            throw new RuntimeException(
                    "Já existe uma relação entre estes utilizadores."
            );
        }

        boolean reverseExists = friendshipRepository
                .existsByRequesterAndAddressee(addressee, requester);

        if (reverseExists) {
            throw new RuntimeException(
                    "Este utilizador já lhe enviou um pedido de amizade."
            );
        }

        FRIENDSHIP friendship = new FRIENDSHIP();

        friendship.setRequester(requester);
        friendship.setAddressee(addressee);
        friendship.setStatus(FriendshipStatus.PENDING);
        webSocketNotificationService.sendFriendRequest(
                addressee,
                friendship.getStatus()
        );
        return friendshipRepository.save(friendship);
    }

    // =========================================================
    // ACEITAR PEDIDO
    // =========================================================

    @Transactional
    public FRIENDSHIP acceptFriendRequest(UUID friendshipId) {

        User authenticatedUser = authenticatedUserService.getAuthenticatedUser();

        FRIENDSHIP friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() ->
                        new RuntimeException("Pedido de amizade não encontrado.")
                );

        if (!friendship.getAddressee().getUserId().equals(authenticatedUser.getUserId())) {
            throw new RuntimeException(
                    "Não tem permissão para aceitar este pedido."
            );
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new RuntimeException(
                    "Este pedido já não está pendente."
            );
        }
        NotificationType notificationType = null;
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        notificationService.createNotification(friendship.getRequester().getUserId(), notificationType.NEW_MESSAGE,"Pedido de amizade.", "O seu pedido de amizade a "+ authenticatedUserService.getAuthenticatedUser() + "foi aceite!");
        Optional<User> user = userRepository.findById(friendship.getRequester().getUserId());
        webSocketNotificationService.sendNotifications(
                user.get()
        );

        return friendshipRepository.save(friendship);
    }

    // =========================================================
    // REJEITAR PEDIDO
    // =========================================================

    @Transactional
    public FRIENDSHIP rejectFriendRequest(UUID friendshipId) {

        User authenticatedUser = authenticatedUserService.getAuthenticatedUser();

        FRIENDSHIP friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() ->
                        new RuntimeException("Pedido de amizade não encontrado.")
                );

        if (!friendship.getAddressee().getUserId().equals(authenticatedUser.getUserId())) {
            throw new RuntimeException(
                    "Não tem permissão para rejeitar este pedido."
            );
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new RuntimeException(
                    "Este pedido já não está pendente."
            );
        }

        friendship.setStatus(FriendshipStatus.REJECTED);

        return friendshipRepository.save(friendship);
    }

    // =========================================================
    // BLOQUEAR UTILIZADOR
    // =========================================================

    @Transactional
    public FRIENDSHIP blockUser(UUID friendshipId) {

        User authenticatedUser = authenticatedUserService.getAuthenticatedUser();

        FRIENDSHIP friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() ->
                        new RuntimeException("Amizade não encontrada.")
                );

        if (!friendship.getRequester().getUserId().equals(authenticatedUser.getUserId())
                && !friendship.getAddressee().getUserId().equals(authenticatedUser.getUserId())) {

            throw new RuntimeException(
                    "Não tem permissão para bloquear este utilizador."
            );
        }

        friendship.setStatus(FriendshipStatus.BLOCKED);

        return friendshipRepository.save(friendship);
    }

    // =========================================================
    // PEDIDOS RECEBIDOS
    // =========================================================

    public List<FRIENDSHIP> getReceivedRequests() {

        User authenticatedUser = authenticatedUserService.getAuthenticatedUser();

        return friendshipRepository.findByAddresseeAndStatus(
                authenticatedUser,
                FriendshipStatus.PENDING
        );
    }

    // =========================================================
    // PEDIDOS ENVIADOS
    // =========================================================

    public List<FRIENDSHIP> getSentRequests() {

        User authenticatedUser = authenticatedUserService.getAuthenticatedUser();

        return friendshipRepository.findByRequesterAndStatus(
                authenticatedUser,
                FriendshipStatus.PENDING
        );
    }

    // =========================================================
    // AMIGOS
    // =========================================================

    public List<FRIENDSHIP> getAcceptedFriendships() {

        User authenticatedUser = authenticatedUserService.getAuthenticatedUser();

        List<FRIENDSHIP> friendships = new ArrayList<>();

        friendships.addAll(
                friendshipRepository.findByRequesterAndStatus(
                        authenticatedUser,
                        FriendshipStatus.ACCEPTED
                )
        );

        friendships.addAll(
                friendshipRepository.findByAddresseeAndStatus(
                        authenticatedUser,
                        FriendshipStatus.ACCEPTED
                )
        );

        return friendships;
    }

    // =========================================================
    // REMOVER AMIZADE
    // =========================================================

    @Transactional
    public void removeFriendship(UUID friendshipId) {

        User authenticatedUser = authenticatedUserService.getAuthenticatedUser();

        FRIENDSHIP friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() ->
                        new RuntimeException("Amizade não encontrada.")
                );

        if (!friendship.getRequester().getUserId().equals(authenticatedUser.getUserId())
                && !friendship.getAddressee().getUserId().equals(authenticatedUser.getUserId())) {

            throw new RuntimeException(
                    "Não tem permissão para remover esta amizade."
            );
        }

        friendshipRepository.delete(friendship);
    }

    // =========================================================
    // CONSULTAR UMA AMIZADE
    // =========================================================

    public FRIENDSHIP findFriendship(UUID addresseeId) {

        User authenticatedUser = authenticatedUserService.getAuthenticatedUser();

        User addressee = userRepository.findById(addresseeId)
                .orElseThrow(() ->
                        new RuntimeException("Utilizador não encontrado.")
                );

        return friendshipRepository
                .findByRequesterAndAddressee(authenticatedUser, addressee)
                .or(() ->
                        friendshipRepository.findByRequesterAndAddressee(addressee, authenticatedUser))
                .orElseThrow(() ->
                        new RuntimeException("Amizade não encontrada."));
    }
}