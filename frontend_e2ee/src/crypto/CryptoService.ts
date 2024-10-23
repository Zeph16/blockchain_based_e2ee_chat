import type { AuthState, PrekeyBundle } from '@/types';
import Session from './Session'
import { arrayBufferToHex, compareUint8Arrays } from '@/utils/utils';
export default class CryptoService {
  
  static async initializeEncryptionAsSender(senderBundle: AuthState['prekeyBundle'], receiverBundle: PrekeyBundle): Promise<Session> {
    // SELF PREKEY BUNDLE SHOULD ALREADY BE STORED
    const ephemeralKey = await this.GENERATE_DH();
    const SK = await this.performX3DHSender(senderBundle!.identityKey, ephemeralKey, receiverBundle.identityKey, receiverBundle.signedPrekey);
    const receiverPublicPrekey = await crypto.subtle.importKey(
      "raw",
      receiverBundle.signedPrekey,
      { name: "ECDH", namedCurve: "P-256" },
      true,
      []
    );
    const session = Session.initializeSender(SK, receiverPublicPrekey, ephemeralKey);

    return session;
  }

  static async initializeEncryptionAsReceiver(senderBundle: PrekeyBundle, receiverBundle: AuthState['prekeyBundle'], ekb_bytes: Uint8Array): Promise<Session> {
    const SK = await this.performX3DHReceiver(receiverBundle!.identityKey, receiverBundle!.signedPrekey, senderBundle.identityKey, ekb_bytes);
    const ekb = await crypto.subtle.importKey(
      "raw",
      ekb_bytes,
      { name: "ECDH", namedCurve: "P-256" },
      true,
      []
    );
    const session = Session.initializeReceiver(SK, receiverBundle!.signedPrekey, ekb);

    return session;
  }

  static async performX3DHSender(
    ika: CryptoKeyPair,
    eka: CryptoKeyPair,
    ikb_bytes: Uint8Array,
    spkb_bytes: Uint8Array
  ): Promise<Uint8Array> {
    try {
      const ikb = await crypto.subtle.importKey(
        "raw",
        ikb_bytes,
        { name: "ECDH", namedCurve: "P-256" },
        false,
        []
      );
      const spkb = await crypto.subtle.importKey(
        "raw",
        spkb_bytes,
        { name: "ECDH", namedCurve: "P-256" },
        false,
        []
      );

      const DH1 = await this.DH(ika, spkb);
      const DH2 = await this.DH(eka, ikb);
      const DH3 = await this.DH(eka, spkb);

      const combinedSecretBytes = new Uint8Array([
        ...DH1,
        ...DH2,
        ...DH3
      ]);

      const combinedSecretHash = await crypto.subtle.digest("SHA-256", combinedSecretBytes);
      const finalSecret = new Uint8Array(combinedSecretHash);

      return finalSecret;
    } catch (error) {
      console.error("Error in X3DH key agreement:", error);
      throw new Error("Failed to perform X3DH key agreement");
    }
  }

  static async performX3DHReceiver(
    ika: CryptoKeyPair,
    spka: CryptoKeyPair,
    ikb_bytes: Uint8Array,
    ekb_bytes: Uint8Array
  ): Promise<Uint8Array> {
    try {
      const ikb = await crypto.subtle.importKey(
        "raw",
        ikb_bytes,
        { name: "ECDH", namedCurve: "P-256" },
        false,
        []
      );
      const ekb = await crypto.subtle.importKey(
        "raw",
        ekb_bytes,
        { name: "ECDH", namedCurve: "P-256" },
        false,
        []
      );

      const DH1 = await this.DH(spka, ikb);
      const DH2 = await this.DH(ika, ekb);
      const DH3 = await this.DH(spka, ekb);

      const combinedSecretBytes = new Uint8Array([
        ...DH1,
        ...DH2,
        ...DH3
      ]);

      const combinedSecretHash = await crypto.subtle.digest("SHA-256", combinedSecretBytes);
      const finalSecret = new Uint8Array(combinedSecretHash);

      return finalSecret;
    } catch (error) {
      console.error("Error in X3DH key agreement:", error);
      throw new Error("Failed to perform X3DH key agreement");
    }
  }


