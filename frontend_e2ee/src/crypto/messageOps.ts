import { useAuthStore } from "@/stores/auth";
import { useCryptoStore } from "@/stores/crypto";
import type { MessageDTO, RawMessage } from "@/types";
import CryptoService from "./CryptoService";
import { base64ToBytes, bytesToBase64, hexToBytes } from "@/utils/utils";

export async function encryptMessage(message: string, receiver: string): Promise<MessageDTO> {
  const cryptoStore = useCryptoStore();
  const authStore = useAuthStore();
  const receiverBundle = cryptoStore.prekeyBundles[receiver];
  console.log("Encrypting message for", receiver, "by", authStore.user!.username, "with prekeys:")
  console.log(receiverBundle);
  if (!receiverBundle) throw new Error('Receiver public key not found');
  if (!authStore.prekeyBundle || !authStore.user) throw new Error('Logged in user or prekey bundle not found');
  let session = cryptoStore.getSession(receiver);
  let initial = false;
  if (!session) {
    session = await CryptoService.initializeEncryptionAsSender(authStore.prekeyBundle, receiverBundle);
    cryptoStore.setSession({ username: receiver, session });
    initial = true;
  }

  const ad = CryptoService.createAD(new Uint8Array(await CryptoService.exportKey(authStore.prekeyBundle.identityKey.publicKey)), receiverBundle.identityKey);
  const [header, cipherText] = await session.RatchetEncrypt(new TextEncoder().encode(message), ad, initial);

  const encryptedPayload = bytesToBase64(cipherText);

  return {
    senderUsername: authStore.user.username,
    recipientUsername: receiver,
    content: encryptedPayload,
    ...header,
  };
}






export async function decryptMessage(rawMessage: RawMessage): Promise<RawMessage> {
  const cryptoStore = useCryptoStore();
  const authStore = useAuthStore();
  const senderUsername = rawMessage.sender.username;
  const senderBundle = cryptoStore.prekeyBundles[senderUsername];
  if (!senderBundle) throw new Error('Sender public key not found');
  if (!authStore.prekeyBundle || !authStore.user) throw new Error('Logged in user or prekey bundle not found');
  let session = cryptoStore.getSession(senderUsername);
  if (!session) {
    session = await CryptoService.initializeEncryptionAsReceiver(senderBundle, authStore.prekeyBundle, hexToBytes(rawMessage.dh));
    cryptoStore.setSession({ username: senderUsername, session });
  }
  const ad = CryptoService.createAD(senderBundle.identityKey, new Uint8Array(await CryptoService.exportKey(authStore.prekeyBundle.identityKey.publicKey)));
  const decryptedPayload = await session.RatchetDecrypt({
    dh: await CryptoService.importCryptoKey(hexToBytes(rawMessage.dh)),
    pn: rawMessage.pn,
    n: rawMessage.n,
      }, base64ToBytes(rawMessage.content), ad);
  return {
    ...rawMessage,
    content: new TextDecoder().decode(decryptedPayload),
  };
}
