package tech.buildrun.springsecurity.repository.Chat;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.buildrun.springsecurity.entities.Chat.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

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