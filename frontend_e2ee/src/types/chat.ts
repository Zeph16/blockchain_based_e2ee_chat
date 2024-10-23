import type { User, UserDTO } from "./user";

export interface Message {
  _id: string;
  senderId: string;
  username: string;
  avatar: string;
  content: string;
  timestamp: string;
  date: string;
  roomId: string;
}

export interface MessageDTO {
  senderUsername: string;
  recipientUsername: string;
  content: string;
  dh: string;
  pn: number;
  n: number;
}

export interface RawMessage {
  id: string;
  content: string;
  recipient: UserDTO;
  sender: UserDTO;
  timestamp: string;
  dh: string;
  pn: number;
  n: number;
}

export interface Room {
  roomId: string;
  roomName: string;
  users: User[];
  avatar: string;
  lastMessage?: Message;
}

export interface ChatState {
  rooms: Room[];
  activeRoom: string | null;
  messages: { [roomId: string]: Message[] };
}

export interface AddMessagePayload {
  roomId: string;
  message: Message;
}

export interface Conversation {
  id: string;
  user1: UserDTO;
  user2: UserDTO;
  active: string;
  createdAt: string;
  updatedAt: string;
}

export interface ConversationsState {
  active: Record<string, Conversation>;
  pending: Record<string, Conversation>;
}
