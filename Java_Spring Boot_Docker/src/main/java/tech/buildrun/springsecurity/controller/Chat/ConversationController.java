package tech.buildrun.springsecurity.controller.Chat;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.buildrun.springsecurity.dtos.Chat.ConversationDTO;
import tech.buildrun.springsecurity.dtos.Chat.ConversationListItemDTO;
import tech.buildrun.springsecurity.dtos.Chat.CreateGroupConversationDTO;
import tech.buildrun.springsecurity.entities.Chat.Conversation;
import tech.buildrun.springsecurity.services.Chat.ConversationService;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(
            ConversationService conversationService
    ) {
        this.conversationService = conversationService;
    }

    @PostMapping("/create")
    public ResponseEntity<Conversation> createConversation(
            @RequestBody ConversationDTO dto
    ) {

        Conversation conversation =
                conversationService.createConversation(
                        dto.type(),
                        dto.name()
                );

        return ResponseEntity.ok(conversation);
    }

    @PostMapping("/group")
    public ResponseEntity<Conversation> createGroupConversation(
            @RequestBody CreateGroupConversationDTO dto
    ) {

        Conversation conversation =
                conversationService.createGroupConversation(
                        dto.name(),
                        dto.members()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(conversation);
    }

    @PostMapping("/find")
    public ResponseEntity<Conversation> findById(
            @RequestBody ConversationDTO dto
    ) {

        Conversation conversation =
                conversationService.findById(
                        dto.conversationId()
                );

        return ResponseEntity.ok(conversation);
    }

    @PostMapping("/find-by-creator")
    public ResponseEntity<List<Conversation>> findByCreator(
            @RequestBody ConversationDTO dto
    ) {

        List<Conversation> conversations =
                conversationService.findByCreator(
                );

        return ResponseEntity.ok(conversations);
    }

    @PostMapping("/find-by-type")
    public ResponseEntity<List<Conversation>> findByType(
            @RequestBody ConversationDTO dto
    ) {

        List<Conversation> conversations =
                conversationService.findByType(
                        dto.type()
                );

        return ResponseEntity.ok(conversations);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(
            @RequestBody ConversationDTO dto
    ) {

        conversationService.delete(
                dto.conversationId()
        );

        return ResponseEntity.noContent().build();
    }
    @GetMapping("listar_conversas")
    public ResponseEntity<List<ConversationListItemDTO>> getMyConversations() {
        return ResponseEntity.ok(conversationService.getMyConversations());
    }
}