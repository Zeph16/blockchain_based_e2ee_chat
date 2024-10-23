<script setup lang="ts">
import { useConversationStore } from '@/stores/conversation';
import api from '@/utils/api'
import { onConversation } from '@/utils/handlers';


const conversationStore = useConversationStore();

async function acceptConversationRequest(conversationId: string) {
  const conversation = conversationStore.pending[conversationId];
  
  try {
    const response = await api.post(`/conversations/accept/${conversation.id}`);
    const convoResponse = response.data;
    onConversation({
      state: "ACCEPTED", 
      conversationDTO: convoResponse
    })
  } catch (e: any) {
    console.error("Failed to accept conversation");
    console.log(e);
  }
}

async function rejectConversationRequest(conversationId: string) {
  const conversation = conversationStore.pending[conversationId];
  
  try {
    await api.post(`/conversations/reject/${conversation.id}`);
    onConversation({
      state: "REJECTED", 
      conversationDTO: conversation
    })
  } catch (e: any) {
    console.error("Failed to reject conversation");
    console.log(e);
  }
}






</script>



<template>
  <div class="request-view">
    <h2>Pending Conversation Requests</h2>
    <ul v-if="Object.keys(conversationStore.pending).length" class="request-list">
      <li v-for="conversationId in Object.keys(conversationStore.pending)" :key="conversationId" class="request-item">
        <span class="request-username">{{ conversationStore.pending[conversationId].user1.username }}</span>
        <div class="request-actions">
          <button @click="acceptConversationRequest(conversationId)" class="action-button accept">Accept</button>
          <button @click="rejectConversationRequest(conversationId)" class="action-button reject">Reject</button>
        </div>
      </li>
    </ul>
    <p v-else class="no-requests">No pending requests.</p>
  </div>
</template>



<style scoped>
.request-view {
  padding: 16px;
  border-radius: 8px;
  color: #e0e0e0;
}

h2 {
  margin-bottom: 16px;
  font-size: 1.5rem;
  color: #e0e0e0;
}

.request-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.request-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #555;
}

.request-username {
  font-weight: 600;
  color: #e0e0e0;
}

.request-actions {
  display: flex;
  gap: 8px;
}

.action-button {
  padding: 6px 12px;
  font-size: 14px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.action-button.accept {
  background-color: #4caf50;
  color: white;
}

.action-button.accept:hover {
  background-color: #388e3c;
}

.action-button.reject {
  background-color: #e53935;
  color: white;
}

.action-button.reject:hover {
  background-color: #d32f2f;
}

.no-requests {
  font-size: 1rem;
  color: #b0b0b0;
  text-align: center;
  margin-top: 16px;
}
</style>
