// SPDX-License-Identifier: MIT
pragma solidity >=0.4.22 <0.9.0;
pragma experimental ABIEncoderV2;

contract PrekeyManagement {
    address private owner;

    constructor() public {
        owner = msg.sender;
    }

    modifier onlyOwner() {
        require(msg.sender == owner, "Not authorized");
        _;
    }

    mapping(string => address) private userAddresses;
    mapping(address => bool) private registeredAddresses;
    string[] private usernames;
    address[] private addresses;

    struct PrekeyBundle {
        bytes identityKey;
        bytes signedPrekey;
    }

    mapping(address => PrekeyBundle) private prekeys;

    event UserRegistered(string username, address userAddress);
    event PrekeyStored(address userAddress, PrekeyBundle prekeyBundle);

    function registerUser(string memory username) public {
        require(userAddresses[username] == address(0), "Username already taken");
        require(!registeredAddresses[msg.sender], "Address already registered");

        userAddresses[username] = msg.sender;
        registeredAddresses[msg.sender] = true;
        usernames.push(username);
        addresses.push(msg.sender);

        emit UserRegistered(username, msg.sender);
    }

    function getUserAddress(string memory username) public view returns (address) {
        return userAddresses[username];
    }

    function storePrekey(
        bytes memory identityKey,
        bytes memory signedPrekey
    ) public {
        require(registeredAddresses[msg.sender], "Address not registered");

        PrekeyBundle memory prekeyBundle = PrekeyBundle({
            identityKey: identityKey,
            signedPrekey: signedPrekey
        });

        prekeys[msg.sender] = prekeyBundle;
        emit PrekeyStored(msg.sender, prekeyBundle);
    }

    function getPrekey(string calldata username) external view returns (PrekeyBundle memory) {
        address userAddress = userAddresses[username];
        require(userAddress != address(0), "Username not registered");

        return prekeys[userAddress];
    }

    function hasPrekeyBundle(string calldata username) external view returns (bool) {
        address userAddress = userAddresses[username];
        if (userAddress == address(0)) {
            return false;
        }

        PrekeyBundle memory prekeyBundle = prekeys[userAddress];
        return prekeyBundle.identityKey.length > 0 && 
               prekeyBundle.signedPrekey.length > 0;
    }

    function resetContract() external onlyOwner {
        for (uint i = 0; i < usernames.length; i++) {
            address userAddress = userAddresses[usernames[i]];
            delete userAddresses[usernames[i]];
            delete registeredAddresses[userAddress];
            delete prekeys[userAddress];
        }
        delete usernames;
        delete addresses;
    }
}
