package tech.buildrun.springsecurity.services.Chat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.buildrun.springsecurity.dtos.Chat.*;
import tech.buildrun.springsecurity.entities.Chat.*;
import tech.buildrun.springsecurity.entities.User;
import tech.buildrun.springsecurity.repository.Chat.ConversationMemberRepository;
import tech.buildrun.springsecurity.repository.Chat.ConversationRepository;
import tech.buildrun.springsecurity.repository.Chat.MessageRepository;
import tech.buildrun.springsecurity.repository.Chat.NotificationRepository;
import tech.buildrun.springsecurity.repository.UserRepository;
import tech.buildrun.springsecurity.services.AuthenticatedUserService;
import tech.buildrun.springsecurity.services.PresenceService;
import tech.buildrun.springsecurity.websocket.WebSocketNotificationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository conversationMemberRepository;
    private final UserRepository userRepository;
    private final AuthenticatedUserService authenticatedUserService;
    ConversationMemberRole ConversationRole;
    private final PresenceService presenceService;;
    private final MessageRepository messageRepository;
    private final NotificationService notificationService;
    NotificationType notificationType;
    private final NotificationRepository notificationRepository;
    private final WebSocketNotificationService webSocketNotificationService;
    public ConversationService(
            ConversationRepository conversationRepository,
            ConversationMemberRepository conversationMemberRepository,
            UserRepository userRepository, AuthenticatedUserService authenticatedUserService, PresenceService presenceService, MessageRepository messageRepository, NotificationService notificationService, NotificationRepository notificationRepository, WebSocketNotificationService webSocketNotificationService
    ) {
        this.conversationRepository = conversationRepository;
        this.conversationMemberRepository = conversationMemberRepository;
        this.userRepository = userRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.presenceService = presenceService;
        this.messageRepository = messageRepository;
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
        this.webSocketNotificationService = webSocketNotificationService;
    }

    // Criar uma nova conversa
    @Transactional
    public Conversation createConversation(
            ConversationType type,
            String name

    ) {
        User user = authenticatedUserService.getAuthenticatedUser();
        User creator = userRepository.findById(user.getUserId())
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado    .")
                );

        // Criar a conversa
        Conversation conversation = new Conversation();

        conversation.setType(type);
        conversation.setName(name);
        conversation.setCreatedBy(creator);
        conversation.setCreatedAt(LocalDateTime.now());

        conversation = conversationRepository.save(conversation);

        // Criador entra automaticamente como ADMIN
        ConversationMember member = new ConversationMember();

        member.setConversation(conversation);
        member.setUser(creator);
        member.setRole(ConversationMemberRole.ADMIN);
        member.setJoinedAt(LocalDateTime.now());

        conversationMemberRepository.save(member);

        return conversation;
    }
    @Transactional
    public Conversation createPrivateConversation(User user1, User user2) {

        Optional<Conversation> existingConversation =
                conversationRepository.findPrivateConversation(
                        user1,
                        user2
                );

        if (existingConversation.isPresent()) {
            return existingConversation.get();
        }

        Conversation conversation = new Conversation();

        conversation.setType(ConversationType.PRIVATE);
        conversation.setName(
                user1.getUsername() + " + " + user2.getUsername()
        );
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setCreatedBy(user1);
        conversation = conversationRepository.save(conversation);


        ConversationMember member1 = new ConversationMember();
        member1.setConversation(conversation);
        member1.setUser(user1);
        member1.setRole(ConversationRole.MEMBER);
        member1.setJoinedAt(LocalDateTime.now());

        ConversationMember member2 = new ConversationMember();
        member2.setConversation(conversation);
        member2.setUser(user2);
        member2.setRole(ConversationRole.MEMBER);
        member2.setJoinedAt(LocalDateTime.now());

        conversationMemberRepository.save(member1);
        conversationMemberRepository.save(member2);

        return conversation;
    }


    @Transactional
    public Conversation createGroupConversation(
            String name,
            List<GroupMemberDTO> members
    ) {

        User authenticatedUser =
                authenticatedUserService.getAuthenticatedUser();

        Conversation conversation = new Conversation();

        conversation.setType(ConversationType.GROUP);
        conversation.setName(name);
        conversation.setCreatedBy(authenticatedUser);
        conversation.setCreatedAt(LocalDateTime.now());


        // Criador do grupo
        ConversationMember creatorMember =
                new ConversationMember();

        creatorMember.setUser(authenticatedUser);
        creatorMember.setRole(ConversationMemberRole.ADMIN);
        creatorMember.setJoinedAt(LocalDateTime.now());

        conversation.addMember(creatorMember);


        // Adicionar os membros selecionados
        for (GroupMemberDTO memberDTO : members) {

            UUID userId = memberDTO.id();

            // Evitar adicionar o próprio utilizador novamente
            if (userId.equals(authenticatedUser.getUserId())) {
                continue;
            }

            User user = userRepository
                    .findById(userId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Usuário não encontrado: " + userId
                            )
                    );

            ConversationMember conversationMember =
                    new ConversationMember();

            conversationMember.setUser(user);
            conversationMember.setRole(
                    memberDTO.role()
            );
            conversationMember.setJoinedAt(
                    LocalDateTime.now()
            );

            conversation.addMember(conversationMember);
            notificationService.createNotification(userId, notificationType.GROUP_ADDED,"💬 Adicionado a grupo de mensagens.", "💬 Você foi adicionado ao grupo de mensagens "+ name + " por: " + authenticatedUser.getUsername());
            Notificão_aceitar_amizade notificcoAceitarAmizade = new Notificão_aceitar_amizade("💬 Adicionado a grupo de mensagens.", notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId).stream().map(this::toDTO).toList());

            webSocketNotificationService.sendNotifications_new_group_add(
                    user, notificcoAceitarAmizade
            );

        }

        return conversationRepository.save(conversation);
    }

    // Buscar uma conversa pelo ID
    public Conversation findById(UUID conversationId) {

        return conversationRepository.findById(conversationId)
                .orElseThrow(() ->
                        new RuntimeException("Conversa não encontrada.")
                );
    }

    // Buscar todas as conversas criadas por um usuário
    public List<Conversation> findByCreator() {
        User user = authenticatedUserService.getAuthenticatedUser();
        return conversationRepository
                .findByCreatedBy_UserId(user.getUserId());
    }

    // Buscar conversas por tipo
    public List<Conversation> findByType(
            ConversationType type
    ) {

        return conversationRepository.findByType(type);
    }


    // Deletar uma conversa
    @Transactional
    public void delete(UUID conversationId) {

        Conversation conversation = findById(conversationId);

        conversationRepository.delete(conversation);
    }

    @Transactional(readOnly = true)
    public List<ConversationListItemDTO> getMyConversations() {

        User user = authenticatedUserService.getAuthenticatedUser();

        List<ConversationMember> memberships =
                conversationMemberRepository.findByUserAndConversation_Type(
                        user,
                        ConversationType.PRIVATE
                );

        return memberships.stream()
                .map(member -> {

                    Conversation conversation = member.getConversation();

                    User friend = conversation.getMembers()
                            .stream()
                            .map(ConversationMember::getUser)
                            .filter(u ->
                                    !u.getUserId().equals(user.getUserId())
                            )
                            .findFirst()
                            .orElseThrow();

                    Message lastMessage = conversation.getLastMessage();

                    long unreadCount =
                            messageRepository.countUnreadMessagesByConversation(
                                    conversation.getConversationId(),
                                    user.getUserId()
                            );

                    return new ConversationListItemDTO(
                            conversation.getConversationId(),
                            friend.getUserId(),
                            friend.getUsername(),
                            presenceService.isOnline(friend.getUserId()),
                            lastMessage != null
                                    ? lastMessage.getContent()
                                    : null,
                            conversation.getLastMessageAt(),
                            unreadCount
                    );
                })
                .toList();
    }

    /*
    *
    * ADICIONAR NOVOS MEMBROS A UM GRUPO
    * */
    @Transactional
    public ConversationMember addMemberToGroup(
            UUID conversationId,
            UUID userId,
            ConversationMemberRole role
    ) {
        User authenticatedUser =
                authenticatedUserService.getAuthenticatedUser();

        // 1. Buscar conversa
        Conversation conversation = conversationRepository
                .findById(conversationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Conversa não encontrada."
                        )
                );

        // 2. Garantir que é um GROUP
        if (conversation.getType() != ConversationType.GROUP) {
            throw new RuntimeException(
                    "Só é possível adicionar membros a grupos."
            );
        }

        // 3. Buscar o membro que está tentando adicionar alguém
        ConversationMember authenticatedMember =
                conversationMemberRepository
                        .findByConversation_ConversationIdAndUser_UserId(
                                conversationId,
                                authenticatedUser.getUserId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Você não pertence a este grupo."
                                )
                        );

        // 4. Apenas ADMIN pode adicionar membros
        if (authenticatedMember.getRole()
                != ConversationMemberRole.ADMIN) {

            throw new RuntimeException(
                    "Apenas administradores podem adicionar membros."
            );
        }

        // 5. Verificar se o utilizador já pertence ao grupo
        boolean alreadyMember =
                conversationMemberRepository
                        .existsByConversation_ConversationIdAndUser_UserId(
                                conversationId,
                                userId
                        );

        if (alreadyMember) {
            throw new RuntimeException(
                    "Este utilizador já pertence ao grupo."
            );
        }

        // 6. Buscar utilizador que será adicionado
        User userToAdd = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuário não encontrado."
                        )
                );

        // 7. Criar membro
        ConversationMember member = new ConversationMember();

        member.setUser(userToAdd);

        // IMPORTANTE:
        // não confies cegamente no role enviado pelo frontend
        member.setRole(
                role != null
                        ? role
                        : ConversationMemberRole.MEMBER
        );

        member.setJoinedAt(LocalDateTime.now());

        // 8. Adicionar à conversa
        conversation.addMember(member);

        // 9. Guardar
        return conversationMemberRepository.save(member);
    }

    /*
    *
    * ADMIN ELIMINA MEMBRO DE UMA GRUPO
    *
    * */
    @Transactional
    public void removeMemberFromGroup(
            UUID conversationId,
            UUID userIdToRemove
    ) {
        User authenticatedUser =
                authenticatedUserService.getAuthenticatedUser();

        // 1. Procurar a conversa
        Conversation conversation = conversationRepository
                .findById(conversationId)
                .orElseThrow(() ->
                        new RuntimeException("Conversa não encontrada.")
                );

        // 2. Garantir que é um grupo
        if (conversation.getType() != ConversationType.GROUP) {
            throw new RuntimeException(
                    "Esta conversa não é um grupo."
            );
        }

        // 3. Procurar o membro que está a executar a operação
        ConversationMember authenticatedMember =
                conversationMemberRepository
                        .findByConversation_ConversationIdAndUser_UserId(
                                conversationId,
                                authenticatedUser.getUserId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Você não pertence a este grupo."
                                )
                        );

        // 4. Apenas ADMIN pode remover membros
        if (authenticatedMember.getRole()
                != ConversationMemberRole.ADMIN) {

            throw new RuntimeException(
                    "Apenas administradores podem remover membros."
            );
        }

        // 5. Procurar o membro que será removido
        ConversationMember memberToRemove =
                conversationMemberRepository
                        .findByConversation_ConversationIdAndUser_UserId(
                                conversationId,
                                userIdToRemove
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "O usuário não pertence a este grupo."
                                )
                        );

        // 6. Não permitir remover o último ADMIN
        if (memberToRemove.getRole()
                == ConversationMemberRole.ADMIN) {

            long adminCount =
                    conversationMemberRepository
                            .countByConversation_ConversationIdAndRole(
                                    conversationId,
                                    ConversationMemberRole.ADMIN
                            );

            if (adminCount <= 1) {
                throw new RuntimeException(
                        "Não é possível remover o último administrador do grupo."
                );
            }
        }

        // 7. Remover da relação
        conversation.removeMember(memberToRemove);

        conversationMemberRepository.delete(memberToRemove);
    }

    /*
    * ELEMENTO SAI DO GRUPO POR LIVRE VONTADE!
    * */
    @Transactional
    public void leaveGroup(UUID conversationId) {

        User user = authenticatedUserService.getAuthenticatedUser();

        ConversationMember member = conversationMemberRepository
                .findByConversation_ConversationIdAndUser_UserId(
                        conversationId,
                        user.getUserId()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Você não pertence a este grupo."
                        )
                );

        Conversation conversation = member.getConversation();

        if (conversation.getType() != ConversationType.GROUP) {
            throw new RuntimeException(
                    "Esta conversa não é um grupo."
            );
        }

        conversation.removeMember(member);

        conversationMemberRepository.delete(member);
    }

    /*
    * OBTER NOME E ROLE DE TODOS ELEMENTOS DE UM DETERMINADO GRUPO!
    * */
    @Transactional(readOnly = true)
    public List<GroupMemberDTO> getGroupMembers(UUID conversationId) {

        Conversation conversation = conversationRepository
                .findById(conversationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Grupo não encontrado."
                        )
                );

        if (conversation.getType() != ConversationType.GROUP) {
            throw new RuntimeException(
                    "Esta conversa não é um grupo."
            );
        }

        return conversationMemberRepository
                .findByConversation_ConversationId(conversationId)
                .stream()
                .map(member -> new GroupMemberDTO(
                        member.getUser().getUserId(),
                        member.getUser().getUsername(),
                        member.getRole()
                ))
                .toList();
    }


    @Transactional(readOnly = true)
    public List<GroupConversationDTO> getMyGroups(User user) {

        List<ConversationMember> memberships =
                conversationMemberRepository
                        .findByUser_UserIdAndConversation_Type(
                                user.getUserId(),
                                ConversationType.GROUP
                        );

        return memberships.stream()
                .map(member -> {

                    Conversation conversation = member.getConversation();

                    Message lastMessage = conversation.getLastMessage();

                    long unreadCount = messageRepository.countUnreadMessagesByConversation(
                                    conversation.getConversationId(),
                                    user.getUserId()
                            );

                    return new GroupConversationDTO(
                            conversation.getConversationId(),
                            conversation.getName(),
                            lastMessage != null
                                    ? lastMessage.getContent()
                                    : null,
                            conversation.getLastMessageAt(),
                            unreadCount
                    );
                })
                .toList();
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