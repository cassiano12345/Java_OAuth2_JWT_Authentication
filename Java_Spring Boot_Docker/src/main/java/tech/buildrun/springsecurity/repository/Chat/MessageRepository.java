package tech.buildrun.springsecurity.repository.Chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tech.buildrun.springsecurity.entities.Chat.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    @Query("""
        SELECT COUNT(m)
        FROM Message m
        WHERE EXISTS (
            SELECT cm
            FROM ConversationMember cm
            WHERE cm.conversation = m.conversation
              AND cm.user.userId = :userId
        )
        AND m.sender.userId <> :userId
        AND NOT EXISTS (
            SELECT mr
            FROM MessageRead mr
            WHERE mr.message = m
              AND mr.user.userId = :userId
        )
    """)
    long countUnreadMessages(UUID userId);

    @Query("""
SELECT COUNT(m)
FROM Message m
WHERE m.conversation.conversationId = :conversationId
AND m.sender.userId <> :userId
AND NOT EXISTS (
    SELECT mr
    FROM MessageRead mr
    WHERE mr.message = m
      AND mr.user.userId = :userId
)
""")
    long countUnreadMessagesByConversation(
            UUID conversationId,
            UUID userId
    );

    // Buscar todas as mensagens de uma conversa
    List<Message> findByConversation_ConversationIdOrderBySentAtAsc(
            UUID conversationId
    );

    // Buscar todas as mensagens enviadas por um usuário
    List<Message> findBySender_UserIdOrderBySentAtAsc(
            UUID userId
    );

    // Buscar mensagens de um usuário dentro de uma conversa
    List<Message> findByConversation_ConversationIdAndSender_UserIdOrderBySentAtAsc(
            UUID conversationId,
            UUID userId
    );

    // Verificar se existem mensagens em uma conversa
    boolean existsByConversation_ConversationId(
            UUID conversationId
    );
}