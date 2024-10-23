import { defineStore } from 'pinia';
import { useAuthStore } from './auth';
import { Client, type IFrame } from '@stomp/stompjs';
import type { MessageHandler, Subscription, WebSocketStoreState } from '@/types/websocket';
import { mapUserToDTO } from '@/utils/mappers';


export const useWebSocketStore = defineStore('webSocket', {
  state: (): WebSocketStoreState => ({
    wsClient: null,
    subscriptions: []
  }),
  actions: {
    setWebSocketClient(client: Client) {
      this.wsClient = client;
    },

    initializeWebsocket(initialSubscriptions: Subscription) {
      const authStore = useAuthStore();
      if (!authStore.user) {
        console.error('Access token is missing');
        return;
      }

      console.log("Initiailizing websocket with token " + authStore.accessToken);

      const wsOptions = {
        brokerURL: 'ws://localhost:8080/ws',
        connectHeaders: {
          "Authorization": `Bearer ${authStore.accessToken}`
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
      }

      const client = new Client(wsOptions);

      client.onConnect = (frame: IFrame) => {
        console.log('Connected to websocket: ' + frame);
        if (!authStore.user || !authStore.accessToken) {
          console.error('Access token is missing, from websocket onconnect...');
          return;
        }
        for (const path in initialSubscriptions) {
          this.subscribe(path, initialSubscriptions[path]);
        }
        this.connectUser();
      }

      client.onDisconnect = (frame: IFrame) => {
        console.log('Disconnected: ' + frame);
      }

      client.onStompError = (frame: IFrame) => {
        console.log('Broker reported error: ' + frame.headers.message);
        console.log('Additional details: ' + frame.body);
      }
      client.activate();

      this.setWebSocketClient(client);
    },
    subscribe(topic: string, callback: MessageHandler) {
      const authStore = useAuthStore();
      if (!this.wsClient || !this.wsClient.active) {
        console.error('WebSocket client is not initialized');
        return;
      }

      console.log("Trying to subscribe to " + topic + " with token " + authStore.accessToken);
      if (!this.wsClient.connected) {
        console.log('Websocket not connected yet')
        return;
      }
      try {

        const sub = this.wsClient.subscribe(topic, (message) => {
          const body = JSON.parse(message.body);
          callback(body);
        }, { Authorization: `Bearer ${authStore.accessToken}` });

        this.subscriptions.push(sub);
      } catch (e) {
        console.error('Failed to subscribe to ' + topic + ': ', e);
      }
    },

    send(destination: string, body: string) {
      const authStore = useAuthStore();
      if (!this.wsClient) {
        console.error('WebSocket client is not initialized');
        return;
      }
      console.log("Sending to " + destination + " with token " + authStore.accessToken);
      try {
        this.wsClient.publish({ destination, body, headers: {
          'Authorization': `Bearer ${authStore.accessToken}`
        }});
      } catch (e) {
        console.error('Failed to send message to ' + destination);
      }
    },

    connectUser() {
      const authStore = useAuthStore();
      console.log("Sending user to user.connectUser...")
      if (!authStore.user || !authStore.accessToken) {
        console.log('Access token is missing, from websocket onconnect...')
        return;
      }
      console.log(JSON.stringify(authStore.user))
      this.send("/app/user.connectUser", JSON.stringify(mapUserToDTO(authStore.user)))
    },

    disconnectUser() {
      const authStore = useAuthStore();
      console.log("Sending user to user.disconnectUser...")
      if (!authStore.user || !authStore.accessToken) {
        console.log('Access token is missing, from websocket ondisconnect...')
        return;
      }
      console.log(JSON.stringify(authStore.user))
      this.send("/app/user.disconnectUser", JSON.stringify(mapUserToDTO(authStore.user)))
    }
  },
});
