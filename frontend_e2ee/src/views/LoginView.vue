<template>
  <div class="wrapper">
    <div class="background">
      <div class="shape"></div>
      <div class="shape"></div>
    </div>
    <form @submit.prevent="login">
      <h3>Welcome back!</h3>

      <label for="username">Username</label>
      <input type="text" placeholder="Username" id="username" v-model="username">

      <label for="password">Password</label>
      <input type="password" placeholder="Password" id="password" v-model="password">

      <label for="accounts">Select wallet</label>
      <select id="accounts" v-model="selectedPrivateKey">
        <option v-for="(privateKey, index) in privateKeyList" :value="privateKey">Wallet {{ index + 1 }}</option>
      </select>

      <button>Login</button>
      <h4>Or <router-link to="/signup">Signup</router-link> if you don't have an account</h4>
    </form>
  </div>
</template>



<script setup lang="ts">
import { useAuthStore } from '@/stores/auth';
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import api from '@/utils/api'


const privateKeyList = ref([] as string[]);

const fetchPrivateKeys = async () => {
  try {

    const response = await fetch('http://localhost:5173/blockchain/keys.json');
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }

    const data = await response.json();
    privateKeyList.value = data.private_keys.map((item: any) => {
      const address = Object.keys(item)[0];
      const privateKey = item[address];
      return `${privateKey}`;
    });
    console.log("Private keys fetched:");
    console.log(privateKeyList.value);
  } catch (error) {
    console.error('Error fetching the keys:', error);
  }
};

const selectedPrivateKey = ref<string>(privateKeyList.value[0]);



onMounted(() => {
  fetchPrivateKeys().then(() => {
    selectedPrivateKey.value = privateKeyList.value[0];
  })

  if (localStorage.getItem('jwt')) {
    router.push('/');
  }
})

const router = useRouter();

const username = ref<string>('');
const password = ref<string>('');

async function login() {
  console.log("Logging in...")
  if (!username || !password) {
      console.log('No username or password')
      return;
  }

  try {
    const response = await api.post('/auth/authenticate', { 
      username: username.value, 
      password: password.value 
    });
    console.log('The response of the login is:');
    console.log(response.data);

    const authStore = useAuthStore();

    authStore.setUser({
      _id: response.data.user.id,
      username: response.data.user.username,
      password: password.value,
      status: {
        state: response.data.user.state,
        lastChanged: response.data.user.lastChanged
      }
    })

    console.log('Setting access token and refresh token: ' + response.data.accessToken + ' ' + response.data.refreshToken);

    authStore.setAccessToken(response.data.access_token);
    authStore.setRefreshToken(response.data.refresh_token);

    authStore.setPrivateKey(selectedPrivateKey.value);

    router.push('/')
  } catch (error: any) {
    
    if (error.response.data.type === "PREKEY_NOT_PUBLISHED") {
      console.log('Prekey not published');
      router.push('/registration');
      return;
    }


    console.log('Login failed!');
    console.log(error)
  }
}

</script>
























<style scoped>
.background{
    width: 530px;
    height: 600px;
    position: absolute;
    transform: translate(-50%,-50%);
    left: 50%;
    top: 50%;
}
.background .shape{
    height: 200px;
    width: 200px;
    position: absolute;
    border-radius: 50%;
}
.shape:first-child{
    background: linear-gradient(
        #1845ad,
        #23a2f6
    );
    left: -80px;
    top: -80px;
}
.shape:last-child{
    background: linear-gradient(
        to right,
        #ff512f,
        #f09819
    );
    right: -30px;
    bottom: -80px;
}
form{
    width: 500px;
    height: 600px;
    background-color: rgba(255,255,255,0.13);
    position: absolute;
    transform: translate(-50%,-50%);
    top: 50%;
    left: 50%;
    border-radius: 10px;
    backdrop-filter: blur(10px);
    border: 2px solid rgba(255,255,255,0.1);
    box-shadow: 0 0 40px rgba(8,7,16,0.6);
    padding: 50px 35px;
}

form *{
    font-family: 'Poppins',sans-serif;
    color: #ffffff;
    letter-spacing: 0.5px;
    outline: none;
    border: none;
}
form h3{
    font-size: 32px;
    font-weight: 500;
    line-height: 42px;
    text-align: center;
}

label{
    display: block;
    margin-top: 30px;
    font-size: 16px;
    font-weight: 500;
}
input{
    display: block;
    height: 50px;
    width: 100%;
    background-color: rgba(255,255,255,0.07);
    border-radius: 3px;
    padding: 0 10px;
    margin-top: 8px;
    font-size: 14px;
    font-weight: 300;
}
button{
    margin-top: 50px;
    width: 100%;
    background-color: #ffffff;
    color: #080710;
    padding: 15px 0;
    font-size: 18px;
    font-weight: 600;
    border-radius: 5px;
    cursor: pointer;
}
::placeholder{
  color: #e5e5e5;
}

select {
  background-color: black;
  margin-top: 0.5em;
  font-size: 1.2em;
  padding: 0.5em;
  width: 100%;
}
h4 {
  text-align: center;
  margin-top: 1em;
  color: #a0a0a0;
}
</style>
