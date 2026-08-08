package tech.buildrun.springsecurity.repository.Chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.buildrun.springsecurity.entities.Chat.ConversationMember;
import tech.buildrun.springsecurity.entities.Chat.ConversationMemberRole;
import tech.buildrun.springsecurity.entities.Chat.ConversationType;
import tech.buildrun.springsecurity.entities.User;

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

    @Query("""
    SELECT cm.user
    FROM ConversationMember cm
    WHERE cm.conversation.conversationId = :conversationId
      AND cm.user.userId <> :senderId
""")
    List<User> findUsersByConversationExcludingSender(
            @Param("conversationId") UUID conversationId,
            @Param("senderId") UUID senderId
    );

    Optional<ConversationMember> findByConversation_ConversationIdAndUser_UserId(
            UUID conversationId,
            UUID userId
    );
    List<ConversationMember> findByUserAndConversation_Type(
            User user,
            ConversationType type
    );

    List<ConversationMember> findByConversation_ConversationIdAndRole(
            UUID conversationId,
            ConversationMemberRole role
    );

    List<ConversationMember> findByUser_UserIdAndConversation_Type(
            UUID userId,
            ConversationType type
    );

    long countByConversation_ConversationIdAndRole(
            UUID conversationId,
            ConversationMemberRole role
    );

    @Query("""
    SELECT DISTINCT cm
    FROM ConversationMember cm
    JOIN FETCH cm.conversation c
    JOIN FETCH c.members members
    JOIN FETCH members.user
    LEFT JOIN FETCH c.lastMessage
    WHERE cm.user = :user
    ORDER BY c.lastMessageAt DESC NULLS LAST
""")
    List<ConversationMember> findByUserWithConversation(User user);
}
