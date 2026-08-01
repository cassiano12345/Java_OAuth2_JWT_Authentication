package tech.buildrun.springsecurity.services;

import org.springframework.stereotype.Service;
import tech.buildrun.springsecurity.dtos.Chat.UserSearchResponseDTO;
import tech.buildrun.springsecurity.entities.Chat.FRIENDSHIP;
import tech.buildrun.springsecurity.entities.User;
import tech.buildrun.springsecurity.repository.FriendshipRepository;
import tech.buildrun.springsecurity.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public UserService(UserRepository userRepository, FriendshipRepository friendshipRepository, AuthenticatedUserService authenticatedUserService) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    // =========================================================
    // BUSCAR USUÁRIO PELO ID
    // =========================================================

    public User findById(UUID userId) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado")
                );
    }

    // =========================================================
    // BUSCAR USUÁRIO PELO USERNAME EXATO
    // =========================================================

    public User findByUsername(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado")
                );
    }

    // =========================================================
    // PESQUISAR USUÁRIOS PELO USERNAME
    // =========================================================

    public List<UserSearchResponseDTO> searchByUsername(String username) {

        if (username == null || username.isBlank()) {
            return List.of();
        }

        User authenticatedUser = authenticatedUserService.getAuthenticatedUser();

        List<User> users = userRepository.findByUsernameContainingIgnoreCase(
                username.trim()
        );

        return users.stream()

                // Não mostrar o próprio utilizador
                .filter(user -> !user.getUserId().equals(authenticatedUser.getUserId()))

                .map(user -> {

                    String friendshipStatus = "NONE";

                    Optional<FRIENDSHIP> friendship = friendshipRepository.findFriendshipBetweenUsers(authenticatedUser, user);

                    if (friendship.isPresent()) {

                        FRIENDSHIP relation = friendship.get();

                        switch (relation.getStatus()) {

                            case ACCEPTED -> friendshipStatus = "ACCEPTED";

                            case BLOCKED -> friendshipStatus = "BLOCKED";

                            case PENDING -> {

                                if (relation.getRequester().getUserId()
                                        .equals(authenticatedUser.getUserId())) {

                                    friendshipStatus = "PENDING_SENT";

                                } else {

                                    friendshipStatus = "PENDING_RECEIVED";

                                }

                            }

                        }

                    }

                    return new UserSearchResponseDTO(

                            user.getUserId(),
                            user.getUsername(),
                            friendshipStatus

                    );

                })

                .toList();

    }

    // =========================================================
    // LISTAR TODOS OS USUÁRIOS
    // =========================================================

    public List<User> findAll() {

        return userRepository.findAll();
    }
}
