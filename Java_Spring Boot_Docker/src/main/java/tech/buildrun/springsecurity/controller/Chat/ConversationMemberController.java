package tech.buildrun.springsecurity.controller.Chat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.buildrun.springsecurity.dtos.Chat.ConversationMemberDTO;
import tech.buildrun.springsecurity.dtos.Chat.ConversationMemberResponseDTO;
import tech.buildrun.springsecurity.entities.Chat.ConversationMember;
import tech.buildrun.springsecurity.services.Chat.ConversationMemberService;

import java.util.List;

@RestController
@RequestMapping("/api/conversation-members")
public class ConversationMemberController {

    private final ConversationMemberService conversationMemberService;

    public ConversationMemberController(
            ConversationMemberService conversationMemberService
    ) {
        this.conversationMemberService = conversationMemberService;
    }

    // Buscar todos os membros de uma conversa
    @PostMapping("/find-by-conversation")
    public ResponseEntity<List<ConversationMemberResponseDTO>> findMembersByConversation(
            @RequestBody ConversationMemberDTO dto
    ) {

        List<ConversationMember> members =
                conversationMemberService.findMembersByConversation(
                        dto.conversationId()
                );

        List<ConversationMemberResponseDTO> response = members.stream()
                .map(this::toResponseDTO)
                .toList();

        return ResponseEntity.ok(response);
    }

    // Buscar todas as conversas de um usuário
    @PostMapping("/find-by-user")
    public ResponseEntity<List<ConversationMemberResponseDTO>> findConversationsByUser(
            @RequestBody ConversationMemberDTO dto
    ) {

        List<ConversationMember> members =
                conversationMemberService.findConversationsByUser(
                        dto.userId()
                );

        List<ConversationMemberResponseDTO> response = members.stream()
                .map(this::toResponseDTO)
                .toList();

        return ResponseEntity.ok(response);
    }

    // Buscar um membro específico
    @PostMapping("/find")
    public ResponseEntity<ConversationMemberResponseDTO> findMember(
            @RequestBody ConversationMemberDTO dto
    ) {

        ConversationMember member =
                conversationMemberService.findMember(
                        dto.conversationId(),
                        dto.userId()
                );

        return ResponseEntity.ok(toResponseDTO(member));
    }

    // Verificar se o usuário pertence à conversa
    @PostMapping("/is-member")
    public ResponseEntity<Boolean> isMember(
            @RequestBody ConversationMemberDTO dto
    ) {

        boolean isMember =
                conversationMemberService.isMember(
                        dto.conversationId(),
                        dto.userId()
                );

        return ResponseEntity.ok(isMember);
    }

    // Verificar se o usuário é administrador
    @PostMapping("/is-admin")
    public ResponseEntity<Boolean> isAdmin(
            @RequestBody ConversationMemberDTO dto
    ) {

        boolean isAdmin =
                conversationMemberService.isAdmin(
                        dto.conversationId(),
                        dto.userId()
                );

        return ResponseEntity.ok(isAdmin);
    }

    // Remover um membro
    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(
            @RequestBody ConversationMemberDTO dto
    ) {

        conversationMemberService.delete(
                dto.conversationId(),
                dto.userId()
        );

        return ResponseEntity.noContent().build();
    }

    // Converter Entity para Response DTO
    private ConversationMemberResponseDTO toResponseDTO(
            ConversationMember member
    ) {

        return new ConversationMemberResponseDTO(
                member.getMemberId(),
                member.getUser().getUserId(),
                member.getUser().getUsername(),
                member.getRole(),
                member.getJoinedAt()
        );
    }
}