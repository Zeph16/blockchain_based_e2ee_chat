package md.proj.encryptedchat.blockchain;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/main/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.6.0.
 */
@SuppressWarnings("rawtypes")
public class PrekeyManagement extends Contract {
    public static final String BINARY = "0x608060405234801561001057600080fd5b50336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550611cc0806100606000396000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c80634985e85c14610067578063704f1b94146100975780637b87f89a146100b35780638808f392146100e3578063b181a8fc146100ff578063d79e1cc714610109575b600080fd5b610081600480360381019061007c9190611004565b610139565b60405161008e919061108e565b60405180910390f35b6100b160048036038101906100ac9190611004565b610181565b005b6100cd60048036038101906100c89190611109565b610444565b6040516100da9190611171565b60405180910390f35b6100fd60048036038101906100f8919061122d565b610702565b005b610107610871565b005b610123600480360381019061011e9190611109565b610ac1565b60405161013091906113b1565b60405180910390f35b600060018260405161014b919061141a565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050919050565b600073ffffffffffffffffffffffffffffffffffffffff166001826040516101a9919061141a565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff161461022e576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016102259061148e565b60405180910390fd5b600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16156102bb576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016102b2906114fa565b60405180910390fd5b336001826040516102cc919061141a565b908152602001604051809103902060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506001600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055506003819080600181540180825580915050600190039060005260206000200160009091909190915090816103a49190611730565b506004339080600181540180825580915050600190039060005260206000200160009091909190916101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055507f7f605d0ff47d2a070e456b69ce2a36041788910b64443c7f4c6d307993cade2c813360405161043992919061183b565b60405180910390a150565b60008060018484604051610459929190611890565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff16036104c85760009150506106fc565b6000600560008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060405180606001604052908160008201805461052490611549565b80601f016020809104026020016040519081016040528092919081815260200182805461055090611549565b801561059d5780601f106105725761010080835404028352916020019161059d565b820191906000526020600020905b81548152906001019060200180831161058057829003601f168201915b505050505081526020016001820180546105b690611549565b80601f01602080910402602001604051908101604052809291908181526020018280546105e290611549565b801561062f5780601f106106045761010080835404028352916020019161062f565b820191906000526020600020905b81548152906001019060200180831161061257829003601f168201915b5050505050815260200160028201805461064890611549565b80601f016020809104026020016040519081016040528092919081815260200182805461067490611549565b80156106c15780601f10610696576101008083540402835291602001916106c1565b820191906000526020600020905b8154815290600101906020018083116106a457829003601f168201915b505050505081525050905060008160000151511180156106e657506000816020015151115b80156106f757506000816040015151115b925050505b92915050565b600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff1661078e576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610785906118f5565b60405180910390fd5b6000604051806060016040528085815260200184815260200183815250905080600560003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008201518160000190816108029190611970565b5060208201518160010190816108189190611970565b50604082015181600201908161082e9190611970565b509050507fc9c582e44cc4d2afc337c8b5cee77af4d1d51f4f67369cebee75d394a8ae55d93382604051610863929190611a42565b60405180910390a150505050565b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16146108ff576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016108f690611abe565b60405180910390fd5b60005b600380549050811015610aa257600060016003838154811061092757610926611ade565b5b9060005260206000200160405161093e9190611b90565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905060016003838154811061098557610984611ade565b5b9060005260206000200160405161099c9190611b90565b908152602001604051809103902060006101000a81549073ffffffffffffffffffffffffffffffffffffffff0219169055600260008273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81549060ff0219169055600560008273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008082016000610a6c9190610d86565b600182016000610a7c9190610d86565b600282016000610a8c9190610d86565b5050508080610a9a90611bd6565b915050610902565b5060036000610ab19190610dc6565b60046000610abf9190610de7565b565b610ac9610e08565b600060018484604051610add929190611890565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff1603610b7d576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610b7490611c6a565b60405180910390fd5b600560008273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020604051806060016040529081600082018054610bd790611549565b80601f0160208091040260200160405190810160405280929190818152602001828054610c0390611549565b8015610c505780601f10610c2557610100808354040283529160200191610c50565b820191906000526020600020905b815481529060010190602001808311610c3357829003601f168201915b50505050508152602001600182018054610c6990611549565b80601f0160208091040260200160405190810160405280929190818152602001828054610c9590611549565b8015610ce25780601f10610cb757610100808354040283529160200191610ce2565b820191906000526020600020905b815481529060010190602001808311610cc557829003601f168201915b50505050508152602001600282018054610cfb90611549565b80601f0160208091040260200160405190810160405280929190818152602001828054610d2790611549565b8015610d745780601f10610d4957610100808354040283529160200191610d74565b820191906000526020600020905b815481529060010190602001808311610d5757829003601f168201915b50505050508152505091505092915050565b508054610d9290611549565b6000825580601f10610da45750610dc3565b601f016020900490600052602060002090810190610dc29190610e29565b5b50565b5080546000825590600052602060002090810190610de49190610e46565b50565b5080546000825590600052602060002090810190610e059190610e29565b50565b60405180606001604052806060815260200160608152602001606081525090565b5b80821115610e42576000816000905550600101610e2a565b5090565b5b80821115610e665760008181610e5d9190610e6a565b50600101610e47565b5090565b508054610e7690611549565b6000825580601f10610e885750610ea7565b601f016020900490600052602060002090810190610ea69190610e29565b5b50565b6000604051905090565b600080fd5b600080fd5b600080fd5b600080fd5b6000601f19601f8301169050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b610f1182610ec8565b810181811067ffffffffffffffff82111715610f3057610f2f610ed9565b5b80604052505050565b6000610f43610eaa565b9050610f4f8282610f08565b919050565b600067ffffffffffffffff821115610f6f57610f6e610ed9565b5b610f7882610ec8565b9050602081019050919050565b82818337600083830152505050565b6000610fa7610fa284610f54565b610f39565b905082815260208101848484011115610fc357610fc2610ec3565b5b610fce848285610f85565b509392505050565b600082601f830112610feb57610fea610ebe565b5b8135610ffb848260208601610f94565b91505092915050565b60006020828403121561101a57611019610eb4565b5b600082013567ffffffffffffffff81111561103857611037610eb9565b5b61104484828501610fd6565b91505092915050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b60006110788261104d565b9050919050565b6110888161106d565b82525050565b60006020820190506110a3600083018461107f565b92915050565b600080fd5b600080fd5b60008083601f8401126110c9576110c8610ebe565b5b8235905067ffffffffffffffff8111156110e6576110e56110a9565b5b602083019150836001820283011115611102576111016110ae565b5b9250929050565b600080602083850312156111205761111f610eb4565b5b600083013567ffffffffffffffff81111561113e5761113d610eb9565b5b61114a858286016110b3565b92509250509250929050565b60008115159050919050565b61116b81611156565b82525050565b60006020820190506111866000830184611162565b92915050565b600067ffffffffffffffff8211156111a7576111a6610ed9565b5b6111b082610ec8565b9050602081019050919050565b60006111d06111cb8461118c565b610f39565b9050828152602081018484840111156111ec576111eb610ec3565b5b6111f7848285610f85565b509392505050565b600082601f83011261121457611213610ebe565b5b81356112248482602086016111bd565b91505092915050565b60008060006060848603121561124657611245610eb4565b5b600084013567ffffffffffffffff81111561126457611263610eb9565b5b611270868287016111ff565b935050602084013567ffffffffffffffff81111561129157611290610eb9565b5b61129d868287016111ff565b925050604084013567ffffffffffffffff8111156112be576112bd610eb9565b5b6112ca868287016111ff565b9150509250925092565b600081519050919050565b600082825260208201905092915050565b60005b8381101561130e5780820151818401526020810190506112f3565b60008484015250505050565b6000611325826112d4565b61132f81856112df565b935061133f8185602086016112f0565b61134881610ec8565b840191505092915050565b60006060830160008301518482036000860152611370828261131a565b9150506020830151848203602086015261138a828261131a565b915050604083015184820360408601526113a4828261131a565b9150508091505092915050565b600060208201905081810360008301526113cb8184611353565b905092915050565b600081519050919050565b600081905092915050565b60006113f4826113d3565b6113fe81856113de565b935061140e8185602086016112f0565b80840191505092915050565b600061142682846113e9565b915081905092915050565b600082825260208201905092915050565b7f557365726e616d6520616c72656164792074616b656e00000000000000000000600082015250565b6000611478601683611431565b915061148382611442565b602082019050919050565b600060208201905081810360008301526114a78161146b565b9050919050565b7f4164647265737320616c72656164792072656769737465726564000000000000600082015250565b60006114e4601a83611431565b91506114ef826114ae565b602082019050919050565b60006020820190508181036000830152611513816114d7565b9050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b6000600282049050600182168061156157607f821691505b6020821081036115745761157361151a565b5b50919050565b60008190508160005260206000209050919050565b60006020601f8301049050919050565b600082821b905092915050565b6000600883026115dc7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff8261159f565b6115e6868361159f565b95508019841693508086168417925050509392505050565b6000819050919050565b6000819050919050565b600061162d611628611623846115fe565b611608565b6115fe565b9050919050565b6000819050919050565b61164783611612565b61165b61165382611634565b8484546115ac565b825550505050565b600090565b611670611663565b61167b81848461163e565b505050565b5b8181101561169f57611694600082611668565b600181019050611681565b5050565b601f8211156116e4576116b58161157a565b6116be8461158f565b810160208510156116cd578190505b6116e16116d98561158f565b830182611680565b50505b505050565b600082821c905092915050565b6000611707600019846008026116e9565b1980831691505092915050565b600061172083836116f6565b9150826002028217905092915050565b611739826113d3565b67ffffffffffffffff81111561175257611751610ed9565b5b61175c8254611549565b6117678282856116a3565b600060209050601f83116001811461179a5760008415611788578287015190505b6117928582611714565b8655506117fa565b601f1984166117a88661157a565b60005b828110156117d0578489015182556001820191506020850194506020810190506117ab565b868310156117ed57848901516117e9601f8916826116f6565b8355505b6001600288020188555050505b505050505050565b600061180d826113d3565b6118178185611431565b93506118278185602086016112f0565b61183081610ec8565b840191505092915050565b600060408201905081810360008301526118558185611802565b9050611864602083018461107f565b9392505050565b600061187783856113de565b9350611884838584610f85565b82840190509392505050565b600061189d82848661186b565b91508190509392505050565b7f41646472657373206e6f74207265676973746572656400000000000000000000600082015250565b60006118df601683611431565b91506118ea826118a9565b602082019050919050565b6000602082019050818103600083015261190e816118d2565b9050919050565b60008190508160005260206000209050919050565b601f82111561196b5761193c81611915565b6119458461158f565b81016020851015611954578190505b6119686119608561158f565b830182611680565b50505b505050565b611979826112d4565b67ffffffffffffffff81111561199257611991610ed9565b5b61199c8254611549565b6119a782828561192a565b600060209050601f8311600181146119da57600084156119c8578287015190505b6119d28582611714565b865550611a3a565b601f1984166119e886611915565b60005b82811015611a10578489015182556001820191506020850194506020810190506119eb565b86831015611a2d5784890151611a29601f8916826116f6565b8355505b6001600288020188555050505b505050505050565b6000604082019050611a57600083018561107f565b8181036020830152611a698184611353565b90509392505050565b7f4e6f7420617574686f72697a6564000000000000000000000000000000000000600082015250565b6000611aa8600e83611431565b9150611ab382611a72565b602082019050919050565b60006020820190508181036000830152611ad781611a9b565b9050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052603260045260246000fd5b60008154611b1a81611549565b611b2481866113de565b94506001821660008114611b3f5760018114611b5457611b87565b60ff1983168652811515820286019350611b87565b611b5d8561157a565b60005b83811015611b7f57815481890152600182019150602081019050611b60565b838801955050505b50505092915050565b6000611b9c8284611b0d565b915081905092915050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b6000611be1826115fe565b91507fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff8203611c1357611c12611ba7565b5b600182019050919050565b7f557365726e616d65206e6f742072656769737465726564000000000000000000600082015250565b6000611c54601783611431565b9150611c5f82611c1e565b602082019050919050565b60006020820190508181036000830152611c8381611c47565b905091905056fea26469706673582212205603e4c7f6b93f554c9a300b62b09f8069d7ee978d4765d0bbea1f1f7435464964736f6c63430008130033";

