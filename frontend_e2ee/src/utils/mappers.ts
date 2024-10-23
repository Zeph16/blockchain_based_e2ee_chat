import { useAuthStore } from "@/stores/auth";
import type { Conversation, Message, MessageDTO, RawMessage, Room, User, UserDTO } from "@/types";

export function mapUserToRoom(user: User): Room {
  const { user: thisUser } = useAuthStore();
  if (!thisUser) throw new Error('User not logged in')
  return {
    roomId: user.username,
    roomName: user.username,
    users: [user, {...thisUser, avatar: 'http://localhost:5173/user_light.png' }],
    avatar: 'http://localhost:5173/user_light.png'
  };
}

export function mapConversationToRoom(conversation: Conversation): Room {
  const { user: thisUser } = useAuthStore();
  if (!thisUser) throw new Error('User not logged in')
  const otherUser = thisUser.username === conversation.user1.username ? conversation.user2 : conversation.user1;
  return {
    roomId: otherUser.username,
    roomName: otherUser.username,
    users: [mapDTOToUser(otherUser), {...thisUser, avatar: 'http://localhost:5173/user_light.png' }],
    avatar: 'http://localhost:5173/user_light.png'
  };
}


export function mapUserToDTO(user: User): UserDTO {
  return {
    id: user._id,
    username: user.username,
    state: user.status.state === 'online' ? 'ONLINE' : 'OFFLINE',
    lastChanged: user.status.lastChanged
  };
}


export function mapDTOToUser(user: UserDTO): User {
  const date = new Date(user.lastChanged);
  console.log('DTO to map:')
  console.log(user);
  const state: 'online' | 'offline' = user.state === 'ONLINE' ? 'online' : 'offline';
  const mappedUser = {
    _id: user.id,
    username: user.username,
    avatar: 'http://localhost:5173/user_light.png',
    status: {
      state: state,
      lastChanged: `${date.getHours()}:${date.getMinutes().toString().padStart(2, '0')}`
    }
  };
  console.log('Mapped user:')
  console.log(mappedUser)
  return mappedUser;
}

export function mapRawToMessage(rawMessage: RawMessage): Message {
  const { user } = useAuthStore();
  if (!user) throw new Error('User not logged in');
  const roomId = user.username === rawMessage.sender.username ? rawMessage.recipient.username : rawMessage.sender.username;

  const date = new Date(rawMessage.timestamp)

  return {
    _id: rawMessage.id,
    username: rawMessage.sender.username,
    senderId: rawMessage.sender.username,
    roomId: roomId,
    avatar: 'http://localhost:5173/user_light.png',
    content: rawMessage.content,
    timestamp: `${date.getHours()}:${date.getMinutes().toString().padStart(2, '0')}`,
    date: date.toLocaleDateString('en-US', { day: "numeric", month: "long" })
  }

}
