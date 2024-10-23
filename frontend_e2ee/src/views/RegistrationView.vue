<template>
  <div class="wrapper">
    <div class="container">
      <h2 v-if="steps.registerUser.completed && steps.generatePrekeyBundle.completed && steps.publishPrekeyBundle.completed">Registration complete! You can now <RouterLink to="/login">login</RouterLink></h2>
      <h2 v-else>Please wait while we set up your account...</h2>
      <ul class="steps">
        <li @click="registerUser">
          <span v-if="steps.registerUser.completed" class="checkmark">&#10003;</span>
          <div v-if="!steps.registerUser.completed" class="loading-icon"></div>
          Registering user on blockchain...
        </li>
        <li @click="generatePrekeyBundle">
          <span v-if="steps.generatePrekeyBundle.completed" class="checkmark">&#10003;</span>
          <div v-if="!steps.generatePrekeyBundle.completed" class="loading-icon"></div>
          Generating prekey bundle...
        </li>
        <li @click="publishPrekeyBundle">
          <span v-if="steps.publishPrekeyBundle.completed" class="checkmark">&#10003;</span>
          <div v-if="!steps.publishPrekeyBundle.completed" class="loading-icon"></div>
          Publishing prekey bundle...
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import CryptoService from '@/crypto/CryptoService';
import { useAuthStore } from '@/stores/auth';
import { BlockchainService } from '@/utils/BlockchainService';
import { onMounted, reactive } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const steps = reactive({
  registerUser: { completed: false, error: null as string | null },
  generatePrekeyBundle: { completed: false, error: null as string | null },
  publishPrekeyBundle: { completed: false, error: null as string | null },
});


async function performRegistration() {
  await registerUser();
  await generatePrekeyBundle();
  await publishPrekeyBundle();
  console.log('Registration completed!');
}

onMounted(() => {
  performRegistration();
});







async function registerUser() {
  const authStore = useAuthStore();
  if (!authStore.user || !authStore.privateKey) {
    router.push('/login');
    console.log("User not found on register");
    return;
  }
  const blockchain = await BlockchainService.getInstance('local', authStore.privateKey);
  console.log("The User:")
  console.log(authStore.user);
  try {
    await blockchain.registerUser(authStore.user.username);
    console.log("Successfully registered ", authStore.user.username)
    steps.registerUser.completed = true;
  } catch (error: any) {
    console.error("Error registering user: ", error)
    steps.registerUser.error = error;
  }
}

async function generatePrekeyBundle() {
  const authStore = useAuthStore();
  const identityKey = await CryptoService.GENERATE_DH();
  const signedPrekey = await CryptoService.GENERATE_DH();

  if (!authStore.user) {
    router.push('/login');
    console.log("User not found on generate");
    return;
  }

  authStore.setPrekeyBundle({
    identityKey,
    signedPrekey,
  });
  steps.generatePrekeyBundle.completed = true;
  console.log('Successfully generated prekey bundle:');
  console.log(authStore.prekeyBundle);
}

async function publishPrekeyBundle() {
  const authStore = useAuthStore();
  if (!authStore.user || !authStore.privateKey) {
    router.push('/login');
    console.log("User not found on publish");
    return;
  }

  if (!authStore.prekeyBundle) {
    console.error("Prekey bundle not found");
    return;
  }
  const blockchain = await BlockchainService.getInstance('local', authStore.privateKey);
  try {
    await blockchain.storePrekey({
      identityKey: await CryptoService.exportCryptoKey(authStore.prekeyBundle.identityKey.publicKey),
      signedPrekey: await CryptoService.exportCryptoKey(authStore.prekeyBundle.signedPrekey.publicKey)
    });
    console.log("Successfully stored prekey");
    steps.publishPrekeyBundle.completed = true;
  } catch (error: any) {
    console.error("Error storing prekey: ", error)
    steps.publishPrekeyBundle.error = error;
  }
}
</script>

<style scoped>
h2 {
  font-size: 2.5rem;
}

.container {
  color: #ffffff;
  background-color: rgba(255,255,255,0.13);
  padding: 50px 100px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 30px;
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255,255,255,0.1);
  box-shadow: 0 0 40px rgba(8,7,16,0.6);
  border-radius: 10px;
  font-size: 1.5rem;
}

.steps {
  list-style-type: none;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;
  gap: 20px;
  padding: 0;
}

.steps li {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  letter-spacing: 0.5px;
}

.checkmark {
  color: green;
  font-size: 24px;
  margin-right: 10px;
}

.loading-icon {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  animation: spin 1s linear infinite;
  margin-right: 10px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
