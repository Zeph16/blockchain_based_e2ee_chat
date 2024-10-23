const PrekeyManagement = artifacts.require("PrekeyManagement");

contract('PrekeyManagement', (accounts) => {
  let instance;

  beforeEach(async () => {
    instance = await PrekeyManagement.deployed();
  });

  it('should register a user', async () => {
    await instance.registerUser("testuser", { from: accounts[0] });
    const userAddress = await instance.getUserAddress("testuser");
    assert.equal(userAddress, accounts[0], "User address mismatch");
  });

  it('should store and retrieve prekey bundle', async () => {
    const identityKey = "0x1234567890abcdef"
    const signedPrekey = "0xabcdef1234567890"
    const prekeySignature = "0x7890abcdef123456"
    await instance.storePrekey(
      identityKey,
      signedPrekey,
      prekeySignature,
      { from: accounts[0] }
    );

    const prekey = await instance.getPrekey("testuser");
    assert.equal(prekey.identityKey, identityKey, "Identity key mismatch");
    assert.equal(prekey.signedPrekey, signedPrekey, "0xabcdef1234567890", "Signed prekey mismatch");
    assert.equal(prekey.prekeySignature, prekeySignature, "0x7890abcdef123456", "Prekey signature mismatch");
  });
});

