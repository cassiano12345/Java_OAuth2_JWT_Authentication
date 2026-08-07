package tech.buildrun.springsecurity.controller.Chat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.buildrun.springsecurity.dtos.Chat.MessageDTO;
import tech.buildrun.springsecurity.dtos.Chat.MessageResponseDTO;
import tech.buildrun.springsecurity.dtos.Chat.Message_listDTO;
import tech.buildrun.springsecurity.entities.Chat.Message;
import tech.buildrun.springsecurity.entities.User;
import tech.buildrun.springsecurity.services.AuthenticatedUserService;
import tech.buildrun.springsecurity.services.Chat.MessageService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;
    private final AuthenticatedUserService authenticatedUserService;
    public MessageController(MessageService messageService, AuthenticatedUserService authenticatedUserService) {
        this.messageService = messageService;
        this.authenticatedUserService = authenticatedUserService;
    }
    // OBTER MENSAGENS NÃO LIDAS PARA O MENU
    @GetMapping("/unread-count")
    public long getUnreadMessagesCount() throws InterruptedException {
        return messageService.getUnreadMessagesCount();
    }


    // Enviar mensagem
    @PostMapping("/send")
    public ResponseEntity<MessageResponseDTO> sendMessage(
            @RequestBody MessageDTO dto
    ) {

        Message message = messageService.sendMessage(dto.conversationId(),dto.messageType(),dto.content());

        return ResponseEntity.ok(
                toResponseDTO(message)
        );
    }


    // Buscar mensagens de uma conversa
    @PostMapping("/find-by-conversation")
    public ResponseEntity<List<MessageResponseDTO>> findMessagesByConversation(
            @RequestBody MessageDTO dto
    ) {

        List<Message> messages =
                messageService.findMessagesByConversation(
                        dto.conversationId()
                );

        List<MessageResponseDTO> response = messages.stream()
                .map(this::toResponseDTO)
                .toList();

        return ResponseEntity.ok(response);
    }


    // Buscar mensagens enviadas por um usuário
    @PostMapping("/find-by-user")
    public ResponseEntity<List<MessageResponseDTO>> findMessagesByUser(
            @RequestBody MessageDTO dto
    ) {

        List<Message> messages =
                messageService.findMessagesByUser(
                );

        List<MessageResponseDTO> response = messages.stream()
                .map(this::toResponseDTO)
                .toList();

        return ResponseEntity.ok(response);
    }


    // Buscar mensagens de um usuário dentro de uma conversa
    @PostMapping("/find-by-user-and-conversation")
    public ResponseEntity<List<MessageResponseDTO>> findMessagesByUserAndConversation(
            @RequestBody MessageDTO dto
    ) {

        List<Message> messages =
                messageService.findMessagesByUserAndConversation(
                        dto.conversationId()
                );

        List<MessageResponseDTO> response = messages.stream()
                .map(this::toResponseDTO)
                .toList();

        return ResponseEntity.ok(response);
    }


    // Buscar uma mensagem pelo ID
    @PostMapping("/find")
    public ResponseEntity<MessageResponseDTO> findById(
            @RequestBody MessageDTO dto
    ) {

        Message message =
                messageService.findById(
                        dto.messageId()
                );

        return ResponseEntity.ok(
                toResponseDTO(message)
        );
    }


    // Editar mensagem
    @PutMapping("/edit")
    public ResponseEntity<MessageResponseDTO> editMessage(
            @RequestBody MessageDTO dto
    ) {

        Message message =
                messageService.editMessage(
                        dto.messageId(),
                        dto.content()
                );

        return ResponseEntity.ok(
                toResponseDTO(message)
        );
    }


    // Apagar mensagem
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteMessage(
            @RequestBody MessageDTO dto
    ) {

        messageService.deleteMessage(
                dto.messageId()
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<Message_listDTO>> getConversationMessages(
            @PathVariable UUID conversationId
    ) {
        User loggedUser = authenticatedUserService.getAuthenticatedUser();
        return ResponseEntity.ok(messageService.getConversationMessages(conversationId, loggedUser)
        );
    }


    // Converter Entity para Response DTO
    private MessageResponseDTO toResponseDTO(
            Message message
    ) {

        return new MessageResponseDTO(
                message.getMessageId(),
                message.getConversation().getConversationId(),
                message.getSender().getUserId(),
                message.getSender().getUsername(),
                message.getMessageType(),
                message.getContent(),
                message.getSentAt(),
                message.getEditedAt(),
                message.getDeletedAt()
        );
    }
}