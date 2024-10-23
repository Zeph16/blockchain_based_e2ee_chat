export interface User {
  _id: string;
  username: string;
  password?: string;
  avatar?: string;
  status: {
    state: 'online' | 'offline';
    lastChanged: string;
  }
}

export interface AuthState {
  user: User | null;
  accessToken: string | null;
  refreshToken: string | null;
  privateKey: string | null;
  prekeyBundle: {
    identityKey: CryptoKeyPair;
    signedPrekey: CryptoKeyPair;
  } | null;
}

export interface UsersState {
  users: Record<string, User>;
}

export interface UserDTO {
  id: string;
  username: string;
  state: 'ONLINE' | 'OFFLINE';
  lastChanged: string;
}
