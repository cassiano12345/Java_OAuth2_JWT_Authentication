package tech.buildrun.springsecurity.services.Chat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.buildrun.springsecurity.entities.Chat.Message;
import tech.buildrun.springsecurity.entities.Chat.MessageRead;
import tech.buildrun.springsecurity.entities.User;
import tech.buildrun.springsecurity.repository.Chat.MessageReadRepository;
import tech.buildrun.springsecurity.repository.Chat.MessageRepository;
import tech.buildrun.springsecurity.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MessageReadService {

    private final MessageReadRepository messageReadRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageReadService(
            MessageReadRepository messageReadRepository,
            MessageRepository messageRepository,
            UserRepository userRepository
    ) {
        this.messageReadRepository = messageReadRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }


    // Marcar uma mensagem como lida
    @Transactional
    public MessageRead markAsRead(
            UUID messageId,
            UUID userId
    ) {

        // Verificar se a mensagem existe
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Mensagem não encontrada."
                        )
                );

        // Verificar se o usuário existe
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuário não encontrado."
                        )
                );

        // Verificar se já existe uma leitura
        return messageReadRepository
                .findByMessage_MessageIdAndUser_UserId(
                        messageId,
                        userId
                )
                .orElseGet(() -> {

                    MessageRead messageRead = new MessageRead();

                    messageRead.setMessage(message);
                    messageRead.setUser(user);
                    messageRead.setReadAt(LocalDateTime.now());

                    return messageReadRepository.save(messageRead);
                });
    }


    // Verificar se o usuário já leu uma mensagem
    public boolean hasRead(
            UUID messageId,
            UUID userId
    ) {

        return messageReadRepository
                .existsByMessage_MessageIdAndUser_UserId(
                        messageId,
                        userId
                );
    }


    // Buscar uma leitura específica
    public MessageRead findRead(
            UUID messageId,
            UUID userId
    ) {

        return messageReadRepository
                .findByMessage_MessageIdAndUser_UserId(
                        messageId,
                        userId
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Registro de leitura não encontrado."
                        )
                );
    }


    // Buscar todas as mensagens lidas por um usuário
    public List<MessageRead> findByUser(
            UUID userId
    ) {

        return messageReadRepository
                .findByUser_UserIdOrderByReadAtDesc(
                        userId
                );
    }


    // Buscar todas as pessoas que leram uma mensagem
    public List<MessageRead> findByMessage(
            UUID messageId
    ) {

        return messageReadRepository
                .findByMessage_MessageIdOrderByReadAtAsc(
                        messageId
                );
    }


    // Buscar todas as leituras de uma conversa
    public List<MessageRead> findByConversation(
            UUID conversationId
    ) {

        return messageReadRepository
                .findByMessage_Conversation_ConversationId(
                        conversationId
                );
    }


    // Contar quantas pessoas leram uma mensagem
    public long countReaders(
            UUID messageId
    ) {

        return messageReadRepository
                .countByMessage_MessageId(
                        messageId
                );
    }
}