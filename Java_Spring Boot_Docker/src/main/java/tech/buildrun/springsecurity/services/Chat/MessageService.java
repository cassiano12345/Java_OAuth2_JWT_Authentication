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
import tech.buildrun.springsecurity.services.AuthenticatedUserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public MessageService(
            MessageRepository messageRepository,
            ConversationRepository conversationRepository,
            UserRepository userRepository, AuthenticatedUserService authenticatedUserService
    ) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.authenticatedUserService = authenticatedUserService;
    }


    // Enviar mensagem
    @Transactional
    public Message sendMessage(
            UUID conversationId,
            MessageType messageType,
            String content
    ) {
        User user = authenticatedUserService.getAuthenticatedUser();

        Conversation conversation = conversationRepository
                .findById(conversationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Conversa não encontrada."
                        )
                );

        User sender = userRepository
                .findById(user.getUserId())
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
    ) {
        User user = authenticatedUserService.getAuthenticatedUser();

        return messageRepository
                .findBySender_UserIdOrderBySentAtAsc(
                        user.getUserId()
                );
    }


    // Buscar mensagens de um usuário em uma conversa
    public List<Message> findMessagesByUserAndConversation(
            UUID conversationId
    ) {
        User user = authenticatedUserService.getAuthenticatedUser();

        return messageRepository
                .findByConversation_ConversationIdAndSender_UserIdOrderBySentAtAsc(
                        conversationId,
                        user.getUserId()
                );
    }


    // Editar mensagem
    @Transactional
    public Message editMessage(
            UUID messageId,
            String newContent
    ) {
        User user = authenticatedUserService.getAuthenticatedUser();

        Message message = messageRepository
                .findById(messageId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Mensagem não encontrada."
                        )
                );

        if (!message.getSender().getUserId().equals(user.getUserId())) {
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
            UUID messageId
    ) {
        User user = authenticatedUserService.getAuthenticatedUser();

        Message message = messageRepository
                .findById(messageId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Mensagem não encontrada."
                        )
                );

        if (!message.getSender().getUserId().equals(user.getUserId())) {
            throw new RuntimeException(
                    "Você não pode apagar esta mensagem."
            );
        }

        message.setDeletedAt(LocalDateTime.now());

        messageRepository.save(message);
    }
}