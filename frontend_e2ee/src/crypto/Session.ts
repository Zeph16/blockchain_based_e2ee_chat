import { arrayBufferToHex } from "@/utils/utils";
import CryptoService from "./CryptoService";

export default class Session {
  DHs: CryptoKeyPair | null;
  DHr: CryptoKey | null;
  RK: Uint8Array;
  CKs: Uint8Array | null;
  CKr: Uint8Array | null;
  Ns: number;
  Nr: number;
  PN: number;
  MKSKIPPED: Map<string, Uint8Array>;
  MAX_SKIP: number = 20;

  private constructor() {
    this.DHs = null;
    this.DHr = null;
    this.RK = new Uint8Array(32);
    this.CKs = null;
    this.CKr = null;
    this.Ns = 0;
    this.Nr = 0;
    this.PN = 0;
    this.MKSKIPPED = new Map();
  }

  static async initializeSender(x3dh: Uint8Array, receiverPublicPrekey: CryptoKey, initialDHs: CryptoKeyPair): Promise<Session> {
    const instance = new Session();
    instance.DHs = initialDHs;
    instance.DHr = receiverPublicPrekey;
    const [RK, CKs] = await CryptoService.KDF_RK(x3dh, await CryptoService.DH(instance.DHs, instance.DHr));
    instance.RK = RK;
    instance.CKs = CKs;
    instance.CKr = null;
    instance.Ns = 0;
    instance.Nr = 0;
    instance.PN = 0;
    instance.MKSKIPPED = new Map();
    return instance;
  }

  static async initializeReceiver(x3dh: Uint8Array, dhs: CryptoKeyPair, dhr: CryptoKey): Promise<Session> {
    const instance = new Session();
    instance.DHs = dhs;
    instance.DHr = dhr;
    const [RK, CKr] = await CryptoService.KDF_RK(x3dh, await CryptoService.DH(instance.DHs, instance.DHr));
    instance.RK = RK;
    instance.CKs = null;
    instance.CKr = CKr;
    instance.Ns = 0;
    instance.Nr = 0;
    instance.PN = 0;
    instance.MKSKIPPED = new Map();
    return instance;
  }

  async RatchetEncrypt(plaintext: Uint8Array, AD: Uint8Array, initial: boolean): Promise<[{ dh: string, pn: number, n: number }, Uint8Array]> {
    console.log('Ratchet state before encrypting:', await this.toString());
    if (this.Ns === 0 && !initial) {
      this.DHs = await CryptoService.GENERATE_DH();
      const [newRK, newCKs] = await CryptoService.KDF_RK(this.RK, await CryptoService.DH(this.DHs, this.DHr!));
      this.RK = newRK;
      this.CKs = newCKs;
    }

    if (!this.CKs) throw new Error('Chain key (CKs) is not initialized');
    if (!this.DHs) throw new Error('DHs is not initialized');
    const [newCKs, mk] = await CryptoService.KDF_CK(this.CKs);
    this.CKs = newCKs;
    const header = CryptoService.HEADER(this.DHs, this.PN, this.Ns);
    this.Ns += 1;
    const encryptedData = await CryptoService.ENCRYPT(mk, plaintext, await CryptoService.CONCAT(AD, header));
    console.log('Ratchet state after encrypting:', await this.toString());
    return [{
      ...header,
      dh: arrayBufferToHex(await CryptoService.exportKey(header.dh))
    }, encryptedData];
  }

  private async TrySkippedMessageKeys(header: { dh: CryptoKey, pn: number, n: number }, ciphertext: Uint8Array, AD: Uint8Array): Promise<Uint8Array | null> {
    const key = this.MKSKIPPED.get(await this.generateKeyForMKSKIPPED(header.dh, header.n));
    if (key) {
      console.log("Skipped message key found")
      this.MKSKIPPED.delete(`${header.dh}-${header.n}`);
      return CryptoService.DECRYPT(key, ciphertext, await CryptoService.CONCAT(AD, header));
    } else {
      console.log("No skipped message keys found")
      return null;
    }
  }

  private async SkipMessageKeys(until: number): Promise<void> {
    if (this.Nr + this.MAX_SKIP < until) {
      throw new Error('Skipping too far ahead');
    }
    if (this.CKr) {
      while (this.Nr < until) {
        const [newCKr, mk] = await CryptoService.KDF_CK(this.CKr);
        this.MKSKIPPED.set(await this.generateKeyForMKSKIPPED(this.DHr!, this.Nr), mk);
        this.CKr = newCKr;
        this.Nr += 1;
      }
    }
  }

  private async DHRatchet(header: { dh: CryptoKey }): Promise<void> {
    this.PN = this.Ns;
    this.Ns = 0;
    this.Nr = 0;
    this.DHr = header.dh;

    const [newRK, newCKr] = await CryptoService.KDF_RK(this.RK, await CryptoService.DH(this.DHs!, this.DHr));
    this.RK = newRK;
    this.CKr = newCKr;
  }

  async RatchetDecrypt(header: { dh: CryptoKey, pn: number, n: number }, ciphertext: Uint8Array, AD: Uint8Array): Promise<Uint8Array> {
    console.log('Ratchet state before decrypting:', await this.toString());
    let plaintext = await this.TrySkippedMessageKeys(header, ciphertext, AD);
    if (plaintext) {
        return plaintext;
    }

    if (!(await CryptoService.areEqual(header.dh, this.DHr!))) {
      console.log("Skipping message keys...")
      await this.SkipMessageKeys(header.pn);
      await this.DHRatchet(header);
      await this.SkipMessageKeys(header.n);
    }

    if (!this.CKr) {
        throw new Error('Chain key (CKr) is not initialized');
    }

    const [newCKr, mk] = await CryptoService.KDF_CK(this.CKr);
    this.CKr = newCKr;
    this.Nr += 1;
    console.log('Ratchet state after decrypting:', await this.toString());

    return await CryptoService.DECRYPT(mk, ciphertext, await CryptoService.CONCAT(AD, header));
  }

  async toString(): Promise<string> {
    return `
      DHs: ${this.DHs ? 'Present' : 'Not Set'}
      DHr: ${this.DHr ? arrayBufferToHex(await CryptoService.exportKey(this.DHr)) : 'Not Set'}
      RK: ${this.RK ? Array.from(this.RK).join(', ') : 'Not Set'}
      CKs: ${this.CKs ? Array.from(this.CKs).join(', ') : 'Not Set'}
      CKr: ${this.CKr ? Array.from(this.CKr).join(', ') : 'Not Set'}
      Ns: ${this.Ns}
      Nr: ${this.Nr}
      PN: ${this.PN}
      MKSKIPPED: ${JSON.stringify(this.MKSKIPPED, null, 2)}
    `.trim();
  }

  private async generateKeyForMKSKIPPED(dh: CryptoKey, n: number): Promise<string> {
    const exportedDh = await crypto.subtle.exportKey('raw', dh);
    const dhString = Array.from(new Uint8Array(exportedDh)).map(byte => byte.toString(16).padStart(2, '0')).join('');
    return `${dhString}-${n}`;
  }
}
