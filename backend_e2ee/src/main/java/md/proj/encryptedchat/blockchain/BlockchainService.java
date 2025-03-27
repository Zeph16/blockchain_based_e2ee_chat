package md.proj.encryptedchat.blockchain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

@Service
public class BlockchainService {

    private final PrekeyManagement prekeyManagement;


    Logger log = LoggerFactory.getLogger(BlockchainService.class);


    public BlockchainService(@Value("${blockchain.url}") String blockchainUrl,
                             @Value("${blockchain.account.private-key}") String privateKey,
                             @Value("${blockchain.address.prekey-management}") String contractAddress) {

        log.debug("Before web3j");
        Web3j web3j = Web3j.build(new HttpService(blockchainUrl));
        log.debug("After web3j");

        log.debug("Before credentials");
        Credentials credentials = Credentials.create(privateKey);
        log.debug("After credentials");

        log.debug("Before contract");
        this.prekeyManagement = PrekeyManagement.load(contractAddress, web3j, credentials, new DefaultGasProvider());
        log.debug("After contract");
    }

    public boolean hasPrekeyBundle(String username) {
        try {
            return prekeyManagement.call_hasPrekeyBundle(username).send();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}