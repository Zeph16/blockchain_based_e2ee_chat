package md.proj.encryptedchat.chatmessage;

import lombok.RequiredArgsConstructor;
import md.proj.encryptedchat.user.User;
import md.proj.encryptedchat.user.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;

    private final UserService userService;

    public ChatMessage saveMessage(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessage> findMessagesByUser(String username) {
        return chatMessageRepository.findBySender_UsernameOrRecipient_Username(username, username);
    }

    public Optional<ChatMessage> findById(UUID id) {
        return chatMessageRepository.findById(id);
    }

    public void deleteMessage(UUID id) {
        chatMessageRepository.deleteById(id);
    }

    public List<ChatMessage> findConversation(String username1, String username2) {
        User user1 = userService.findByUsername(username1);
        User user2 = userService.findByUsername(username2);

        return chatMessageRepository.findConversation(user1.getId(), user2.getId());
    }

}
