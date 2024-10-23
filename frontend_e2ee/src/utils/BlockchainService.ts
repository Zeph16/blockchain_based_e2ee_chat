import { useAuthStore } from '@/stores/auth';
import type { PrekeyBundle } from '@/types';
import type { Wallet } from 'ethers';
import { BrowserProvider, Contract, JsonRpcProvider, JsonRpcSigner, ethers } from 'ethers';
import { hexToBytes } from './utils';

export class BlockchainService {
  private static instance: BlockchainService;
  private provider: JsonRpcProvider | BrowserProvider | null = null;
  private signer: JsonRpcSigner | Wallet | null = null;
  private contract: Contract | null = null;
  private providerType: 'metamask' | 'local' = 'metamask';
  private privateKey: string = '';

  private constructor(providerType: 'metamask' | 'local', privateKey: string) {
    this.providerType = providerType
    this.privateKey = privateKey
  }

  public static async getInstance(providerType: 'metamask' | 'local', privateKey: string) {
    if (!BlockchainService.instance) {
      console.log("Creating service...")
      BlockchainService.instance = new BlockchainService(providerType, privateKey);
      await BlockchainService.instance.initialize();
    } else {
      console.log("Returning created instance...")
    }
    return BlockchainService.instance;
  }

  public async initialize(): Promise<void> {
    if (this.providerType === 'metamask') {
      if (window.ethereum) {
        this.provider = new ethers.BrowserProvider(window.ethereum);
        await this.provider.send('eth_requestAccounts', []);
        this.signer = await this.provider.getSigner();
      } else {
        throw new Error('MetaMask is not installed');
      }
    } else if (this.providerType === 'local') {
      this.provider = new ethers.JsonRpcProvider('http://localhost:8545');
      this.signer = new ethers.Wallet(
        this.privateKey,
        this.provider
    );
    } else {
      throw new Error('Invalid provider type');
    }

    const response = await fetch('http://localhost:5173/blockchain/PrekeyManagement.json');
    const contractData = await response.json();
    const { abi, networks: { 5777: { address } } } = contractData;

    this.contract = new ethers.Contract(address, abi, this.signer);
  }

  public async getProvider() {
    if (!this.provider) {
      await this.initialize();
    }
    if (!this.provider) {
      throw new Error("provider could not be initialized.");
    }
    return this.provider;
  }

  public async getSigner() {
    if (!this.signer) {
      await this.initialize();
    }
    if (!this.signer) {
      throw new Error("Signer could not be initialized.");
    }
    return this.signer;
  }

  public async getContract() {
    if (!this.contract) {
      await this.initialize();
    }
    if (!this.contract) {
      throw new Error("Contract could not be initialized.");
    }
    return this.contract;
  }

  public async registerUser(username: string) {
    const contract = await this.getContract();
    console.log(this.signer);
    const tx = await contract.registerUser(username, { gasLimit: 6721975 });
    await tx.wait();
  }

  public async storePrekey({
    identityKey,
    signedPrekey,
  }: PrekeyBundle) {
    const contract = await this.getContract();

    const user = useAuthStore().user;

    if (!user) throw new Error('User not authenticated');

    const userAddress = await contract.getUserAddress(user.username);
    if (userAddress === ethers.ZeroAddress) {
      throw new Error('Username not registered');
    }

    const tx = await contract.storePrekey(
      identityKey,
      signedPrekey,
      {
        gasLimit: 6721975
      }
    );
    await tx.wait();
    console.log(`Prekey bundle stored for ${user.username}`);
  }

  public async getPrekey(username: string): Promise<PrekeyBundle> {
    const contract = await this.getContract();
    const prekey  = await contract.getPrekey(username, { gasLimit: 6721975 });
    console.log("Fetched value from blockchain:")
    console.log(prekey);
    const [identityKey, signedPrekey] = prekey;
    return {
      identityKey: hexToBytes(identityKey),
      signedPrekey: hexToBytes(signedPrekey)
    };
  }

  public async hasPrekeyBundle(username: string): Promise<boolean> {
    const contract = await this.getContract();
    return await contract.hasPrekeyBundle(username, { gasLimit: 6721975 });
  }
}
