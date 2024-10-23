package md.proj.encryptedchat.chatmessage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;


public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    List<ChatMessage> findBySender_UsernameOrRecipient_Username(String username, String username1);

    @Query("SELECT m FROM ChatMessage m WHERE (m.sender.id = :userId1 AND m.recipient.id = :userId2) OR (m.sender.id = :userId2 AND m.recipient.id = :userId1) ORDER BY m.timestamp")
    List<ChatMessage> findConversation(@Param("userId1") UUID userId1, @Param("userId2") UUID userId2);
}
