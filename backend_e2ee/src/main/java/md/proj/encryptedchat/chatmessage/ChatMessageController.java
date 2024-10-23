package md.proj.encryptedchat.chatmessage;

import lombok.RequiredArgsConstructor;
import md.proj.encryptedchat.security.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatMessageController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageMapper mapper;
    private final ChatMessageService service;

    @MessageMapping("/chat")
    public void newMessage(ChatMessageDTO message) throws Exception {
        ChatMessage chatMessage = service.saveMessage(mapper.toEntity(message));
        messagingTemplate.convertAndSendToUser(String.valueOf(message.getRecipientUsername()), "/queue/messages", chatMessage);
    }

    @PostMapping("/api/messages")
    public ResponseEntity<ChatMessage> postMessage(@RequestBody ChatMessageDTO message) {
        System.out.println("Message sent by " + message.getSenderUsername() + " to " + message.getRecipientUsername());
        ChatMessage chatMessage = service.saveMessage(mapper.toEntity(message));
        messagingTemplate.convertAndSendToUser(String.valueOf(message.getRecipientUsername()), "/queue/messages", chatMessage);
        return ResponseEntity.ok(chatMessage);
    }

    @GetMapping("/api/messages/{otherUsername}")
    public ResponseEntity<List<ChatMessage>> findConversation(@PathVariable String otherUsername) throws Exception {
        String currentUser = SecurityUtils.getCurrentUsername();

        return ResponseEntity.ok(service.findConversation(currentUser, otherUsername));
    }
}
