package md.proj.encryptedchat.chatmessage;

import lombok.RequiredArgsConstructor;
import md.proj.encryptedchat.user.User;
import md.proj.encryptedchat.user.UserService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class ChatMessageMapper {

    private final UserService userService;

    public ChatMessage toEntity(ChatMessageDTO chatMessageDTO) {
        if (chatMessageDTO == null) {
            throw new IllegalArgumentException("ChatMessageDTO cannot be null");
        }

        User sender = userService.findByUsername(chatMessageDTO.getSenderUsername());
        User recipient = userService.findByUsername(chatMessageDTO.getRecipientUsername());

        if (sender == null) {
            throw new IllegalArgumentException("Sender not found");
        }

        if (recipient == null) {
            throw new IllegalArgumentException("Recipient not found");
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(sender);
        chatMessage.setRecipient(recipient);
        chatMessage.setContent(chatMessageDTO.getContent());
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessage.setDh(chatMessageDTO.getDh());
        chatMessage.setPn(chatMessageDTO.getPn());
        chatMessage.setN(chatMessageDTO.getN());

        return chatMessage;
    }

    public static ChatMessageDTO toDTO(ChatMessage chatMessage) {
        if (chatMessage == null) {
            return null;
        }

        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        chatMessageDTO.setSenderUsername(chatMessage.getSender().getUsername());
        chatMessageDTO.setRecipientUsername(chatMessage.getRecipient().getUsername());
        chatMessageDTO.setContent(chatMessage.getContent());
        chatMessageDTO.setDh(chatMessage.getDh());
        chatMessageDTO.setPn(chatMessage.getPn());
        chatMessageDTO.setN(chatMessage.getN());

        return chatMessageDTO;
    }
}