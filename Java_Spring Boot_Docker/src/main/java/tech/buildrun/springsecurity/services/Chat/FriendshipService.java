package tech.buildrun.springsecurity.services.Chat;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import tech.buildrun.springsecurity.dtos.Chat.FriendDTO;
import tech.buildrun.springsecurity.dtos.Chat.NotificationResponseDTO;
import tech.buildrun.springsecurity.dtos.Chat.Notificão_aceitar_amizade;
import tech.buildrun.springsecurity.entities.Chat.*;
import tech.buildrun.springsecurity.entities.User;
import tech.buildrun.springsecurity.repository.Chat.ConversationRepository;
import tech.buildrun.springsecurity.repository.Chat.NotificationRepository;
import tech.buildrun.springsecurity.repository.FriendshipRepository;
import tech.buildrun.springsecurity.repository.UserRepository;
import tech.buildrun.springsecurity.services.AuthenticatedUserService;
import tech.buildrun.springsecurity.services.PresenceService;
import tech.buildrun.springsecurity.websocket.WebSocketNotificationService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final WebSocketNotificationService webSocketNotificationService;
    private final NotificationService notificationService;
    private final PresenceService presenceService;
    private NotificationType notificationType;
    private final ConversationService conversationService;
    private final ConversationRepository conversationRepository;
    private final NotificationRepository notificationRepository;
    public FriendshipService(
            FriendshipRepository friendshipRepository,
            UserRepository userRepository,
            AuthenticatedUserService authenticatedUserService,
            WebSocketNotificationService webSocketNotificationService,
            NotificationService notificationService, PresenceService presenceService, ConversationService conversationService, ConversationRepository conversationRepository, NotificationRepository notificationRepository
    ) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.webSocketNotificationService = webSocketNotificationService;
        this.notificationService = notificationService;
        this.presenceService = presenceService;
        this.conversationService = conversationService;
        this.conversationRepository = conversationRepository;
        this.notificationRepository = notificationRepository;
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
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        notificationService.createNotification(friendship.getRequester().getUserId(), notificationType.NEW_MESSAGE,"Pedido de amizade.", "🎲 O seu pedido de amizade a "+ authenticatedUser.getUsername() + " foi aceite!");
        User user = userRepository.findById(friendship.getRequester().getUserId()).orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
        Notificão_aceitar_amizade notificãoAceitarAmizade = new Notificão_aceitar_amizade("🎲 O seu pedido de amizade a "+ authenticatedUser.getUsername() + " foi aceite!", notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(friendship.getRequester().getUserId()).stream().map(this::toDTO).toList());
        webSocketNotificationService.pedido_de_amizade_aceite(
                user, notificãoAceitarAmizade
        );
        webSocketNotificationService.sendOnlineFriends_amizade_aceite(
                user.getUsername(), getFriends(user)
        );

        //Criar conversa e adicionar elementos a conversa!!!!!!!!
        conversationService.createPrivateConversation(
                friendship.getRequester(),
                friendship.getAddressee()
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
    // PEDIDOS BLOQUEADOS
    // =========================================================

    public List<FRIENDSHIP> getBlockRequests() {

        User authenticatedUser = authenticatedUserService.getAuthenticatedUser();

        return friendshipRepository.findByAddresseeAndStatus(
                authenticatedUser,
                FriendshipStatus.BLOCKED
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

    public List<FRIENDSHIP> getAcceptedFriendships(User authenticatedUser)  {

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
    //OBTER A LISTA DE AMIGOS A APRESENTAR NA LISTA LATERAL DO LAYOUT
    @Transactional()
    public List<FriendDTO> getFriends(User user) {

        List<FRIENDSHIP> friendships = getAcceptedFriendships(user);

        Map<UUID, UUID> conversationMap = conversationRepository
                .findConversationIdsByUser(user.getUserId())
                .stream()
                .collect(Collectors.toMap(
                        row -> (UUID) row[0],
                        row -> (UUID) row[1]
                ));

        return friendships.stream()
                .map(friendship -> {

                    User friend = friendship.getRequester().equals(user)
                            ? friendship.getAddressee()
                            : friendship.getRequester();

                    return new FriendDTO(

                            friend.getUserId(),

                            friend.getUsername(),

                            conversationMap.get(friend.getUserId()),

                            presenceService.isOnline(friend.getUserId())



                    );

                })
                .toList();
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
        Optional<User> user = userRepository.findById(friendship.getAddressee().getUserId());
        webSocketNotificationService.sendFriendRequest(
                user.get(),
                friendship.getStatus()
        );
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

    // =========================================================
    // PESQUISAR AMIGOS PARA ENVIAR O ESTADO DE ONLINE NO WEBSOCKET
    // =========================================================
    public List<String> getFriendUsernames(User user) {

        return friendshipRepository.findFriendUsernames(
                user.getUserId()
        );

    }
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