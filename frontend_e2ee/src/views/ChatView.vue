<script setup lang="ts">

import { useAuthStore } from '@/stores/auth';
import { useChatStore } from '@/stores/chat';
import { useWebSocketStore } from '@/stores/websocket';
import type { Conversation, MessageDTO, RawMessage, User, Message } from '@/types';
import { ref, onMounted, onUnmounted } from 'vue';
import { register } from 'vue-advanced-chat';
import { useRouter } from 'vue-router';
import api from '../utils/api'
import { initializeConversation, onConversation, onMessage } from '@/utils/handlers';
import CustomModal from '@/components/CustomModal.vue';
import UserProfile from '@/components/UserProfile.vue';
import SelfProfile from '@/components/SelfProfile.vue';
import RequestForm from '@/components/RequestForm.vue';
import RequestView from '@/components/RequestView.vue';
import { encryptMessage } from '@/crypto/messageOps';
import { mapRawToMessage } from '@/utils/mappers';

const router = useRouter();
const authStore = useAuthStore();
const chatStore = useChatStore();


function disconnectUser() {
  const webSocketStore = useWebSocketStore();
  console.log('Deactivating websocket...');
  webSocketStore.disconnectUser();
  webSocketStore.wsClient?.deactivate();
}

onMounted(async () => {
  const webSocketStore = useWebSocketStore();
  console.log('ChatView mounted, access token:');

  console.log(authStore.accessToken);
  console.log('ChatView mounted, user:');
  console.log(authStore.user);

  if (!authStore.user || !authStore.accessToken) {
    router.push('/signup')
    return;
  }
  const messagesQueue = `/user/${authStore.user.username}/queue/messages`
  const conversationQueue = `/user/${authStore.user.username}/queue/conversation`
  webSocketStore.initializeWebsocket({
    [messagesQueue]: onMessage,
    [conversationQueue]: (arg) => { onConversation(arg); if (arg.state === "PENDING") isRequestViewVisible.value = true; },
  })

  window.addEventListener('beforeunload', disconnectUser);
  await fetchRooms();
});

onUnmounted(() => {
  disconnectUser();
  window.removeEventListener('beforeunload', disconnectUser);
})


const roomsLoaded = ref(false);
const messagesLoaded = ref(false);
const currentRoom = ref('');
const isProfileVisible = ref(false);
const isSelfProfileVisible = ref(false);
const isRequestViewVisible = ref(false);
const isRequestFormVisible = ref(false);
const emptyUser: User = {
  _id: '',
  username: '',
  status: {
    state: 'offline',
    lastChanged: ''
  }
};
const selectedUser = ref<User>(emptyUser);

const roomActions = [
  { name: 'showProfile', title: 'Show Profile' },
  { name: 'disconnect', title: 'Disconnect' }
];




const fetchMessages = async ({ room, options = {} }: { room: any, options: any }) => {
  console.log('Fetching messages for room:', room.roomId);
  currentRoom.value = room.roomId;
  messagesLoaded.value = false;

  try {
    const response = await api.get(`/messages/${room.roomName}`);
    console.log('All messages from', room.roomName);
    console.log(response.data);
    const messages: RawMessage[] = response.data;
    messages.map((message) => {
      onMessage(message);
    })
  } catch (error: any) {
    console.error('Error fetching messages:', error);
  }


  console.log('Messages fetched for room:', room.roomName);
  messagesLoaded.value = true;
}

async function fetchRooms() {
  console.log('Fetching rooms');
  roomsLoaded.value = false;

  try {
    const pendingResponse = await api.get('/conversations/inactive');
    const pending: Conversation[] = pendingResponse.data;
    const activeResponse = await api.get('/conversations/active');
    const active: Conversation[] = activeResponse.data;
    const conversations = [...pending, ...active];
    console.log('All conversations:');
    console.log(conversations);
    for (const conversation of conversations) {
      await initializeConversation(conversation);
    }
    roomsLoaded.value = true;
  } catch (error: any) {
    console.error('Error fetching rooms:', error);
  }
}

async function sendMessage(message: Message) {
  console.log('Sending message:', message.content, ' to: ', message.roomId);

  const encryptedMessage: MessageDTO = await encryptMessage(message.content, message.roomId);

  console.log("Going to send:")
  console.log(encryptedMessage);
  
  const response = await api.post("/messages", encryptedMessage);
  const messageResponse: RawMessage = response.data;
  console.log('Message sent:', messageResponse);

  chatStore.addMessage(mapRawToMessage({...messageResponse, content: message.content}));
}









function menuActionHandler({ roomId, action }: { roomId: any, action: any }) {
  switch (action.name) {
    case 'disconnect':
      console.log('Disconnecting');
      disconnectUser();
    case 'showProfile':
      isProfileVisible.value = true;
      const room = chatStore.rooms.find((room) => room.roomId === roomId);
      selectedUser.value = room?.users[0] || emptyUser;
      break;
  }
}

function closeModal(event: boolean) {
  isProfileVisible.value = event;
  isRequestFormVisible.value = event;
  isRequestViewVisible.value = event;
}



register();



</script>

<template>
  <CustomModal
    v-model:isVisible="isProfileVisible"
    @update:isVisible="closeModal($event)"
  >
    <template #modal-content>
      <UserProfile :userProfile="selectedUser" />
    </template>
  </CustomModal>
  <CustomModal
    v-model:isVisible="isSelfProfileVisible"
    @update:isVisible="closeModal($event)"
  >
    <template #modal-content>
      <SelfProfile />
    </template>
  </CustomModal>
  <CustomModal
    v-model:isVisible="isRequestFormVisible"
    @update:isVisible="closeModal($event)"
  >
    <template #modal-content>
      <RequestForm />
    </template>
  </CustomModal>
  <CustomModal
    v-model:isVisible="isRequestViewVisible"
    @update:isVisible="closeModal($event)"
  >
    <template #modal-content>
      <RequestView />
    </template>
  </CustomModal>

  <vue-advanced-chat
    :theme="'dark'"
    :height="'100vh'"
    :current-user-id="authStore.user?.username"
    :rooms="JSON.stringify(chatStore.rooms)"
    :room-actions="JSON.stringify(roomActions)"
    :rooms-loaded="roomsLoaded"
    :messages-loaded="messagesLoaded"
    :messages="JSON.stringify(chatStore.messages[currentRoom])"
    @fetch-messages="fetchMessages($event.detail[0])"
    @send-message="sendMessage($event.detail[0])"
    @room-action-handler="menuActionHandler($event.detail[0])"
    @add-room="isRequestFormVisible = true"
  >
    <div class="rooms-header" slot="rooms-header">
      <button class="header-button profile-button" @click="isSelfProfileVisible = true">
        My Profile
      </button>
      <button class="header-button requests-button" @click="isRequestViewVisible = true">
        Show Requests
      </button>
    </div>
  </vue-advanced-chat>
</template>



<style scoped>
.rooms-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 16px;
  background-color: #1f1f1f;
  border-bottom: 1px solid #333;
  border-radius: 8px 8px 0 0;
}

.header-button {
  padding: 8px 12px;
  font-size: 14px;
  font-weight: 600;
  color: #f9f9f9;
  background-color: #2c2c2c;
  border: 1px solid #444;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.header-button:hover {
  background-color: #3a3a3a;
}

.profile-button {
  margin-right: 8px;
}

.requests-button {
  margin-left: 8px;
}
</style>
