package tech.buildrun.springsecurity.repository.Chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.buildrun.springsecurity.entities.Chat.ConversationMember;
import tech.buildrun.springsecurity.entities.Chat.Message;

import java.util.List;
import java.util.Optional;
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

    Optional<ConversationMember>
    findByConversation_ConversationIdAndUser_UserId(
            UUID conversationId,
            UUID userId
    );

    // Numero de mensagens não lidas privadas
    @Query("""
    SELECT COUNT(m)
    FROM Message m
    WHERE m.conversation.type = tech.buildrun.springsecurity.entities.Chat.ConversationType.PRIVATE
      AND EXISTS (
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
    long countUnreadPrivateMessages(UUID userId);

    // Numero de mensagens não lidas grupos
    @Query("""
    SELECT COUNT(m)
    FROM Message m
    WHERE m.conversation.type = tech.buildrun.springsecurity.entities.Chat.ConversationType.GROUP
      AND EXISTS (
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
    long countUnreadGroupMessages(UUID userId);

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

    @Query("""
    SELECT COUNT(m)
    FROM Message m
    WHERE m.sender.userId <> :userId
      AND NOT EXISTS (
            SELECT mr
            FROM MessageRead mr
            WHERE mr.message = m
              AND mr.user.userId = :userId
      )
      AND m.deletedAt IS NULL
""")
    long countUnreadMessages_(@Param("userId") UUID userId);

    // Buscar todas as mensagens de uma conversa
    List<Message> findByConversation_ConversationIdOrderBySentAtAsc(UUID conversationId);


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
    @Query("""
    SELECT m
    FROM Message m
    LEFT JOIN FETCH m.sender
    LEFT JOIN FETCH m.messageReads mr
    WHERE m.conversation.conversationId = :conversationId
    ORDER BY m.sentAt ASC
""")
    List<Message> findMessagesWithReads(UUID conversationId);

}