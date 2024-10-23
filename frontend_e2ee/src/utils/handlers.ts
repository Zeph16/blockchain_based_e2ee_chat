import { useAuthStore } from "@/stores/auth";
import type { Conversation, PrekeyBundle, RawMessage, UserDTO } from "@/types";
import { mapConversationToRoom, mapDTOToUser, mapRawToMessage, mapUserToRoom } from "./mappers";
import { useUserStore } from "@/stores/user";
import { useChatStore } from "@/stores/chat";
import { useCryptoStore } from "@/stores/crypto";
import { BlockchainService } from "./BlockchainService";
import { useConversationStore } from "@/stores/conversation";
import { decryptMessage } from "@/crypto/messageOps";
import { base64ToBytes, hexToBytes } from "./utils";

export async function onUserChange(userDTO: UserDTO) {
  console.log('Message received to onUserChange:');
  console.log(userDTO);

  const authStore = useAuthStore();
  if (!authStore.privateKey) {
    console.log('No wallet found');
    return;
  }
  if (!authStore.user) {
    console.log('No logged in user found');
    return;
  }

  if (userDTO.username === authStore.user.username) {
    console.log('User is the same as the current user, aborting new user storage');
    return;
  }
  const user = mapDTOToUser(userDTO);
  const { username } = user;
  const userStore = useUserStore();
  const chatStore = useChatStore();
  const cryptoStore = useCryptoStore();
  if (userStore.getUserById(user._id)) {
    userStore.updateUser(user._id, user);
    chatStore.updateRoom(mapUserToRoom(user));
    return;
  }

  

  console.log('New user:')
  console.log(user)

  try {
    console.log('Fetching prekey bundle for', user.username);
    const blockchain = await BlockchainService.getInstance('local', authStore.privateKey);
    const bundle: PrekeyBundle = await blockchain.getPrekey(username);

    console.log('Prekey bundle for', user.username, ' :')
    console.log(bundle);

    cryptoStore.setPrekeyBundle({ username, bundle });


    chatStore.addRoom(mapUserToRoom(user));
    userStore.addUser(user);
  } catch {
    console.error('Failed to fetch prekey');
  }
}

export async function onMessage(rawMessage: RawMessage) {
  const chatStore = useChatStore();
  console.log('Message received to onNewMessage:');
  console.log(rawMessage)
  const roomMessages = chatStore.messages[rawMessage.recipient.username];
  const message = mapRawToMessage(rawMessage);


  if (chatStore.messages[message.roomId] && chatStore.messages[message.roomId].find(m => m._id === message._id)) {
    console.log('Message already exists, aborting');
    return;
  }


  if (roomMessages && roomMessages.find(m => m._id === rawMessage.id)) {
    return;
  }
  console.log('Mapped message:');
  console.log(message);

  const decryptedRawMessage = await decryptMessage(rawMessage);
  console.log("Decrypted message:")
  console.log(decryptedRawMessage);

  const decryptedMessage = mapRawToMessage(decryptedRawMessage);


  chatStore.addMessage(decryptedMessage);


  // WITHOUT DECRYPTION
  // chatStore.addMessage({...mapRawToMessage(rawMessage), content: new TextDecoder().decode(base64ToBytes(rawMessage.content))})
}

export async function onConversation(conversationState: { state: "PENDING" | "ACCEPTED" | "REJECTED" | "DELETED" | "ONLINE" | "OFFLINE" | "REQUESTING", conversationDTO: Conversation}) {
  const conversation = conversationState.conversationDTO;
  const chatStore = useChatStore();
  const conversationStore = useConversationStore();
  switch (conversationState.state) {
    case "REQUESTING":
      
    case "PENDING":
      conversationStore.addPending(conversation);
      break;
    case "ACCEPTED":
      conversationStore.removePending(conversation.id);
      await initializeConversation(conversation);
      break;
    case "REJECTED":
      conversationStore.removePending(conversation.id);
      break;
    case "DELETED":
      // TODO: delete from active
      // TODO: delete room
      // TODO: delete keys
      // TODO: delete double ratchet session
      break;
    case "ONLINE":
    case "OFFLINE":
      conversationStore.updateActive(conversation.id, conversation);
      chatStore.updateRoom(mapConversationToRoom(conversation));
      break;
  }
  
}

export async function populateRooms() {
  const { active } = useConversationStore();
  console.log("Populating rooms")
  const entries = Object.entries(active);
  for (const [key, value] of entries) {
    console.log(key, value);
    await initializeConversation(value);
  };

}

export async function initializeConversation(conversation: Conversation) {
  const conversationStore = useConversationStore();
  if (!conversation.active) {
    conversationStore.addPending(conversation);
    return;
  }
  const chatStore = useChatStore();
  const authStore = useAuthStore();
  if (!authStore.user) {
    console.log('No logged in user found');
    return;
  }
  const otherUser = conversation.user1.username === authStore.user.username ? conversation.user2 : conversation.user1;
  const bundle = await fetchKeys(otherUser);
  if (!bundle) {
    console.error('No prekey when initializing conversation');
    return;
  }

  conversationStore.addActive(conversation);
  chatStore.addRoom(mapConversationToRoom(conversation));
}

export async function fetchKeys(user: UserDTO): Promise<PrekeyBundle | null> {
  const authStore = useAuthStore();
  const cryptoStore = useCryptoStore();
  const userStore = useUserStore();
  if (!authStore.privateKey) {
    console.log('No wallet found when fetching keys');
    return null;
  }
  try {
    console.log('Fetching prekey bundle for', user.username);
    const blockchain = await BlockchainService.getInstance('local', authStore.privateKey);
    const bundle: PrekeyBundle = await blockchain.getPrekey(user.username);

    console.log('Prekey bundle for', user.username, ' :')
    console.log(bundle);

    cryptoStore.setPrekeyBundle({ username: user.username, bundle });

    userStore.addUser(mapDTOToUser(user));
    return bundle;
  } catch (e: any) {
    console.error('Failed to fetch prekey');
    console.log(e);
    return null;
  }
}
