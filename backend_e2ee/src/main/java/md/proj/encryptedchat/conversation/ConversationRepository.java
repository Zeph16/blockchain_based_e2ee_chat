package md.proj.encryptedchat.conversation;

import md.proj.encryptedchat.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    @Query("SELECT c FROM Conversation c WHERE c.user1 = :user OR c.user2 = :user")
    List<Conversation> findAllByUser(User user);

    @Query("SELECT c FROM Conversation c WHERE (c.user1 = :user1 AND c.user2 = :user2) OR (c.user1 = :user2 AND c.user2 = :user1)")
    Optional<Conversation> findByUsers(User user1, User user2);

    @Query("SELECT c FROM Conversation c WHERE (c.user1 = :user OR c.user2 = :user) AND c.active = true")
    List<Conversation> findAllActiveByUser(User user);

    @Query("SELECT c FROM Conversation c WHERE (c.user1 = :user OR c.user2 = :user) AND c.active = false")
    List<Conversation> findAllInactiveByUser(User user);
}