  static async GENERATE_DH(): Promise<CryptoKeyPair> {
    const keyPair = await crypto.subtle.generateKey(
      {
        name: 'ECDH',
        namedCurve: 'P-256'
      },
      true,
      ['deriveKey', 'deriveBits']
    );
    return keyPair;
  }

  static async convertToECDSASigning(dh: CryptoKeyPair): Promise<CryptoKey> {
    const exportedPrivateKey = await crypto.subtle.exportKey(
      "jwk",
      dh.privateKey
    );
    console.log("Converting key")
    const ecdsaPrivateKey = await crypto.subtle.importKey(
      "jwk",
      {
        ...exportedPrivateKey,
        key_ops: ["sign", "verify"],
        alg: "ES256",
      },
      {
        name: "ECDSA",
        namedCurve: "P-256",
      },
      false,
      ["sign"]
    );
    console.log("Converted key")

    return ecdsaPrivateKey;
  }
  static async verifySignature(
    publicKeyBytes: Uint8Array,
    data: Uint8Array,
    signature: Uint8Array
  ): Promise<boolean> {
    try {
      const publicKey = await crypto.subtle.importKey(
        "raw",
        publicKeyBytes,
        { name: "ECDSA", namedCurve: "P-256" },
        false,
        ["verify"]
      );

      const isValid = await crypto.subtle.verify(
        { name: "ECDSA", hash: { name: "SHA-256" } },
        publicKey,
        signature,
        data
      );
      console.log("Signature verification result:", isValid);

      return isValid;
    } catch (error) {
      console.error("Signature verification failed:", error);
      return false;
    }
  }

  static async sign(key: CryptoKey, data: Uint8Array): Promise<Uint8Array> {
    const signature = await crypto.subtle.sign(
      { name: "ECDSA", hash: "SHA-256" },
      key,
      data
    );
    return new Uint8Array(signature);
  }


  static async DH(dh_pair: CryptoKeyPair, dh_pub: CryptoKey): Promise<Uint8Array> {
    const dhSecret = await crypto.subtle.deriveBits(
      {
        name: 'ECDH',
        public: dh_pub
      },
      dh_pair.privateKey,
      256
    );
    console.log('dhSecret:', arrayBufferToHex(dhSecret));
    return new Uint8Array(dhSecret);
  }

  static async importCryptoKey(keyBytes: Uint8Array): Promise<CryptoKey> {
    return crypto.subtle.importKey(
      "raw",
      keyBytes.buffer,
      { name: "ECDH", namedCurve: "P-256" },
      true,
      ["deriveKey", "deriveBits"]
    );
  }
  
  static async exportCryptoKey(key: CryptoKey): Promise<Uint8Array> {
    const exported = await crypto.subtle.exportKey("raw", key);
    return new Uint8Array(exported);
  }

  static async signData(privateKey: CryptoKey, data: Uint8Array): Promise<Uint8Array> {
    const signature = await crypto.subtle.sign(
      {
        name: "ECDSA",
        hash: { name: "SHA-256" }
      },
      privateKey,
      data
    );

    return new Uint8Array(signature);
  }

  static async CONCAT(ad: Uint8Array, header: { dh: CryptoKey, pn: number, n: number }): Promise<Uint8Array> {
    // Export CryptoKey to raw format (ArrayBuffer)
    const dhBytes = new Uint8Array(await crypto.subtle.exportKey('raw', header.dh));

    // Convert numbers pn and n to Uint8Array
    const pnBytes = new Uint8Array(new Uint32Array([header.pn]).buffer);
    const nBytes = new Uint8Array(new Uint32Array([header.n]).buffer);

    // Combine all parts
    const headerBytes = new Uint8Array([...dhBytes, ...pnBytes, ...nBytes]);
    return new Uint8Array([...ad, ...headerBytes]);
  }

