import type { User, UsersState } from '@/types';
import { defineStore } from 'pinia';

export const useUserStore = defineStore('userStore', {
  state: (): UsersState => ({
    users: {}
  }),
  actions: {
    addUser(user: User) {
      this.users[user._id] = user;
    },
    removeUser(userId: string) {
      delete this.users[userId];
    },
    getUser(userId: string): User | undefined {
      return this.users[userId];
    },
    updateUser(userId: string, userData: Partial<User>) {
      this.users[userId] = { ...this.users[userId], ...userData };
    },
  },
  getters: {
    getUserById: (state) => {
      return (userId: string) => state.users[userId];
    },
    allUsers: (state) => {
      return Object.values(state.users);
    },
  },
});
