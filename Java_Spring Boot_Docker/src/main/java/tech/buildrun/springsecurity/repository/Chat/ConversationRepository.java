package tech.buildrun.springsecurity.repository.Chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.buildrun.springsecurity.entities.Chat.Conversation;
import tech.buildrun.springsecurity.entities.Chat.ConversationType;
import tech.buildrun.springsecurity.entities.User;

import java.util.List;
import java.util.Optional;
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

    @Query("""
    SELECT c
    FROM Conversation c
    WHERE c.type = tech.buildrun.springsecurity.entities.Chat.ConversationType.PRIVATE
      AND (
            SELECT COUNT(cm)
            FROM ConversationMember cm
            WHERE cm.conversation = c
          ) = 2
      AND EXISTS (
            SELECT 1
            FROM ConversationMember cm
            WHERE cm.conversation = c
              AND cm.user = :user1
          )
      AND EXISTS (
            SELECT 1
            FROM ConversationMember cm
            WHERE cm.conversation = c
              AND cm.user = :user2
          )
""")
    Optional<Conversation> findPrivateConversation(
            @Param("user1") User user1,
            @Param("user2") User user2
    );
}