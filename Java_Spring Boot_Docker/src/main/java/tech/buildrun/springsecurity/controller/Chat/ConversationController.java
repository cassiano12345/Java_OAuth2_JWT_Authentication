package tech.buildrun.springsecurity.controller.Chat;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.buildrun.springsecurity.dtos.Chat.*;
import tech.buildrun.springsecurity.entities.Chat.Conversation;
import tech.buildrun.springsecurity.entities.Chat.ConversationMember;
import tech.buildrun.springsecurity.entities.User;
import tech.buildrun.springsecurity.services.AuthenticatedUserService;
import tech.buildrun.springsecurity.services.Chat.ConversationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;
    private final AuthenticatedUserService authenticatedUserService;
    public ConversationController(
            ConversationService conversationService, AuthenticatedUserService authenticatedUserService
    ) {
        this.conversationService = conversationService;
        this.authenticatedUserService = authenticatedUserService;
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

    @GetMapping("listar_conversas_grupos")
    public ResponseEntity<List<GroupConversationDTO>> getMyConversations_group() {
        User user = authenticatedUserService.getAuthenticatedUser();
        return ResponseEntity.ok(conversationService.getMyGroups(user));
    }

    /*
    *
    * ADMIN adiciona membro a um grupo
    * */
    @PostMapping("/add_member")
    public ResponseEntity<ConversationMember> addMemberToGroup(
            @RequestBody Add_member_conversationDTO add
    ) {
        ConversationMember member =conversationService.addMemberToGroup(add.conversationId(), add.userId(), add.role());

        return ResponseEntity.ok(member);
    }
    /*
    * ADMIN REMOVER ELEMENTO DO GRUPO
    * */
    @DeleteMapping("/group/remove_member")
    public ResponseEntity<Void> removeMember(
            @RequestBody RemoveGroupMemberRequest request
    ) {

        conversationService.removeMemberFromGroup(
                request.conversationId(),
                request.userId()
        );

        return ResponseEntity.noContent().build();
    }

    /*
    * ELEMENTO SAI DO GRUPO POR LIVRE VONTADE!
    * */
    @DeleteMapping("/{conversationId}/members/me")
    public ResponseEntity<Void> leaveGroup(
            @PathVariable UUID conversationId
    ) {

        conversationService.leaveGroup(conversationId);

        return ResponseEntity.noContent().build();
    }
}