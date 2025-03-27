# End-to-End Encrypted Chat Application

## Overview

This project is an end-to-end encrypted (E2EE) chat application implementing the Signal Protocol for secure messaging. The backend is built with Spring Boot, the frontend uses Vue.js, and Ethereum smart contracts (via a simple blockchain setup in Ganache) are used for key management.

> 🚨 **Note:** This is not a production-ready implementation. It is meant for research and learning purposes only. Don't do cryptography in the browser.

## Architecture

- **Frontend**: Vue.js application for user interaction
    
- **Backend**: Spring Boot REST API
    
- **Blockchain**: Ethereum smart contracts deployed on Ganache for storing encryption keys
    
- **Encryption**: Custom implementation of the Signal Protocol in the frontend for message encryption.
    

## Setup Instructions

### Prerequisites

Ensure you have the following installed:

- Node.js & npm
    
- Java 17+ & Maven
    
- Truffle & Ganache
    
- Web3j CLI
    
- jq (for JSON parsing in shell scripts)
    
- PostgreSQL Database


    

### Running the Project

1. **Clone the repository:**
    
    ```bash
    git clone https://github.com/Zeph16/blockchain_based_e2ee_chat
    cd blockchain_based_e2ee_chat
    ```
    
2. Configure spring boot's database connection configs in the `backend_e2ee/src/main/resources/application.properties` file to properly connect to your own database.
    
3. **Run the startup script:**
    
    ```bash
    ./startup.sh
    ```
    
    This will:
    
    - Start a local Ethereum blockchain (Ganache)
        
    - Deploy smart contracts
        
    - Start the Spring Boot backend
        
    - Start the Vue frontend
    
4. Pay close attention to each of the 3 log files found in the root of the directory. The startup script output by itself will not show indication of failure of the individual components.

## Blockchain Integration

The smart contract `PrekeyManagement` is used to store and manage encryption keys securely.

- **Deployment**: Contracts are compiled and migrated using Truffle.
    
- **Interaction**: Web3j is used to generate Java wrappers for blockchain communication.
    

## Backend

The Spring Boot backend serves as an API layer, providing authentication and encrypted message exchange. It only interacts with the blockchain to **check for prekey existence** before registering a user. The STOMP protocol is used for real time websocket interactions. 

## Frontend

The Vue.js frontend:

- Registers and authenticates users
    
- Exchanges encrypted messages

- Retrieves public keys from the blockchain

- Handles all cryptographic operations

> It contains a custom implementation of the Signal Protocol using the Web Crypto API, a stripped down version can be found at my signal protocol implementation repository [here](https://github.com/Zeph16/signal-webcrypto-ts).
    

## Logging and Debugging

- **Backend logs:** `backend.log`
    
- **Frontend logs:** `frontend.log`
    
- **Blockchain logs:** `blockchain.log`
    

## Stopping the Application

Press `CTRL+C` to stop all services. The startup script automatically cleans up running processes.

## Future Improvements

- Implement group messaging
    
- Optimize blockchain interactions
    
- Enhance the user interface and UX
    

## License

This project is released under the MIT License.
