package md.proj.encryptedchat.conversation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import md.proj.encryptedchat.user.UserDTO;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationDTO {
    private UUID id;
    private UserDTO user1;
    private UserDTO user2;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}