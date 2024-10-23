package md.proj.encryptedchat.conversation;

import md.proj.encryptedchat.user.UserMapper;

public class ConversationMapper {
    public static ConversationDTO toDTO(Conversation conversation) {
        if (conversation == null) {
            return null;
        }

        ConversationDTO dto = new ConversationDTO();
        dto.setId(conversation.getId());
        dto.setUser1(UserMapper.toDTO(conversation.getUser1()));
        dto.setUser2(UserMapper.toDTO(conversation.getUser2()));
        dto.setActive(conversation.isActive());
        dto.setCreatedAt(conversation.getCreatedAt());
        dto.setUpdatedAt(conversation.getUpdatedAt());

        return dto;
    }
}
