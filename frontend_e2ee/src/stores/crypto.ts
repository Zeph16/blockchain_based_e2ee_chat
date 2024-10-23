import type { CryptoState, PrekeyBundle } from '@/types';
import Session from '@/crypto/Session'
import { defineStore } from 'pinia';

export const useCryptoStore = defineStore('crypto', {
  state: (): CryptoState => ({
    prekeyBundles: {},
    sessions: {}
  }),
  actions: {
    setPrekeyBundle(payload: { username: string; bundle: PrekeyBundle }) {
      this.prekeyBundles[payload.username] = payload.bundle
    },
    removePrekeyBundle(username: string) {
      delete this.prekeyBundles[username]
    },
    setSession(payload: { username: string; session: Session }) {
      this.sessions[payload.username] = payload.session;
    },
    removeSession(username: string) {
      delete this.sessions[username];
    }
  },
  getters: {
    getPrekeyBundle: (state) => {
      return (username: string) => state.prekeyBundles[username];
    },
    getSession: (state) => {
      return (username: string) => state.sessions[username];
    },
  }
});
