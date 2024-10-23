package md.proj.encryptedchat.conversation;

import lombok.RequiredArgsConstructor;
import md.proj.encryptedchat.user.User;
import md.proj.encryptedchat.user.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;

    private final UserService userService;

    public Conversation findById(UUID conversationId) {
        return conversationRepository.findById(conversationId).orElse(null);

    }


    public Conversation requestConversation(UUID userId1, UUID userId2) {
        User user1 = userService.findUserById(userId1);
        User user2 = userService.findUserById(userId2);

        Optional<Conversation> existingConversation = conversationRepository.findByUsers(user1, user2);
        if (existingConversation.isPresent()) {
            throw new IllegalArgumentException("Conversation already exists");
        }

        Conversation newConversation = new Conversation();
        newConversation.setUser1(user1);
        newConversation.setUser2(user2);
        newConversation.setActive(false);

        return conversationRepository.save(newConversation);
    }

    public Conversation acceptConversation(UUID conversationId, UUID userId2) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        if (!conversation.getUser2().getId().equals(userId2)) {
            throw new IllegalArgumentException("User mismatch or not authorized");
        }

        conversation.setActive(true);

        return conversationRepository.save(conversation);
    }

    public Conversation rejectConversation(UUID conversationId, UUID userId2) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        if (!conversation.getUser2().getId().equals(userId2)) {
            throw new IllegalArgumentException("User mismatch or not authorized");
        }

        conversationRepository.delete(conversation);
        return conversation;
    }


    public List<Conversation> getActiveConversations(UUID userId) {
        User user = userService.findUserById(userId);

        return conversationRepository.findAllActiveByUser(user);
    }
    public List<Conversation> getInactiveConversations(UUID userId) {
        User user = userService.findUserById(userId);

        return conversationRepository.findAllInactiveByUser(user);
    }

    public List<User> getUsersInConversationWith(UUID userId) {
        List<Conversation> conversations = getActiveConversations(userId);

        List<User> users = new ArrayList<>();

        for (Conversation conversation : conversations) {
            if (conversation.getUser2().getId().equals(userId)) {
                users.add(conversation.getUser1());
            } else {
                users.add(conversation.getUser2());
            }
        }
        return users;
    }

    public void deleteConversation(UUID id) {
        conversationRepository.deleteById(id);
    }
}
