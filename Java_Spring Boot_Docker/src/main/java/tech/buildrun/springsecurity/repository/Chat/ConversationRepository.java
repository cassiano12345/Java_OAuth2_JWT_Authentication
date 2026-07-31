package tech.buildrun.springsecurity.repository.Chat;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.buildrun.springsecurity.entities.Chat.Conversation;
import tech.buildrun.springsecurity.entities.Chat.ConversationType;

import java.util.List;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    // Buscar todas as conversas criadas por um usuário
    List<Conversation> findByCreatedBy_UserId(UUID userId);

    // Buscar conversas por tipo
    List<Conversation> findByType(ConversationType type);

    // Buscar conversas criadas por um usuário e de determinado tipo
    List<Conversation> findByCreatedBy_UserIdAndType(
            UUID userId,
            ConversationType type
    );

    // Verificar se existe uma conversa
    boolean existsByConversationId(UUID conversationId);
}