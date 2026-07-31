package tech.buildrun.springsecurity.controller.Chat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.buildrun.springsecurity.dtos.Chat.FriendshipActionDTO;
import tech.buildrun.springsecurity.dtos.Chat.FriendshipRequestDTO;
import tech.buildrun.springsecurity.dtos.Chat.FriendshipResponseDTO;
import tech.buildrun.springsecurity.dtos.Chat.UserIdDTO;
import tech.buildrun.springsecurity.entities.Chat.FRIENDSHIP;
import tech.buildrun.springsecurity.services.Chat.FriendshipService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/friendships")
public class FriendshipController {

    private final FriendshipService friendshipService;

    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    // =========================================================
    // ENVIAR PEDIDO
    // =========================================================

    @PostMapping("/send")
    public ResponseEntity<FriendshipResponseDTO> sendFriendRequest(
            @RequestBody FriendshipRequestDTO dto
    ) {

        FRIENDSHIP friendship = friendshipService.sendFriendRequest(
                dto.requesterId(),
                dto.addresseeId()
        );

        return ResponseEntity.ok(toResponseDTO(friendship));
    }

    // =========================================================
    // ACEITAR PEDIDO
    // =========================================================

    @PutMapping("/accept")
    public ResponseEntity<FriendshipResponseDTO> acceptFriendRequest(
            @RequestBody FriendshipActionDTO dto
    ) {

        FRIENDSHIP friendship =
                friendshipService.acceptFriendRequest(dto.friendshipId());

        return ResponseEntity.ok(toResponseDTO(friendship));
    }

    // =========================================================
    // REJEITAR PEDIDO
    // =========================================================

    @PutMapping("/reject")
    public ResponseEntity<FriendshipResponseDTO> rejectFriendRequest(
            @RequestBody FriendshipActionDTO dto
    ) {

        FRIENDSHIP friendship =
                friendshipService.rejectFriendRequest(dto.friendshipId());

        return ResponseEntity.ok(toResponseDTO(friendship));
    }

    // =========================================================
    // BLOQUEAR
    // =========================================================

    @PutMapping("/block")
    public ResponseEntity<FriendshipResponseDTO> blockUser(
            @RequestBody FriendshipActionDTO dto
    ) {

        FRIENDSHIP friendship =
                friendshipService.blockUser(dto.friendshipId());

        return ResponseEntity.ok(toResponseDTO(friendship));
    }

    // =========================================================
    // PEDIDOS RECEBIDOS
    // =========================================================

    @PostMapping("/received")
    public ResponseEntity<List<FriendshipResponseDTO>> getReceivedRequests(
            @RequestBody UserIdDTO dto
    ) {

        List<FRIENDSHIP> friendships =
                friendshipService.getReceivedRequests(dto.userId());

        return ResponseEntity.ok(
                friendships.stream()
                        .map(this::toResponseDTO)
                        .toList()
        );
    }

    // =========================================================
    // PEDIDOS ENVIADOS
    // =========================================================

    @PostMapping("/sent")
    public ResponseEntity<List<FriendshipResponseDTO>> getSentRequests(
            @RequestBody UserIdDTO dto
    ) {

        List<FRIENDSHIP> friendships =
                friendshipService.getSentRequests(dto.userId());

        return ResponseEntity.ok(
                friendships.stream()
                        .map(this::toResponseDTO)
                        .toList()
        );
    }

    // =========================================================
    // AMIGOS ACEITOS
    // =========================================================

    @PostMapping("/friends")
    public ResponseEntity<List<FriendshipResponseDTO>> getFriends(
            @RequestBody UserIdDTO dto
    ) {

        List<FRIENDSHIP> friendships =
                friendshipService.getAcceptedFriendships(dto.userId());

        return ResponseEntity.ok(
                friendships.stream()
                        .map(this::toResponseDTO)
                        .toList()
        );
    }

    // =========================================================
    // BUSCAR RELAÇÃO
    // =========================================================

    @PostMapping("/find")
    public ResponseEntity<FriendshipResponseDTO> findFriendship(
            @RequestBody FriendshipRequestDTO dto
    ) {

        FRIENDSHIP friendship =
                friendshipService.findFriendship(
                        dto.requesterId(),
                        dto.addresseeId()
                );

        return ResponseEntity.ok(toResponseDTO(friendship));
    }

    // =========================================================
    // REMOVER AMIZADE
    // =========================================================

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeFriendship(
            @RequestBody FriendshipActionDTO dto
    ) {

        friendshipService.removeFriendship(dto.friendshipId());

        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // CONVERTER ENTITY → DTO
    // =========================================================

    private FriendshipResponseDTO toResponseDTO(FRIENDSHIP friendship) {

        return new FriendshipResponseDTO(
                friendship.getFriendshipId(),
                friendship.getRequester().getUserId(),
                friendship.getRequester().getUsername(),
                friendship.getAddressee().getUserId(),
                friendship.getAddressee().getUsername(),
                friendship.getStatus(),
                friendship.getCreatedAt()
        );
    }
}
