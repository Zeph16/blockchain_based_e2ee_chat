package md.proj.encryptedchat.user;

import lombok.RequiredArgsConstructor;
import md.proj.encryptedchat.chatmessage.ChatMessage;
import md.proj.encryptedchat.chatmessage.ChatMessageDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class UserMapper {

    public static UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setState(user.getState());
        userDTO.setLastChanged(user.getLastChanged());

        return userDTO;
    }
}
