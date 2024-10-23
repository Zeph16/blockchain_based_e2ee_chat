<template>
  <div class="wrapper">
    <div class="background">
      <div class="shape"></div>
      <div class="shape"></div>
    </div>
    <form @submit.prevent="signup">
      <img :src="logo" alt="">
      <h3>Join the community</h3>

      <label for="username">Username</label>
      <input type="text" placeholder="Username" id="username" v-model="username">

      <div class="passwords">
        <div class="password">
          <label for="password">Password</label>
          <input type="password" placeholder="Password" id="password" v-model="password">
        </div>

        <div class="confirmpassword">
          <label for="confirmpassword">Confirm Password</label>
          <input type="password" placeholder="Confirm Password" id="confirmpassword" v-model="confirmpassword">
        </div>
      </div>

      <label for="accounts">Select wallet</label>
      <select id="accounts" v-model="selectedPrivateKey">
        <option v-for="(privateKey, index) in privateKeyList" :value="privateKey">Wallet {{ index + 1 }}</option>
      </select>

      <button>Signup</button>
      <h4>Or <router-link to="/login">Login</router-link> if you already have an account</h4>
    </form>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/auth';
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import logo from '@/assets/logo2.png';
import api from '@/utils/api'



// const privateKeyList = [
//   "0x3b18fe24d8910b524fd35d20aefbe9fefe341f63fa96fb4dff93e31ffcf77a89",
//   "0x18b5fbe84a69a1ba2c2749bda2a14baed21c6cc5504d3ce6b50bb02eb0baa32a",
//   "0x0e01f76b1714510d52a4fd7cb7843ba4e0e6deadb2e948c2e92142e12836b4bb",
//   "0x25741940576f84a8941146869e19e4757cf73c4e68dc74574b4d3a10acb21234",
//   "0x6a5c335822980e75c48b60c00c0fa80cbb9797f0fd2492eb544cb6abc65cbf61"
// 

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


const router = useRouter();
const username = ref<string>('');
const password = ref<string>('');
const confirmpassword = ref<string>('');

async function signup() {
  console.log("Signing up...")
  if (!username || !password) {
      console.log('No username or password')
      return;
  }

  if (password.value.length < 8) {
    console.log('Password too weak')
    return;
  }

  if (password.value !== confirmpassword.value) {
    console.log('Password and confirm password not the same')
    return;
  }

  try {
    const response = await api.post('/auth/register', { 
      username: username.value, 
      password: password.value 
    });
    console.log('The response of the signup is:');
    console.log(response.data);

    const authStore = useAuthStore();

    authStore.setUser(response.data)
    authStore.setPrivateKey(selectedPrivateKey.value);

    router.push('/registration')
  } catch (error) {
    console.log('Signup failed!');
    console.log(error)
  }
}

onMounted(fetchPrivateKeys);




</script>

<style scoped>
.background{
    width: 630px;
    height: 700px;
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
    left: -120px;
    top: -20px;
}
.shape:last-child{
    background: linear-gradient(
        to right,
        #ff512f,
        #f09819
    );
    right: -80px;
    bottom: -20px;
}
form{
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
  display: flex;
  flex-direction: column;
  justify-content: center;
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
    width: 70%;
    background-color: #ffffff;
    color: #080710;
    padding: 15px 0;
    font-size: 18px;
    font-weight: 600;
    border-radius: 5px;
    cursor: pointer;
    align-self: center;
}
button:hover {
  background-color: #e2e2e2;
  color: #282720;
}
img {
  width: 200px;
  height: 200px;
  margin-bottom: 20px;
  align-self: center;
}
::placeholder{
  color: #e5e5e5;
}

div.passwords {
  display: flex;
  gap: 2em;
}
div.password, div.confirmpassword {
  display: flex;
  flex-direction: column;
}
select {
  background-color: black;
  margin-top: 0.5em;
  font-size: 1.2em;
  padding: 0.5em;
  overflow-wrap: break-word;
  word-break: break-all;
}
h4 {
  text-align: center;
  margin-top: 1em;
  color: #a0a0a0;
}
</style>
