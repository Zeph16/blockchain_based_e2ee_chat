import Session from '@/crypto/Session'
export interface CryptoState {
  prekeyBundles: { [conversationId: string]: PrekeyBundle };
  sessions: { [username: string]: Session };
}

export interface PrekeyBundle {
  identityKey: Uint8Array;
  signedPrekey: Uint8Array;
  prekeySignature: Uint8Array;
}

