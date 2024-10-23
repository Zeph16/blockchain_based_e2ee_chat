import type { AuthState, User } from '@/types';
import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    user: null,
    accessToken: null,
    refreshToken: null,
    privateKey: null,
    prekeyBundle: null,
  }),
  actions: {
    setUser(data: User) {
      this.user = data;
    },
    setAccessToken(data: string) {
      this.accessToken = data;
      // localStorage.setItem('accessToken', data);
    },
    setRefreshToken(data: string) {
      this.refreshToken = data;
      // localStorage.setItem('refreshToken', data);
    },
    setPrivateKey(data: string) {
      this.privateKey = data;
    },
    setPrekeyBundle(data: AuthState['prekeyBundle']) {
      this.prekeyBundle = data;
    },
    logout() {
      this.user = null;
      this.accessToken = null;
      this.refreshToken = null;
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
    }
  },
});
