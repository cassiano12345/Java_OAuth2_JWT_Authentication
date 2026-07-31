package tech.buildrun.springsecurity.entities.Chat;

import jakarta.persistence.*;
import tech.buildrun.springsecurity.entities.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "MESSAGE_READ",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_message_read_user_message",
                        columnNames = {"message_id", "user_id"}
                )
        }
)
public class MessageRead {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "message_read_id")
    private UUID messageReadId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "read_at", nullable = false)
    private LocalDateTime readAt;


    public UUID getMessageReadId() {
        return messageReadId;
    }

    public void setMessageReadId(UUID messageReadId) {
        this.messageReadId = messageReadId;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
}