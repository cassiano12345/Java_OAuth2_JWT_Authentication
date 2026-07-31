package tech.buildrun.springsecurity.repository.Chat;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.buildrun.springsecurity.entities.Chat.ConversationMember;
import tech.buildrun.springsecurity.entities.Chat.ConversationMemberRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationMemberRepository
        extends JpaRepository<ConversationMember, UUID> {


    List<ConversationMember> findByConversation_ConversationId(UUID conversationId);

    List<ConversationMember> findByUser_UserId(UUID userId);

    boolean existsByConversation_ConversationIdAndUser_UserId(
            UUID conversationId,
            UUID userId
    );


    Optional<ConversationMember> findByConversation_ConversationIdAndUser_UserId(
            UUID conversationId,
            UUID userId
    );

    List<ConversationMember> findByConversation_ConversationIdAndRole(
            UUID conversationId,
            ConversationMemberRole role
    );
}
