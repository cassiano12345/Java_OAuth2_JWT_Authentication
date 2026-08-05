package tech.buildrun.springsecurity.services.Chat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.buildrun.springsecurity.entities.Chat.Conversation;
import tech.buildrun.springsecurity.entities.Chat.ConversationMember;
import tech.buildrun.springsecurity.entities.Chat.ConversationMemberRole;
import tech.buildrun.springsecurity.entities.Chat.ConversationType;
import tech.buildrun.springsecurity.entities.User;
import tech.buildrun.springsecurity.repository.Chat.ConversationMemberRepository;
import tech.buildrun.springsecurity.repository.Chat.ConversationRepository;
import tech.buildrun.springsecurity.repository.UserRepository;
import tech.buildrun.springsecurity.services.AuthenticatedUserService;

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
    public ConversationService(
            ConversationRepository conversationRepository,
            ConversationMemberRepository conversationMemberRepository,
            UserRepository userRepository, AuthenticatedUserService authenticatedUserService
    ) {
        this.conversationRepository = conversationRepository;
        this.conversationMemberRepository = conversationMemberRepository;
        this.userRepository = userRepository;
        this.authenticatedUserService = authenticatedUserService;
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
                        new RuntimeException("Usuário não encontrado.")
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
}