package tech.buildrun.springsecurity.services.Chat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.buildrun.springsecurity.dtos.Chat.MessageDTO;
import tech.buildrun.springsecurity.entities.Chat.Conversation;
import tech.buildrun.springsecurity.entities.Chat.Message;
import tech.buildrun.springsecurity.entities.Chat.MessageType;
import tech.buildrun.springsecurity.entities.User;
import tech.buildrun.springsecurity.repository.Chat.ConversationMemberRepository;
import tech.buildrun.springsecurity.repository.Chat.ConversationRepository;
import tech.buildrun.springsecurity.repository.Chat.MessageRepository;
import tech.buildrun.springsecurity.repository.UserRepository;
import tech.buildrun.springsecurity.services.AuthenticatedUserService;
import tech.buildrun.springsecurity.websocket.WebSocketNotificationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final ConversationMemberRepository conversationMemberRepository;
    private final WebSocketNotificationService webSocketNotificationService;
    public MessageService(
            MessageRepository messageRepository,
            ConversationRepository conversationRepository,
            UserRepository userRepository, AuthenticatedUserService authenticatedUserService, ConversationMemberRepository conversationMemberRepository, WebSocketNotificationService webSocketNotificationService
    ) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.conversationMemberRepository = conversationMemberRepository;
        this.webSocketNotificationService = webSocketNotificationService;
    }

/*
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

*/
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
                        new RuntimeException("Conversa não encontrada.")
                );

        // Verifica se o usuário pertence à conversa
        boolean isMember = conversationMemberRepository
                .existsByConversation_ConversationIdAndUser_UserId(
                        conversationId,
                        user.getUserId()
                );

        if (!isMember) {
            throw new RuntimeException(
                    "Você não faz parte desta conversa."
            );
        }

        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(user);
        message.setMessageType(messageType);
        message.setContent(content);
        message.setSentAt(LocalDateTime.now());

        Message savedMessage = messageRepository.save(message);

        // Atualiza a última mensagem da conversa
        conversation.setLastMessage(savedMessage);
        conversation.setLastMessageAt(savedMessage.getSentAt());

        List<User> recipients = conversationMemberRepository
                .findUsersByConversationExcludingSender(
                        conversationId,
                        user.getUserId()
                );
        System.out.println("Passou daqui");
        for (User recipient : recipients) {
            // enviar websocket
            webSocketNotificationService.sendPrivateMessage(
                    recipient// ou MessageDTO
            );

        }
        conversationRepository.save(conversation);

        return savedMessage;
    }
    public List<User> getRecipients(
            UUID conversationId,
            UUID senderId
    ) {
        return conversationMemberRepository
                .findUsersByConversationExcludingSender(
                        conversationId,
                        senderId
                );
    }
}