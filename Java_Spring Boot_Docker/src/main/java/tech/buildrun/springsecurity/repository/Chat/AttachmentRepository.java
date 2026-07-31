package tech.buildrun.springsecurity.repository.Chat;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.buildrun.springsecurity.entities.Chat.Attachment;

import java.util.List;
import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {

    // Buscar todos os anexos de uma mensagem
    List<Attachment> findByMessage_MessageId(
            UUID messageId
    );

    // Buscar anexos de uma mensagem ordenados pelo nome
    List<Attachment> findByMessage_MessageIdOrderByFileNameAsc(
            UUID messageId
    );

    // Buscar todos os anexos enviados dentro de uma conversa
    List<Attachment> findByMessage_Conversation_ConversationId(
            UUID conversationId
    );

    // Contar quantos anexos uma mensagem possui
    long countByMessage_MessageId(
            UUID messageId
    );

    // Verificar se uma mensagem possui anexos
    boolean existsByMessage_MessageId(
            UUID messageId
    );
}