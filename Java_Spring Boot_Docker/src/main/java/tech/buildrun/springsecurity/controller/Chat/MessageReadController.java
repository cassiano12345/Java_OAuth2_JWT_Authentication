package tech.buildrun.springsecurity.controller.Chat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tech.buildrun.springsecurity.dtos.Chat.MessageReadDTO;
import tech.buildrun.springsecurity.dtos.Chat.MessageReadResponseDTO;
import tech.buildrun.springsecurity.entities.Chat.MessageRead;
import tech.buildrun.springsecurity.services.Chat.MessageReadService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/message-reads")
public class MessageReadController {

    private final MessageReadService messageReadService;

    public MessageReadController(
            MessageReadService messageReadService
    ) {
        this.messageReadService = messageReadService;
    }


    // Marcar mensagem como lida
    @PostMapping("/mark-as-read")
    public ResponseEntity<MessageReadResponseDTO> markAsRead(
            @RequestBody MessageReadDTO dto
    ) {

        MessageRead messageRead =messageReadService.markAsRead(
                        dto.messageId()
                );

        return ResponseEntity.ok(
                toResponseDTO(messageRead)
        );
    }


    // Verificar se um usuário já leu uma mensagem
    @PostMapping("/has-read")
    public ResponseEntity<Boolean> hasRead(
            @RequestBody MessageReadDTO dto
    ) {

        boolean hasRead =
                messageReadService.hasRead(
                        dto.messageId()
                );

        return ResponseEntity.ok(hasRead);
    }


    // Buscar uma leitura específica
    @PostMapping("/find")
    public ResponseEntity<MessageReadResponseDTO> findRead(
            @RequestBody MessageReadDTO dto
    ) {

        MessageRead messageRead =
                messageReadService.findRead(
                        dto.messageId()
                );

        return ResponseEntity.ok(
                toResponseDTO(messageRead)
        );
    }


    // Buscar todas as mensagens lidas por um usuário
    @PostMapping("/find-by-user")
    public ResponseEntity<List<MessageReadResponseDTO>> findByUser(
            @RequestBody MessageReadDTO dto
    ) {

        List<MessageRead> messageReads =
                messageReadService.findByUser(

                );

        List<MessageReadResponseDTO> response =
                messageReads.stream()
                        .map(this::toResponseDTO)
                        .toList();

        return ResponseEntity.ok(response);
    }


    // Buscar todas as pessoas que leram uma mensagem
    @PostMapping("/find-by-message")
    public ResponseEntity<List<MessageReadResponseDTO>> findByMessage(
            @RequestBody MessageReadDTO dto
    ) {

        List<MessageRead> messageReads =
                messageReadService.findByMessage(
                        dto.messageId()
                );

        List<MessageReadResponseDTO> response =
                messageReads.stream()
                        .map(this::toResponseDTO)
                        .toList();

        return ResponseEntity.ok(response);
    }


    // Buscar todas as leituras de uma conversa
    @PostMapping("/find-by-conversation")
    public ResponseEntity<List<MessageReadResponseDTO>> findByConversation(
            @RequestBody MessageReadDTO dto
    ) {

        List<MessageRead> messageReads =
                messageReadService.findByConversation(
                        dto.conversationId()
                );

        List<MessageReadResponseDTO> response =
                messageReads.stream()
                        .map(this::toResponseDTO)
                        .toList();

        return ResponseEntity.ok(response);
    }


    // Contar quantas pessoas leram uma mensagem
    @PostMapping("/count-readers")
    public ResponseEntity<Long> countReaders(
            @RequestBody MessageReadDTO dto
    ) {

        long count =
                messageReadService.countReaders(
                        dto.messageId()
                );

        return ResponseEntity.ok(count);
    }


    // Converter Entity para Response DTO
    private MessageReadResponseDTO toResponseDTO(
            MessageRead messageRead
    ) {

        return new MessageReadResponseDTO(
                messageRead.getMessageReadId(),
                messageRead.getMessage().getMessageId(),
                messageRead.getUser().getUserId(),
                messageRead.getUser().getUsername(),
                messageRead.getReadAt()
        );
    }
}