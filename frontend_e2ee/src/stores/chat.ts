import type { ChatState, Message, Room } from '@/types';
import { defineStore } from 'pinia';

export const useChatStore = defineStore('chat', {
  state: (): ChatState => ({
    rooms: [],
    activeRoom: null,
    messages: {},
  }),
  actions: {
    setActiveroom(roomId: string) {
      this.activeRoom = roomId;
    },
    addMessage(message: Message) {
      if (!this.messages[message.roomId]) {
        this.messages[message.roomId] = [];
      }
      if (this.messages[message.roomId].find(m => m._id === message._id)) return;
      this.messages[message.roomId] = [...this.messages[message.roomId], message];

      this.rooms = this.rooms.map((room) => {
        if (room.roomId === message.roomId) {
          room.lastMessage = message;
        }
        return room;
      });
    },
    addRoom(room: Room) {
      if (this.rooms.find(r => r.roomId === room.roomId)) return;
      if (!room.lastMessage) {
        room.lastMessage = {
          _id: '0',
          senderId: '0',
          username: '0',
          content: '_No messages yet_',
          roomId: room.roomId,
          date: '0',
          timestamp: '--:--',
          avatar: '0'
        }
      }
      this.rooms = [...this.rooms, room];
    },
    deleteMessage(roomId: string, messageId: string) {
      this.messages[roomId] = this.messages[roomId].filter(
        (message) => message._id !== messageId
      );
    },
    deleteroom(roomId: string) {
      this.rooms = this.rooms.filter(
        (room) => room.roomId !== roomId
      );
      delete this.messages[roomId];
    },
    updateMessage(message: Message) {
      if (this.messages[message.roomId]) {
        this.messages[message.roomId] = this.messages[message.roomId].map((m) => {
          if (message._id === m._id) {
            return message
          }
          return m;
        });
      }
    },
    updateRoom(room: Room) {
      this.rooms = this.rooms.map((r) => {
        if (room.roomId === r.roomId) {
          return { ...room, lastMessage: r.lastMessage };
        }
        return r;
      });
    },
  },

});
