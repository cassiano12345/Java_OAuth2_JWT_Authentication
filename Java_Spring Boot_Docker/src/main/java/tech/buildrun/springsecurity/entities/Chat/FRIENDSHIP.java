package tech.buildrun.springsecurity.entities.Chat;

import jakarta.persistence.*;
import tech.buildrun.springsecurity.entities.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "FRIENDSHIP",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_friendship_users",
                        columnNames = {"requester_id", "addressee_id"}
                )
        }
)
public class FRIENDSHIP {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "friendship_id")
    private UUID friendshipId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "addressee_id", nullable = false)
    private User addressee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendshipStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public FRIENDSHIP() {
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (status == null) {
            status = FriendshipStatus.PENDING;
        }
    }

    public UUID getFriendshipId() {
        return friendshipId;
    }

    public void setFriendshipId(UUID friendshipId) {
        this.friendshipId = friendshipId;
    }

    public User getRequester() {
        return requester;
    }

    public void setRequester(User requester) {
        this.requester = requester;
    }

    public User getAddressee() {
        return addressee;
    }

    public void setAddressee(User addressee) {
        this.addressee = addressee;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}