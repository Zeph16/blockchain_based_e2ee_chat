Installation Manual blockchain_based_e2ee_chat

1. Open Ganache and Create a New Workspace
• Launch Ganache on your machine.
• Create a new workspace and configure it to connect with your blockchain project.
• In your project directory, open the truffle-config.js file. Ensure it is set up to connect to the 
Ganache network, specifying the correct host, port, and network ID.
2. Open and Connect with the PostgreSQL Database Server
• Start the PostgreSQL database server on your machine.
• Connect to your PostgreSQL database using a preferred database management tool pgAdmin 
3. Compile and Migrate the Smart Contracts
• In your project directory, run the following commands:
truffle compile
truffle migrate
• This will compile your smart contracts and deploy them to the blockchain network configured in 
Ganache.
4. Replace the PrekeyManagement.json File
• Locate the PrekeyManagement.json file in the build folder of your blockchain project.
• Replace the existing PrekeyManagement.json file in the client/public/abi directory with the 
newly generated one.
5. Copy Ganache Private Keys into SignupVue.vue
• In Ganache, locate the private keys for the accounts generated.
• Copy one of the private keys and paste it into the SignupVue.vue file under the client directory 
where the private key is required.
6. Copy Contract Address and Private Key into Spring Boot Application
• Copy the smart contract address from the deployment process.
• Open the application.properties file in your Spring Boot project.
• Paste the smart contract address and a private key into the appropriate properties in the 
application.properties file.
7. Run the Client Application
• Navigate to the client directory and run the following command:
npm run dev
• This will start the Vue.js client application.
8. Run the Spring Boot Application
• In your Spring Boot project directory, run:
./mvnw spring-boot:run
• This will start the Spring Boot backend application.
9. Test the Login Functionality
• Open the client application in your browser.
• Perform the login process. The login should succeed if all configurations are correctly set up
