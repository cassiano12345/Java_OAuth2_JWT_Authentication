package tech.buildrun.springsecurity.services.Chat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.buildrun.springsecurity.entities.Chat.FRIENDSHIP;
import tech.buildrun.springsecurity.entities.Chat.FriendshipStatus;
import tech.buildrun.springsecurity.entities.User;
import tech.buildrun.springsecurity.repository.FriendshipRepository;
import tech.buildrun.springsecurity.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    public FriendshipService(
            FriendshipRepository friendshipRepository,
            UserRepository userRepository
    ) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // ENVIAR PEDIDO DE AMIZADE
    // =========================================================

    @Transactional
    public FRIENDSHIP sendFriendRequest(
            UUID requesterId,
            UUID addresseeId
    ) {

        if (requesterId.equals(addresseeId)) {
            throw new RuntimeException(
                    "Você não pode enviar um pedido de amizade para si mesmo."
            );
        }

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() ->
                        new RuntimeException("Usuário solicitante não encontrado.")
                );

        User addressee = userRepository.findById(addresseeId)
                .orElseThrow(() ->
                        new RuntimeException("Usuário destinatário não encontrado.")
                );

        // Verifica se já existe uma relação no sentido atual
        boolean exists = friendshipRepository
                .existsByRequesterAndAddressee(requester, addressee);

        if (exists) {
            throw new RuntimeException(
                    "Já existe uma relação entre esses usuários."
            );
        }

        // Verifica se o outro usuário já enviou um pedido
        boolean reverseExists = friendshipRepository
                .existsByRequesterAndAddressee(addressee, requester);

        if (reverseExists) {
            throw new RuntimeException(
                    "Já existe uma solicitação enviada pelo outro usuário."
            );
        }

        FRIENDSHIP friendship = new FRIENDSHIP();

        friendship.setRequester(requester);
        friendship.setAddressee(addressee);
        friendship.setStatus(FriendshipStatus.PENDING);

        return friendshipRepository.save(friendship);
    }

    // =========================================================
    // ACEITAR PEDIDO
    // =========================================================

    @Transactional
    public FRIENDSHIP acceptFriendRequest(UUID friendshipId) {

        FRIENDSHIP friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() ->
                        new RuntimeException("Pedido de amizade não encontrado.")
                );

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new RuntimeException(
                    "Este pedido de amizade não está pendente."
            );
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);

        return friendshipRepository.save(friendship);
    }

    // =========================================================
    // REJEITAR PEDIDO
    // =========================================================

    @Transactional
    public FRIENDSHIP rejectFriendRequest(UUID friendshipId) {

        FRIENDSHIP friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() ->
                        new RuntimeException("Pedido de amizade não encontrado.")
                );

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new RuntimeException(
                    "Este pedido de amizade não está pendente."
            );
        }

        friendship.setStatus(FriendshipStatus.REJECTED);

        return friendshipRepository.save(friendship);
    }

    // =========================================================
    // BLOQUEAR USUÁRIO
    // =========================================================

    @Transactional
    public FRIENDSHIP blockUser(UUID friendshipId) {

        FRIENDSHIP friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() ->
                        new RuntimeException("Relação de amizade não encontrada.")
                );

        friendship.setStatus(FriendshipStatus.BLOCKED);

        return friendshipRepository.save(friendship);
    }

    // =========================================================
    // BUSCAR PEDIDOS RECEBIDOS
    // =========================================================

    public List<FRIENDSHIP> getReceivedRequests(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado.")
                );

        return friendshipRepository.findByAddresseeAndStatus(
                user,
                FriendshipStatus.PENDING
        );
    }

    // =========================================================
    // BUSCAR PEDIDOS ENVIADOS
    // =========================================================

    public List<FRIENDSHIP> getSentRequests(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado.")
                );

        return friendshipRepository.findByRequesterAndStatus(
                user,
                FriendshipStatus.PENDING
        );
    }

    // =========================================================
    // BUSCAR AMIZADES DO USUÁRIO
    // =========================================================

    public List<FRIENDSHIP> getAcceptedFriendships(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado.")
                );

        List<FRIENDSHIP> sent = friendshipRepository
                .findByRequesterAndStatus(
                        user,
                        FriendshipStatus.ACCEPTED
                );

        List<FRIENDSHIP> received = friendshipRepository
                .findByAddresseeAndStatus(
                        user,
                        FriendshipStatus.ACCEPTED
                );

        sent.addAll(received);

        return sent;
    }

    // =========================================================
    // BUSCAR UMA RELAÇÃO ESPECÍFICA
    // =========================================================

    public FRIENDSHIP findFriendship(
            UUID requesterId,
            UUID addresseeId
    ) {

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() ->
                        new RuntimeException("Usuário solicitante não encontrado.")
                );

        User addressee = userRepository.findById(addresseeId)
                .orElseThrow(() ->
                        new RuntimeException("Usuário destinatário não encontrado.")
                );

        return friendshipRepository
                .findByRequesterAndAddressee(requester, addressee)
                .orElseThrow(() ->
                        new RuntimeException("Relação de amizade não encontrada.")
                );
    }

    // =========================================================
    // REMOVER AMIZADE
    // =========================================================

    @Transactional
    public void removeFriendship(UUID friendshipId) {

        FRIENDSHIP friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() ->
                        new RuntimeException("Amizade não encontrada.")
                );

        friendshipRepository.delete(friendship);
    }
}