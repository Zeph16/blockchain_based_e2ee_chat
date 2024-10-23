## Prerequisites

Ensure you have the following installed on your machine:

- [Node.js](https://nodejs.org/) (v18 or higher)
- [Ganache](https://www.trufflesuite.com/ganache) (GUI or CLI)
- [Truffle](https://www.trufflesuite.com/truffle) (v5 or higher)

## Setup

1. **Clone the Repository**

   Clone this repository to your local machine:

   ```bash
   git clone https://github.com/your-username/e2ee-chat.git
   cd e2ee-chat
   ```
2. **Start Ganache**

   Open Ganache GUI and create a new workspace or start Ganache CLI with the following command:

   ```bash
   ganache-cli -p 7545
   ```

   This will start a local blockchain on port 7545. Ensure it matches the port in the Truffle configuration.

3. **Compile Contracts**

   Compile your smart contracts:

   ```bash
   truffle compile
   ```

4. **Deploy Contracts**

   Deploy your contracts to the Ganache blockchain:

   ```bash
   truffle migrate
   ```

## Testing the Smart Contract with Truffle Console

1. **Open Truffle Console**

   Launch the Truffle console:

   ```bash
   truffle console
   ```

2. **Interact with the Contract**

   In the console, you can interact with your deployed contracts. For example:

   ```js
   // Get the deployed instance of PrekeyManagement
   const instance = await PrekeyManagement.deployed();

   // Call a function from the contract
   await instance.registerUser('user', { from: accounts[0]});
   await instance.storePrekey('a', 'a', 'a', { from: accounts[0] })
   const result = await instance.getPrekey('user');

   console.log(result.toString());
   ```
