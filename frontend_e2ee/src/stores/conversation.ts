import type { Conversation, ConversationsState } from '@/types';
import { defineStore } from 'pinia';

export const useConversationStore = defineStore('conversationStore', {
  state: (): ConversationsState => ({
    active: {},
    pending: {}
  }),
  actions: {
    addActive(conversation: Conversation) {
      this.active[conversation.id] = conversation;
    },
    removeActive(conversationId: string) {
      delete this.active[conversationId];
    },
    getActive(conversationId: string): Conversation | undefined {
      return this.active[conversationId];
    },
    updateActive(conversationId: string, conversationData: Partial<Conversation>) {
      this.active[conversationId] = { ...this.active[conversationId], ...conversationData };
    },
    addPending(conversation: Conversation) {
      this.pending[conversation.id] = conversation;
    },
    removePending(conversationId: string) {
      delete this.pending[conversationId];
    },
    getPending(conversationId: string): Conversation | undefined {
      return this.pending[conversationId];
    },
    updatePending(conversationId: string, conversationData: Partial<Conversation>) {
      this.pending[conversationId] = { ...this.pending[conversationId], ...conversationData };
    },
    activate(conversationId: string) {
      const c = this.getPending(conversationId);
      if (!c) return;
      this.addActive({...c});
      this.removePending(c.id);
    }
  },
  getters: {
    getActiveById: (state) => {
      return (conversationId: string) => state.active[conversationId];
    },
    allActive: (state) => {
      return Object.values(state.active);
    },
    getPendingById: (state) => {
      return (conversationId: string) => state.pending[conversationId];
    },
    allPending: (state) => {
      return Object.values(state.pending);
    },
  },
});
