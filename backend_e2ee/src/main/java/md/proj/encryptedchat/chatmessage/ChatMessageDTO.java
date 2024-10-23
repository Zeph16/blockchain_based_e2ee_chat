package md.proj.encryptedchat.chatmessage;

import lombok.Data;

@Data
public class ChatMessageDTO {
    private String senderUsername;
    private String recipientUsername;
    private String content;
    private String dh;
    private int pn;
    private int n;
}