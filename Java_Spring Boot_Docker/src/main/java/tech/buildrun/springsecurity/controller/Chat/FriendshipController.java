package tech.buildrun.springsecurity.controller.Chat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.buildrun.springsecurity.dtos.Chat.FriendshipActionDTO;
import tech.buildrun.springsecurity.dtos.Chat.FriendshipFindDTO;
import tech.buildrun.springsecurity.dtos.Chat.FriendshipRequestDTO;
import tech.buildrun.springsecurity.dtos.Chat.FriendshipResponseDTO;
import tech.buildrun.springsecurity.entities.Chat.FRIENDSHIP;
import tech.buildrun.springsecurity.services.Chat.FriendshipService;

import java.util.List;

@RestController
@RequestMapping("/api/friendships")
public class FriendshipController {

    private final FriendshipService friendshipService;

    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    // =========================================================
    // ENVIAR PEDIDO DE AMIZADE
    // =========================================================

    @PostMapping("/send")
    public ResponseEntity<FriendshipResponseDTO> sendFriendRequest(
            @RequestBody FriendshipRequestDTO dto
    ) {

        FRIENDSHIP friendship = friendshipService.sendFriendRequest(
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
    // BLOQUEAR UTILIZADOR
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
    // LISTAR PEDIDOS RECEBIDOS
    // =========================================================

    @GetMapping("/received")
    public ResponseEntity<List<FriendshipResponseDTO>> getReceivedRequests() {

        List<FriendshipResponseDTO> response = friendshipService
                .getReceivedRequests()
                .stream()
                .map(this::toResponseDTO)
                .toList();

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // LISTAR PEDIDOS ENVIADOS
    // =========================================================

    @GetMapping("/sent")
    public ResponseEntity<List<FriendshipResponseDTO>> getSentRequests() {

        List<FriendshipResponseDTO> response = friendshipService
                .getSentRequests()
                .stream()
                .map(this::toResponseDTO)
                .toList();

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // LISTAR AMIGOS
    // =========================================================

    @GetMapping("/friends")
    public ResponseEntity<List<FriendshipResponseDTO>> getFriends() {

        List<FriendshipResponseDTO> response = friendshipService
                .getAcceptedFriendships()
                .stream()
                .map(this::toResponseDTO)
                .toList();

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // PROCURAR AMIZADE COM UM UTILIZADOR
    // =========================================================

    @PostMapping("/find")
    public ResponseEntity<FriendshipResponseDTO> findFriendship(
            @RequestBody FriendshipFindDTO dto
    ) {

        FRIENDSHIP friendship =
                friendshipService.findFriendship(dto.addresseeId());

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
    // ENTITY -> DTO
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