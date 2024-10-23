package md.proj.encryptedchat.conversation;

import lombok.RequiredArgsConstructor;
import md.proj.encryptedchat.exception.ResourceNotFoundException;
import md.proj.encryptedchat.security.SecurityUtils;
import md.proj.encryptedchat.user.User;
import md.proj.encryptedchat.user.UserMapper;
import md.proj.encryptedchat.user.UserService;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.HttpMethod;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    private final SimpMessagingTemplate messagingTemplate;

    private final UserService userService;


    @PostMapping("/request/{username}")
    public ConversationDTO requestConversation(@PathVariable String username) {
        UUID userId1 = SecurityUtils.getAuthenticatedUserId();
        User user2 = userService.findByUsername(username);
        if (user2 == null) {
            throw new ResourceNotFoundException(username);
        }

        Conversation conversation = conversationService.requestConversation(userId1, user2.getId());

        messagingTemplate.convertAndSendToUser(user2.getUsername(), "/queue/conversation",
                new ConversationStateResponse(ConversationMapper.toDTO(conversation), ConversationState.PENDING));

        return ConversationMapper.toDTO(conversation);
    }

    @PostMapping("/accept/{conversationId}")
    public ConversationDTO acceptConversation(@PathVariable UUID conversationId) {
        UUID userId2 = SecurityUtils.getAuthenticatedUserId();
        Conversation conversation = conversationService.acceptConversation(conversationId, userId2);
        messagingTemplate.convertAndSendToUser(conversation.getUser1().getUsername(), "/queue/conversation",
                new ConversationStateResponse(ConversationMapper.toDTO(conversation), ConversationState.ACCEPTED));

        return ConversationMapper.toDTO(conversation);
    }

    @PostMapping("/reject/{conversationId}")
    public void rejectConversation(@PathVariable UUID conversationId) {
        UUID userId2 = SecurityUtils.getAuthenticatedUserId();
        Conversation conversation = conversationService.rejectConversation(conversationId, userId2);

        messagingTemplate.convertAndSendToUser(conversation.getUser1().getUsername(), "/queue/conversation",
                new ConversationStateResponse(ConversationMapper.toDTO(conversation), ConversationState.REJECTED));
    }

    @DeleteMapping("/delete/{conversationId}")
    public void deleteConversation(@PathVariable UUID conversationId) throws AuthenticationException {
        Conversation c = conversationService.findById(conversationId);
        if (c == null) {
            throw new ResourceNotFoundException("Conversation not found");
        }
        UUID userId = SecurityUtils.getAuthenticatedUserId();
        if (!c.getUser1().getId().equals(userId) && !c.getUser2().getId().equals(userId)) {
            throw new AuthenticationException("Not permitted to delete conversation");
        }

        conversationService.deleteConversation(conversationId);

        List<User> usersToBeNotified = conversationService.getUsersInConversationWith(userId);
        for (User u : usersToBeNotified) {
            messagingTemplate.convertAndSendToUser(u.getUsername(), "/queue/conversation",
                    new ConversationStateResponse(ConversationMapper.toDTO(c), ConversationState.DELETED));
        }
    }

    @GetMapping("/active")
    public List<ConversationDTO> getActiveConversations() {
        UUID userId = SecurityUtils.getAuthenticatedUserId();
        return conversationService.getActiveConversations(userId).stream().map(ConversationMapper::toDTO).toList();
    }

    @GetMapping("/inactive")
    public List<ConversationDTO> getInactiveConversations() {
        UUID userId = SecurityUtils.getAuthenticatedUserId();
        return conversationService.getInactiveConversations(userId).stream().map(ConversationMapper::toDTO).toList();
    }
}