  static async exportKey(key: CryptoKey): Promise<ArrayBuffer> {
    return await window.crypto.subtle.exportKey('raw', key);
  }

  static async logKeyPair(key: CryptoKey): Promise<void> {
    const keyBuffer = await this.exportKey(key);

    const keyHex = arrayBufferToHex(keyBuffer);

    console.log(keyHex);
  }







  static async KDF_RK(rk: Uint8Array, dh_out: Uint8Array): Promise<[Uint8Array, Uint8Array]> {
    // HKDF for key derivation
    const hkdf = async (salt: Uint8Array, ikm: Uint8Array, info: Uint8Array) => {
      const ikm_cryptokey = await crypto.subtle.importKey(
        'raw',
        ikm,
        { name: 'HKDF', hash: 'SHA-256' },
        false,
        ['deriveBits', 'deriveKey']
      );


      console.log("After importing ikm:", ikm_cryptokey)
      // Derive 64 bytes (32 bytes for rk and 32 bytes for ck) from PRK
      const derivedBits = await crypto.subtle.deriveBits(
        {
          name: 'HKDF',
          hash: 'SHA-256',
          salt: salt,
          info: info
        },
        ikm_cryptokey,
        64 * 8 // 64 bytes * 8 bits/byte
      );

      console.log("Derived bits:", new Uint8Array(derivedBits));
      return new Uint8Array(derivedBits);
    };

    const prk = await hkdf(rk, dh_out, new Uint8Array([1]));
    const rk_new = prk.slice(0, 32);
    const ck_new = prk.slice(32, 64);
    console.log("RK new:", rk_new)
    console.log("CK new:", ck_new)
    return [rk_new, ck_new];
  }

  static async KDF_CK(ck: Uint8Array): Promise<[Uint8Array, Uint8Array]> {
    const hmac = async (key: Uint8Array, data: Uint8Array) => {
      const keyObj = await crypto.subtle.importKey(
        'raw',
        key,
        { name: 'HMAC', hash: 'SHA-256' },
        false,
        ['sign', 'verify']
      );
      const signature = await crypto.subtle.sign('HMAC', keyObj, data);
      return new Uint8Array(signature);
    };

    const messageKey = await hmac(ck, new Uint8Array([0x01]));
    const chainKey = await hmac(ck, new Uint8Array([0x02]));
    return [chainKey, messageKey];
  }

  static async ENCRYPT(mk: Uint8Array, plaintext: Uint8Array, associated_data: Uint8Array): Promise<Uint8Array> {
    const hkdf = async (salt: Uint8Array, ikm: Uint8Array): Promise<[Uint8Array, Uint8Array, Uint8Array]> => {
      const prk = await crypto.subtle.importKey(
        'raw',
        ikm,
        { name: 'HKDF', hash: 'SHA-256' },
        false,
        ['deriveBits']
      );
      const derivedKey = await crypto.subtle.deriveBits(
        {
          name: 'HKDF',
          hash: 'SHA-256',
          salt: salt,
          info: new Uint8Array([0x03])
        },
        prk,
        80 * 8
      );
      console.log("Derived Key:")
      console.log(derivedKey);

      const key = new Uint8Array(derivedKey.slice(0, 32));
      const authKey = new Uint8Array(derivedKey.slice(32, 64));
      const iv = new Uint8Array(derivedKey.slice(64, 80));
      return [key, authKey, iv];
    };

    const [key, authKey, iv] = await hkdf(new Uint8Array(32), mk);

    console.log("Key:");
    console.log(key);
    console.log("Auth Key:");
    console.log(authKey);
    console.log("IV:");
    console.log(iv);

    const cryptoKey = await crypto.subtle.importKey(
      'raw',
      key,
      { name: 'AES-CBC' },
      false,
      ['encrypt']
    );

    const cipherText = await crypto.subtle.encrypt(
      { name: 'AES-CBC', iv: iv },
      cryptoKey,
      plaintext
    );

    console.log("Cipher Text:");
    console.log(new Uint8Array(cipherText));

    const hmacKey = await crypto.subtle.importKey(
      'raw',
      authKey,
      { name: 'HMAC', hash: 'SHA-256' },
      false,
      ['sign']
    );

    const hmac = await crypto.subtle.sign(
      'HMAC',
      hmacKey,
      new Uint8Array([...associated_data, ...new Uint8Array(cipherText)])
    );

    console.log("HMAC:");
    console.log(new Uint8Array(hmac));

    const encryptedData = new Uint8Array([...new Uint8Array(cipherText), ...new Uint8Array(hmac)]);

    console.log("Encrypted Data:");
    console.log(encryptedData);

    return encryptedData;
  }




