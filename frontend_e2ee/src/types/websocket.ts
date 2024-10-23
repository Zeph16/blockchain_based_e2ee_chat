import type { Client, StompSubscription } from "@stomp/stompjs";

export type MessageHandler = (message: any) => void;

export type Subscription = {
  [path: string]: MessageHandler;
};

export interface WebSocketStoreState {
  wsClient: Client | null;
  subscriptions: StompSubscription[]
}

