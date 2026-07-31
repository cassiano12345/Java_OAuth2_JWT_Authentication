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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository conversationMemberRepository;
    private final UserRepository userRepository;

    public ConversationService(
            ConversationRepository conversationRepository,
            ConversationMemberRepository conversationMemberRepository,
            UserRepository userRepository
    ) {
        this.conversationRepository = conversationRepository;
        this.conversationMemberRepository = conversationMemberRepository;
        this.userRepository = userRepository;
    }

    // Criar uma nova conversa
    @Transactional
    public Conversation createConversation(
            ConversationType type,
            String name,
            UUID creatorId
    ) {

        User creator = userRepository.findById(creatorId)
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

    // Buscar uma conversa pelo ID
    public Conversation findById(UUID conversationId) {

        return conversationRepository.findById(conversationId)
                .orElseThrow(() ->
                        new RuntimeException("Conversa não encontrada.")
                );
    }

    // Buscar todas as conversas criadas por um usuário
    public List<Conversation> findByCreator(UUID userId) {

        return conversationRepository
                .findByCreatedBy_UserId(userId);
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