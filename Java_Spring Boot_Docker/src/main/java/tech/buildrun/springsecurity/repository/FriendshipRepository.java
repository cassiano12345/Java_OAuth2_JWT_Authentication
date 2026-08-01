package tech.buildrun.springsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.buildrun.springsecurity.entities.Chat.FRIENDSHIP;
import tech.buildrun.springsecurity.entities.Chat.FriendshipStatus;
import tech.buildrun.springsecurity.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FriendshipRepository extends JpaRepository<FRIENDSHIP, UUID> {

    @Query("""
SELECT f
FROM FRIENDSHIP f
WHERE
    (f.requester = :user1 AND f.addressee = :user2)
OR
    (f.requester = :user2 AND f.addressee = :user1)
""")
    Optional<FRIENDSHIP> findFriendshipBetweenUsers(
            @Param("user1") User user1,
            @Param("user2") User user2
    );
    // Pedidos enviados por um usuário
    List<FRIENDSHIP> findByRequesterAndStatus(
            User requester,
            FriendshipStatus status
    );

    // Pedidos recebidos por um usuário
    List<FRIENDSHIP> findByAddresseeAndStatus(
            User addressee,
            FriendshipStatus status
    );

    // Relação específica entre dois usuários
    Optional<FRIENDSHIP> findByRequesterAndAddressee(
            User requester,
            User addressee
    );

    // Todas as relações onde o usuário é quem enviou
    List<FRIENDSHIP> findByRequester(User requester);

    // Todas as relações onde o usuário recebeu
    List<FRIENDSHIP> findByAddressee(User addressee);

    // Verifica se existe relação entre dois usuários
    boolean existsByRequesterAndAddressee(
            User requester,
            User addressee
    );
}