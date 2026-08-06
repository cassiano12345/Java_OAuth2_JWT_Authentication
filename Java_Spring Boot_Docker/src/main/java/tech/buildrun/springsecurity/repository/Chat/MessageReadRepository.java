package tech.buildrun.springsecurity.repository.Chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tech.buildrun.springsecurity.entities.Chat.MessageRead;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageReadRepository extends JpaRepository<MessageRead, UUID> {

    // Verificar se um usuário já leu uma determinada mensagem
    boolean existsByMessage_MessageIdAndUser_UserId(
            UUID messageId,
            UUID userId
    );
    @Query("""
    SELECT mr
    FROM MessageRead mr
    WHERE mr.user.userId = :userId
      AND mr.message.conversation.conversationId = :conversationId
""")
    List<MessageRead> findByConversationAndUser(
            UUID conversationId,
            UUID userId
    );

    // Buscar o registro de leitura de uma mensagem por um usuário
    Optional<MessageRead> findByMessage_MessageIdAndUser_UserId(
            UUID messageId,
            UUID userId
    );

    // Buscar todas as mensagens lidas por um usuário
    List<MessageRead> findByUser_UserIdOrderByReadAtDesc(
            UUID userId
    );

    // Buscar todos os usuários que leram uma determinada mensagem
    List<MessageRead> findByMessage_MessageIdOrderByReadAtAsc(
            UUID messageId
    );

    // Buscar todas as leituras de mensagens de uma conversa
    List<MessageRead> findByMessage_Conversation_ConversationId(
            UUID conversationId
    );

    // Contar quantos usuários leram uma mensagem
    long countByMessage_MessageId(
            UUID messageId
    );
}