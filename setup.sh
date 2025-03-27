#!/bin/bash

GANACHE_PORT=8545
GANACHE_NETWORK_ID=5777
GANACHE_URL="http://127.0.0.1:$GANACHE_PORT"
CONTRACTS_DIR="contracts_e2ee"
BACKEND_DIR="backend_e2ee"
FRONTEND_DIR="frontend_e2ee"
CONTRACT_JSON_FILE="PrekeyManagement.json"
CONTRACT_CLASS_NAME="PrekeyManagement"
ACCOUNTS_FILE="accounts.json"
KEYS_FILE="keys.json"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

log_info() {
    echo -e "${GREEN}[INFO] $1${NC}"
}

log_warn() {
    echo -e "${YELLOW}[WARNING] $1${NC}"
}

log_error() {
    echo -e "${RED}[ERROR] $1${NC}"
}

cleanup() {
    log_warn "Cleaning up..."
    kill $GANACHE_PID $BACKEND_PID $FRONTEND_PID 2>/dev/null
    wait $GANACHE_PID $BACKEND_PID $FRONTEND_PID 2>/dev/null
    log_info "All processes have been stopped."
    exit 0
}

trap cleanup SIGINT

log_info "Starting Ganache on port $GANACHE_PORT..."
ganache -p $GANACHE_PORT -i $GANACHE_NETWORK_ID -a 10 --account_keys_path $ACCOUNTS_FILE > blockchain.log &
GANACHE_PID=$!
sleep 5

if ! kill -0 $GANACHE_PID >/dev/null 2>&1; then
    log_error "Failed to start Ganache. Please check if the port $GANACHE_PORT is available."
    exit 1
fi

log_info "Ganache started successfully with PID $GANACHE_PID."

log_info "Extracting private keys..."
jq -r '{ private_keys: .private_keys | to_entries | map({ (.key): .value }) }' $ACCOUNTS_FILE > $KEYS_FILE
if [ $? -ne 0 ]; then
    log_error "Failed to extract private keys."
    kill $GANACHE_PID
    exit 1
fi

cd $CONTRACTS_DIR
log_info "Compiling and deploying contracts..."
truffle migrate --network development
if [ $? -ne 0 ]; then
    log_error "Truffle migration failed."
    kill $GANACHE_PID
    exit 1
fi

CONTRACT_ADDRESS=$(truffle networks | grep "$CONTRACT_CLASS_NAME:" | tail -1 | awk '{print $2}')
if [ -z "$CONTRACT_ADDRESS" ]; then
    log_error "Failed to get the contract address."
    kill $GANACHE_PID
    exit 1
fi
log_info "Contract deployed at address: $CONTRACT_ADDRESS"

FIRST_PRIVATE_KEY=$(jq -r '.private_keys[0] | to_entries | .[0].value' ../$KEYS_FILE)

log_info "Generating Java wrappers with Web3j..."
web3j generate truffle --truffle-json=build/contracts/$CONTRACT_JSON_FILE -o ../$BACKEND_DIR/src/main/java/ -p md.proj.encryptedchat.blockchain
if [ $? -ne 0 ]; then
    log_error "Web3j code generation failed."
    kill $GANACHE_PID
    exit 1
fi

cd ../$BACKEND_DIR
APPLICATION_PROPERTIES="src/main/resources/application.properties"
log_info "Updating application.properties..."
sed -i "s/^blockchain.address.prekey-management=.*/blockchain.address.prekey-management=$CONTRACT_ADDRESS/" $APPLICATION_PROPERTIES
sed -i "s/^blockchain.account.private-key=.*/blockchain.account.private-key=$FIRST_PRIVATE_KEY/" $APPLICATION_PROPERTIES

log_info "Starting the Spring Boot backend..."
./mvnw spring-boot:run > ../backend.log 2>&1 &
BACKEND_PID=$!
sleep 5

if ! kill -0 $BACKEND_PID >/dev/null 2>&1; then
    log_error "Failed to start the backend application. Check backend.log for details."
    kill $GANACHE_PID
    exit 1
fi

cd ..
log_info "Copying compiled contract JSON to the frontend..."
mkdir -p $FRONTEND_DIR/public/blockchain
cp $CONTRACTS_DIR/build/contracts/$CONTRACT_JSON_FILE $FRONTEND_DIR/public/blockchain/
cp $KEYS_FILE $FRONTEND_DIR/public/blockchain/

cd $FRONTEND_DIR
log_info "Starting the frontend application..."
npm install
npm run dev > ../frontend.log 2>&1 &
FRONTEND_PID=$!
sleep 5

if ! kill -0 $FRONTEND_PID >/dev/null 2>&1; then
    log_error "Failed to start the frontend application. Check frontend.log for details."
    kill $GANACHE_PID $BACKEND_PID
    exit 1
fi

log_info "Setup complete."
log_info "Ganache is running on $GANACHE_URL."
log_info "Backend application is running on http://localhost:8080."
log_info "Frontend application is running on http://localhost:5173."
log_info "Log files are available at backend.log and frontend.log."

log_warn "Press [CTRL+C] to stop all processes..."
wait $GANACHE_PID $BACKEND_PID $FRONTEND_PID
