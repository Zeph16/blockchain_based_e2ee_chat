<template>
  <div class="profile">
    <img src="http://localhost:5173/user_dark.png" alt="User Avatar" class="avatar" />
    <h2 class="username">{{ authStore.user!.username }}</h2>
    <h3 class="key-title">Public Key:</h3>
    <p class="key">{{ identityKey }}</p>
    <span class="key-message">Make sure other users also see this public key on their device!</span>
  </div>
</template>

<script lang="ts" setup>
import CryptoService from '@/crypto/CryptoService';
import { useAuthStore } from '@/stores/auth';
import { arrayBufferToHex } from '@/utils/utils';
import { ref, onMounted } from 'vue';
const authStore = useAuthStore();

const identityKey = ref<string | null>(null);
onMounted(async () => {
  console.log("User profile:", authStore.user);
  console.log('Prekey bundles:', authStore.prekeyBundle);
  identityKey.value = arrayBufferToHex(await CryptoService.exportKey(authStore.prekeyBundle!.identityKey.publicKey));
});
</script>

<style scoped>
.profile {
  display: flex;
  flex-direction: column;
  align-items: center;
  border-radius: 8px;
  max-width: 100%;
  color: #f0f0f0;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  border: 2px solid #555;
  margin-bottom: 15px;
}

.username {
  font-size: 1.8rem;
  font-weight: 600;
  margin-bottom: 10px;
  color: #ffffff;
}

.key-title {
  font-size: 1.2rem;
  font-weight: 500;
  color: #a0a0a0;
  margin-bottom: 5px;
}

.key {
  background-color: #2e2e2e;
  padding: 10px;
  border-radius: 4px;
  font-family: 'Courier New', Courier, monospace;
  word-break: break-all;
  text-align: center;
  font-size: 0.95rem;
  color: #ffffff;
  max-width: 100%;
  overflow-wrap: break-word;
}

.key-message {
  font-size: 0.9rem;
  color: #b0b0b0;
  margin-top: 10px;
  text-align: center;
  max-width: 320px;
}
</style>