  static async DECRYPT(mk: Uint8Array, encryptedData: Uint8Array, associated_data: Uint8Array): Promise<Uint8Array> {
    const cipherText = encryptedData.slice(0, -32);
    const hmac = encryptedData.slice(-32);

    console.log("Received ciphertext:");
    console.log(cipherText);
    console.log("Received hmac:")
    console.log(hmac);

    const hkdf = async (salt: Uint8Array, ikm: Uint8Array): Promise<[Uint8Array, Uint8Array, Uint8Array]> => {
      const prk = await crypto.subtle.importKey(
        'raw',
        ikm,
        { name: 'HKDF', hash: 'SHA-256' },
        false,
        ['deriveBits']
      );
      const derivedKey = await crypto.subtle.deriveBits(
        {
          name: 'HKDF',
          hash: 'SHA-256',
          salt: salt,
          info: new Uint8Array([0x03])
        },
        prk,
        80 * 8
      );

      const key = new Uint8Array(derivedKey.slice(0, 32));
      const authKey = new Uint8Array(derivedKey.slice(32, 64));
      const iv = new Uint8Array(derivedKey.slice(64, 80));
      return [key, authKey, iv];
    };

    const [key, authKey, iv] = await hkdf(new Uint8Array(32), mk);
    console.log("Key:");
    console.log(key);
    console.log("Auth Key:");
    console.log(authKey);
    console.log("IV:");
    console.log(iv);

    const cryptoKey = await crypto.subtle.importKey(
      'raw',
      key,
      { name: 'AES-CBC' },
      false,
      ['decrypt']
    );

    const plaintext = await crypto.subtle.decrypt(
      { name: 'AES-CBC', iv: iv },
      cryptoKey,
      cipherText
    );
    console.log("Plaintext in Uint8Array:")
    console.log(new Uint8Array(plaintext));
    console.log("Plaintext in string:")
    console.log(arrayBufferToHex(plaintext));

    const hmacKey = await crypto.subtle.importKey(
      'raw',
      authKey,
      { name: 'HMAC', hash: 'SHA-256' },
      false,
      ['sign', 'verify']
    );

    const isValid = await crypto.subtle.verify(
      'HMAC',
      hmacKey,
      hmac,
      new Uint8Array([...associated_data, ...cipherText])
    );

    if (!isValid) throw new Error('Authentication failed');

    return new Uint8Array(plaintext);
  }

  static HEADER(dh_pair: CryptoKeyPair, pn: number, n: number): { dh: CryptoKey, pn: number, n: number } {
    return {
      dh: dh_pair.publicKey,
      pn: pn,
      n: n
    };
  }

  static createAD(ika: Uint8Array, ikb: Uint8Array) {
    const ad = new Uint8Array(ika.length + ikb.length);
    ad.set(ika, 0);
    ad.set(ikb, ika.length);
    return ad;
  }
  static async areEqual(key1: CryptoKey, key2: CryptoKey): Promise<boolean> {
      const rawKey1 = await this.exportKey(key1);
      const rawKey2 = await this.exportKey(key2);

      return compareUint8Arrays(new Uint8Array(rawKey1), new Uint8Array(rawKey2));
  }

}