    private static String librariesLinkedBinary;

    public static final String FUNC_REGISTERUSER = "registerUser";

    public static final String FUNC_GETUSERADDRESS = "getUserAddress";

    public static final String FUNC_STOREPREKEY = "storePrekey";

    public static final String FUNC_GETPREKEY = "getPrekey";

    public static final String FUNC_HASPREKEYBUNDLE = "hasPrekeyBundle";

    public static final String FUNC_RESETCONTRACT = "resetContract";

    public static final Event PREKEYSTORED_EVENT = new Event("PrekeyStored", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<PrekeyBundle>() {}));
    ;

    public static final Event USERREGISTERED_EVENT = new Event("UserRegistered", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Address>() {}));
    ;

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
        _addresses.put("5777", "0x75F7f8330f52AF6bd7497A86DBA1e32c15F4f004");
    }

    @Deprecated
    protected PrekeyManagement(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected PrekeyManagement(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected PrekeyManagement(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected PrekeyManagement(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<PrekeyStoredEventResponse> getPrekeyStoredEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(PREKEYSTORED_EVENT, transactionReceipt);
        ArrayList<PrekeyStoredEventResponse> responses = new ArrayList<PrekeyStoredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PrekeyStoredEventResponse typedResponse = new PrekeyStoredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.userAddress = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.prekeyBundle = (PrekeyBundle) eventValues.getNonIndexedValues().get(1);
            responses.add(typedResponse);
        }
        return responses;
    }

    public static PrekeyStoredEventResponse getPrekeyStoredEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(PREKEYSTORED_EVENT, log);
        PrekeyStoredEventResponse typedResponse = new PrekeyStoredEventResponse();
        typedResponse.log = log;
        typedResponse.userAddress = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.prekeyBundle = (PrekeyBundle) eventValues.getNonIndexedValues().get(1);
        return typedResponse;
    }

    public Flowable<PrekeyStoredEventResponse> prekeyStoredEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getPrekeyStoredEventFromLog(log));
    }

    public Flowable<PrekeyStoredEventResponse> prekeyStoredEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PREKEYSTORED_EVENT));
        return prekeyStoredEventFlowable(filter);
    }

    public static List<UserRegisteredEventResponse> getUserRegisteredEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(USERREGISTERED_EVENT, transactionReceipt);
        ArrayList<UserRegisteredEventResponse> responses = new ArrayList<UserRegisteredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            UserRegisteredEventResponse typedResponse = new UserRegisteredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.username = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.userAddress = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static UserRegisteredEventResponse getUserRegisteredEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(USERREGISTERED_EVENT, log);
        UserRegisteredEventResponse typedResponse = new UserRegisteredEventResponse();
        typedResponse.log = log;
        typedResponse.username = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.userAddress = (String) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<UserRegisteredEventResponse> userRegisteredEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getUserRegisteredEventFromLog(log));
    }

    public Flowable<UserRegisteredEventResponse> userRegisteredEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(USERREGISTERED_EVENT));
        return userRegisteredEventFlowable(filter);
    }

    public RemoteFunctionCall<TransactionReceipt> send_registerUser(String username) {
        final Function function = new Function(
                FUNC_REGISTERUSER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(username)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> call_getUserAddress(String username) {
        final Function function = new Function(FUNC_GETUSERADDRESS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(username)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> send_getUserAddress(String username) {
        final Function function = new Function(
                FUNC_GETUSERADDRESS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(username)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> send_storePrekey(byte[] identityKey,
            byte[] signedPrekey, byte[] prekeySignature) {
        final Function function = new Function(
                FUNC_STOREPREKEY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(identityKey), 
                new org.web3j.abi.datatypes.DynamicBytes(signedPrekey), 
                new org.web3j.abi.datatypes.DynamicBytes(prekeySignature)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<PrekeyBundle> call_getPrekey(String username) {
        final Function function = new Function(FUNC_GETPREKEY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(username)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<PrekeyBundle>() {}));
        return executeRemoteCallSingleValueReturn(function, PrekeyBundle.class);
    }

    public RemoteFunctionCall<TransactionReceipt> send_getPrekey(String username) {
        final Function function = new Function(
                FUNC_GETPREKEY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(username)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> call_hasPrekeyBundle(String username) {
        final Function function = new Function(FUNC_HASPREKEYBUNDLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(username)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> send_hasPrekeyBundle(String username) {
        final Function function = new Function(
                FUNC_HASPREKEYBUNDLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(username)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> send_resetContract() {
        final Function function = new Function(
                FUNC_RESETCONTRACT, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static PrekeyManagement load(String contractAddress, Web3j web3j,
            Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new PrekeyManagement(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static PrekeyManagement load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new PrekeyManagement(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static PrekeyManagement load(String contractAddress, Web3j web3j,
            Credentials credentials, ContractGasProvider contractGasProvider) {
        return new PrekeyManagement(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static PrekeyManagement load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new PrekeyManagement(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<PrekeyManagement> deploy(Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return deployRemoteCall(PrekeyManagement.class, web3j, credentials, contractGasProvider, getDeploymentBinary(), "");
    }

    public static RemoteCall<PrekeyManagement> deploy(Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(PrekeyManagement.class, web3j, transactionManager, contractGasProvider, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<PrekeyManagement> deploy(Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(PrekeyManagement.class, web3j, credentials, gasPrice, gasLimit, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<PrekeyManagement> deploy(Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(PrekeyManagement.class, web3j, transactionManager, gasPrice, gasLimit, getDeploymentBinary(), "");
    }

    public static void linkLibraries(List<Contract.LinkReference> references) {
        librariesLinkedBinary = linkBinaryWithReferences(BINARY, references);
    }

    private static String getDeploymentBinary() {
        if (librariesLinkedBinary != null) {
            return librariesLinkedBinary;
        } else {
            return BINARY;
        }
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static class PrekeyBundle extends DynamicStruct {
        public byte[] identityKey;

        public byte[] signedPrekey;

        public byte[] prekeySignature;

        public PrekeyBundle(byte[] identityKey, byte[] signedPrekey, byte[] prekeySignature) {
            super(new org.web3j.abi.datatypes.DynamicBytes(identityKey), 
                    new org.web3j.abi.datatypes.DynamicBytes(signedPrekey), 
                    new org.web3j.abi.datatypes.DynamicBytes(prekeySignature));
            this.identityKey = identityKey;
            this.signedPrekey = signedPrekey;
            this.prekeySignature = prekeySignature;
        }

        public PrekeyBundle(DynamicBytes identityKey, DynamicBytes signedPrekey,
                DynamicBytes prekeySignature) {
            super(identityKey, signedPrekey, prekeySignature);
            this.identityKey = identityKey.getValue();
            this.signedPrekey = signedPrekey.getValue();
            this.prekeySignature = prekeySignature.getValue();
        }
    }

    public static class PrekeyStoredEventResponse extends BaseEventResponse {
        public String userAddress;

        public PrekeyBundle prekeyBundle;
    }

    public static class UserRegisteredEventResponse extends BaseEventResponse {
        public String username;

        public String userAddress;
    }
}
