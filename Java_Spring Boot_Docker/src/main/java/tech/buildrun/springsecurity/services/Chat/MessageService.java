package tech.buildrun.springsecurity.services.Chat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.buildrun.springsecurity.entities.Chat.Conversation;
import tech.buildrun.springsecurity.entities.Chat.Message;
import tech.buildrun.springsecurity.entities.Chat.MessageType;
import tech.buildrun.springsecurity.entities.User;
import tech.buildrun.springsecurity.repository.Chat.ConversationRepository;
import tech.buildrun.springsecurity.repository.Chat.MessageRepository;
import tech.buildrun.springsecurity.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    public MessageService(
            MessageRepository messageRepository,
            ConversationRepository conversationRepository,
            UserRepository userRepository
    ) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
    }


    // Enviar mensagem
    @Transactional
    public Message sendMessage(
            UUID conversationId,
            UUID senderId,
            MessageType messageType,
            String content
    ) {

        Conversation conversation = conversationRepository
                .findById(conversationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Conversa não encontrada."
                        )
                );

        User sender = userRepository
                .findById(senderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuário não encontrado."
                        )
                );

        Message message = new Message();

        message.setConversation(conversation);
        message.setSender(sender);
        message.setMessageType(messageType);
        message.setContent(content);
        message.setSentAt(LocalDateTime.now());

        return messageRepository.save(message);
    }


    // Buscar mensagem pelo ID
    public Message findById(UUID messageId) {

        return messageRepository
                .findById(messageId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Mensagem não encontrada."
                        )
                );
    }


    // Buscar mensagens de uma conversa
    public List<Message> findMessagesByConversation(
            UUID conversationId
    ) {

        return messageRepository
                .findByConversation_ConversationIdOrderBySentAtAsc(
                        conversationId
                );
    }


    // Buscar mensagens enviadas por um usuário
    public List<Message> findMessagesByUser(
            UUID userId
    ) {

        return messageRepository
                .findBySender_UserIdOrderBySentAtAsc(
                        userId
                );
    }


    // Buscar mensagens de um usuário em uma conversa
    public List<Message> findMessagesByUserAndConversation(
            UUID conversationId,
            UUID userId
    ) {

        return messageRepository
                .findByConversation_ConversationIdAndSender_UserIdOrderBySentAtAsc(
                        conversationId,
                        userId
                );
    }


    // Editar mensagem
    @Transactional
    public Message editMessage(
            UUID messageId,
            UUID userId,
            String newContent
    ) {

        Message message = messageRepository
                .findById(messageId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Mensagem não encontrada."
                        )
                );

        if (!message.getSender().getUserId().equals(userId)) {
            throw new RuntimeException(
                    "Você não pode editar esta mensagem."
            );
        }

        message.setContent(newContent);
        message.setEditedAt(LocalDateTime.now());

        return messageRepository.save(message);
    }


    // Apagar mensagem
    @Transactional
    public void deleteMessage(
            UUID messageId,
            UUID userId
    ) {

        Message message = messageRepository
                .findById(messageId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Mensagem não encontrada."
                        )
                );

        if (!message.getSender().getUserId().equals(userId)) {
            throw new RuntimeException(
                    "Você não pode apagar esta mensagem."
            );
        }

        message.setDeletedAt(LocalDateTime.now());

        messageRepository.save(message);
    }
}