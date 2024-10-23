<script setup lang="ts">
import type { Conversation } from '@/types';
import api from'@/utils/api'
import { onConversation } from '@/utils/handlers';
import { ref } from 'vue';


const username = ref('');
const message = ref('');
const messageType = ref('');
const loading = ref(false);


async function handleRequest() {
  loading.value = true;
  message.value = '';
  messageType.value = '';

  try {
    await requestConversation(username.value);
    message.value = 'Request sent successfully!';
    messageType.value = 'success';
  } catch (error) {
    console.error("Error when requesting conversation:")
    console.log(error);
    message.value = 'Failed to send request. Please try again.';
    messageType.value = 'error';
  } finally {
    loading.value = false;
    username.value = '';
  }
}

async function requestConversation(username: string) {
  const response = await api.post(`/conversations/request/${username}`);
  console.log('The response of the conversation request is:');
  console.log(response.data);
  const conversation: Conversation = response.data;
  onConversation({
    state: "REQUESTING", 
    conversationDTO: conversation
  })
}





</script>


<template>
  <div class="request-form">
    <form @submit.prevent="handleRequest">
      <div class="form-group">
        <label for="username-input">Recipient Username</label>
        <input
          id="username-input"
          v-model="username"
          type="text"
          placeholder="Enter username"
          class="form-input"
          required
        />
      </div>
      <div class="form-actions">
        <button type="submit" class="form-button" :disabled="loading">Request Conversation</button>
      </div>
    </form>
    <div v-if="message" :class="`alert ${messageType}`">
      {{ message }}
    </div>
  </div>
</template>

<style scoped>
.request-form {
  width: 100%;
  max-width: 300px;
  margin: 0 auto;
  padding: 16px;
  border-radius: 8px;
  color: #e0e0e0;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #b0b0b0;
}

.form-input {
  width: 100%;
  padding: 8px;
  background-color: #3a3a3a;
  border: 1px solid #555;
  border-radius: 4px;
  font-size: 16px;
  color: #e0e0e0;
  transition: border-color 0.2s ease;
}

.form-input:focus {
  border-color: #777;
  outline: none;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
}

.form-button {
  padding: 8px 16px;
  background-color: #444;
  color: #e0e0e0;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.2s ease;
}

.form-button:disabled {
  background-color: #555;
  cursor: not-allowed;
}

.form-button:hover:enabled {
  background-color: #666;
}

.alert {
  margin-top: 16px;
  padding: 8px;
  border-radius: 4px;
  text-align: center;
}

.alert.success {
  background-color: #2e7d32;
  color: #e8f5e9;
}

.alert.error {
  background-color: #c62828;
  color: #ffebee;
}
</style>
