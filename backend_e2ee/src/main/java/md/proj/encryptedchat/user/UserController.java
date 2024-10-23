package md.proj.encryptedchat.user;

import lombok.RequiredArgsConstructor;
import md.proj.encryptedchat.conversation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ConversationService conversationService;
    private final SimpMessagingTemplate messagingTemplate;

//    @MessageMapping("/user.connectUser")
//    @SendTo("/topic/public")
//    public UserDTO addUser(
//            @Payload UserDTO userDTO
//    ) {
//        User u = userService.findByUsername(userDTO.getUsername());
//        return UserMapper.toDTO(userService.connect(u));
//    }
//
//    @MessageMapping("/user.disconnectUser")
//    @SendTo("/topic/public")
//    public UserDTO disconnectUser(
//            @Payload UserDTO userDTO
//    ) {
//        User u = userService.findByUsername(userDTO.getUsername());
//        return UserMapper.toDTO(userService.disconnect(u));
//    }

    @MessageMapping("/user.connectUser")
    public void connectUser(
            @Payload UserDTO userDTO
    ) {
        User user = userService.connect(userService.findByUsername(userDTO.getUsername()));

        List<Conversation> conversations = conversationService.getActiveConversations(user.getId());
        for (Conversation c : conversations) {
            User u = c.getUser2().getId() == user.getId() ? c.getUser1() : c.getUser2();
            messagingTemplate.convertAndSendToUser(u.getUsername(), "/queue/conversation",
                    new ConversationStateResponse(ConversationMapper.toDTO(c), ConversationState.ONLINE));
        }


        // TODO: get all active conversations -> loop through them -> check whether user1 or user2 is the other user -> notify that user with that conversation object
    }

    @MessageMapping("/user.disconnectUser")
    public void disconnectUser(
            @Payload UserDTO userDTO
    ) {
        User user = userService.disconnect(userService.findByUsername(userDTO.getUsername()));

        List<Conversation> conversations = conversationService.getActiveConversations(user.getId());
        for (Conversation c : conversations) {
            User u = c.getUser2().getId() == user.getId() ? c.getUser1() : c.getUser2();
            messagingTemplate.convertAndSendToUser(u.getUsername(), "/queue/conversation",
                    new ConversationStateResponse(ConversationMapper.toDTO(c), ConversationState.OFFLINE));
        }
    }

    @GetMapping("/api/users")
    public ResponseEntity<List<UserDTO>> findAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers().stream().map(UserMapper::toDTO).toList());
    }
    @GetMapping("/api/users/online")
    public ResponseEntity<List<UserDTO>> findConnectedUsers() {
        return ResponseEntity.ok(userService.findConnectedUsers().stream().map(UserMapper::toDTO).toList());
    }
}
