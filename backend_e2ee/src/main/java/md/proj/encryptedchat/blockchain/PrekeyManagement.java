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
    public static final String BINARY = "0x608060405234801561001057600080fd5b50336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550611b0e806100606000396000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c80634985e85c14610067578063704f1b94146100975780637b87f89a146100b3578063887c0b49146100e3578063b181a8fc146100ff578063d79e1cc714610109575b600080fd5b610081600480360381019061007c9190610e9b565b610139565b60405161008e9190610f25565b60405180910390f35b6100b160048036038101906100ac9190610e9b565b610181565b005b6100cd60048036038101906100c89190610fa0565b610444565b6040516100da9190611008565b60405180910390f35b6100fd60048036038101906100f891906110c4565b61065f565b005b6101076107b1565b005b610123600480360381019061011e9190610fa0565b6109f1565b60405161013091906111ff565b60405180910390f35b600060018260405161014b9190611268565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050919050565b600073ffffffffffffffffffffffffffffffffffffffff166001826040516101a99190611268565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff161461022e576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610225906112dc565b60405180910390fd5b600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16156102bb576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016102b290611348565b60405180910390fd5b336001826040516102cc9190611268565b908152602001604051809103902060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506001600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055506003819080600181540180825580915050600190039060005260206000200160009091909190915090816103a4919061157e565b506004339080600181540180825580915050600190039060005260206000200160009091909190916101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055507f7f605d0ff47d2a070e456b69ce2a36041788910b64443c7f4c6d307993cade2c8133604051610439929190611689565b60405180910390a150565b600080600184846040516104599291906116de565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff16036104c8576000915050610659565b6000600560008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060405180604001604052908160008201805461052490611397565b80601f016020809104026020016040519081016040528092919081815260200182805461055090611397565b801561059d5780601f106105725761010080835404028352916020019161059d565b820191906000526020600020905b81548152906001019060200180831161058057829003601f168201915b505050505081526020016001820180546105b690611397565b80601f01602080910402602001604051908101604052809291908181526020018280546105e290611397565b801561062f5780601f106106045761010080835404028352916020019161062f565b820191906000526020600020905b81548152906001019060200180831161061257829003601f168201915b5050505050815250509050600081600001515111801561065457506000816020015151115b925050505b92915050565b600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff166106eb576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016106e290611743565b60405180910390fd5b6000604051806040016040528084815260200183815250905080600560003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082015181600001908161075991906117be565b50602082015181600101908161076f91906117be565b509050507f9f13e202616ec8880592acfca17a7a058e1b716e1b6d54df2614207e388bead433826040516107a4929190611890565b60405180910390a1505050565b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161461083f576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016108369061190c565b60405180910390fd5b60005b6003805490508110156109d25760006001600383815481106108675761086661192c565b5b9060005260206000200160405161087e91906119de565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690506001600383815481106108c5576108c461192c565b5b906000526020600020016040516108dc91906119de565b908152602001604051809103902060006101000a81549073ffffffffffffffffffffffffffffffffffffffff0219169055600260008273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81549060ff0219169055600560008273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600080820160006109ac9190610c24565b6001820160006109bc9190610c24565b50505080806109ca90611a24565b915050610842565b50600360006109e19190610c64565b600460006109ef9190610c85565b565b6109f9610ca6565b600060018484604051610a0d9291906116de565b908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff1603610aad576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610aa490611ab8565b60405180910390fd5b600560008273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020604051806040016040529081600082018054610b0790611397565b80601f0160208091040260200160405190810160405280929190818152602001828054610b3390611397565b8015610b805780601f10610b5557610100808354040283529160200191610b80565b820191906000526020600020905b815481529060010190602001808311610b6357829003601f168201915b50505050508152602001600182018054610b9990611397565b80601f0160208091040260200160405190810160405280929190818152602001828054610bc590611397565b8015610c125780601f10610be757610100808354040283529160200191610c12565b820191906000526020600020905b815481529060010190602001808311610bf557829003601f168201915b50505050508152505091505092915050565b508054610c3090611397565b6000825580601f10610c425750610c61565b601f016020900490600052602060002090810190610c609190610cc0565b5b50565b5080546000825590600052602060002090810190610c829190610cdd565b50565b5080546000825590600052602060002090810190610ca39190610cc0565b50565b604051806040016040528060608152602001606081525090565b5b80821115610cd9576000816000905550600101610cc1565b5090565b5b80821115610cfd5760008181610cf49190610d01565b50600101610cde565b5090565b508054610d0d90611397565b6000825580601f10610d1f5750610d3e565b601f016020900490600052602060002090810190610d3d9190610cc0565b5b50565b6000604051905090565b600080fd5b600080fd5b600080fd5b600080fd5b6000601f19601f8301169050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b610da882610d5f565b810181811067ffffffffffffffff82111715610dc757610dc6610d70565b5b80604052505050565b6000610dda610d41565b9050610de68282610d9f565b919050565b600067ffffffffffffffff821115610e0657610e05610d70565b5b610e0f82610d5f565b9050602081019050919050565b82818337600083830152505050565b6000610e3e610e3984610deb565b610dd0565b905082815260208101848484011115610e5a57610e59610d5a565b5b610e65848285610e1c565b509392505050565b600082601f830112610e8257610e81610d55565b5b8135610e92848260208601610e2b565b91505092915050565b600060208284031215610eb157610eb0610d4b565b5b600082013567ffffffffffffffff811115610ecf57610ece610d50565b5b610edb84828501610e6d565b91505092915050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000610f0f82610ee4565b9050919050565b610f1f81610f04565b82525050565b6000602082019050610f3a6000830184610f16565b92915050565b600080fd5b600080fd5b60008083601f840112610f6057610f5f610d55565b5b8235905067ffffffffffffffff811115610f7d57610f7c610f40565b5b602083019150836001820283011115610f9957610f98610f45565b5b9250929050565b60008060208385031215610fb757610fb6610d4b565b5b600083013567ffffffffffffffff811115610fd557610fd4610d50565b5b610fe185828601610f4a565b92509250509250929050565b60008115159050919050565b61100281610fed565b82525050565b600060208201905061101d6000830184610ff9565b92915050565b600067ffffffffffffffff82111561103e5761103d610d70565b5b61104782610d5f565b9050602081019050919050565b600061106761106284611023565b610dd0565b90508281526020810184848401111561108357611082610d5a565b5b61108e848285610e1c565b509392505050565b600082601f8301126110ab576110aa610d55565b5b81356110bb848260208601611054565b91505092915050565b600080604083850312156110db576110da610d4b565b5b600083013567ffffffffffffffff8111156110f9576110f8610d50565b5b61110585828601611096565b925050602083013567ffffffffffffffff81111561112657611125610d50565b5b61113285828601611096565b9150509250929050565b600081519050919050565b600082825260208201905092915050565b60005b8381101561117657808201518184015260208101905061115b565b60008484015250505050565b600061118d8261113c565b6111978185611147565b93506111a7818560208601611158565b6111b081610d5f565b840191505092915050565b600060408301600083015184820360008601526111d88282611182565b915050602083015184820360208601526111f28282611182565b9150508091505092915050565b6000602082019050818103600083015261121981846111bb565b905092915050565b600081519050919050565b600081905092915050565b600061124282611221565b61124c818561122c565b935061125c818560208601611158565b80840191505092915050565b60006112748284611237565b915081905092915050565b600082825260208201905092915050565b7f557365726e616d6520616c72656164792074616b656e00000000000000000000600082015250565b60006112c660168361127f565b91506112d182611290565b602082019050919050565b600060208201905081810360008301526112f5816112b9565b9050919050565b7f4164647265737320616c72656164792072656769737465726564000000000000600082015250565b6000611332601a8361127f565b915061133d826112fc565b602082019050919050565b6000602082019050818103600083015261136181611325565b9050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b600060028204905060018216806113af57607f821691505b6020821081036113c2576113c1611368565b5b50919050565b60008190508160005260206000209050919050565b60006020601f8301049050919050565b600082821b905092915050565b60006008830261142a7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff826113ed565b61143486836113ed565b95508019841693508086168417925050509392505050565b6000819050919050565b6000819050919050565b600061147b6114766114718461144c565b611456565b61144c565b9050919050565b6000819050919050565b61149583611460565b6114a96114a182611482565b8484546113fa565b825550505050565b600090565b6114be6114b1565b6114c981848461148c565b505050565b5b818110156114ed576114e26000826114b6565b6001810190506114cf565b5050565b601f82111561153257611503816113c8565b61150c846113dd565b8101602085101561151b578190505b61152f611527856113dd565b8301826114ce565b50505b505050565b600082821c905092915050565b600061155560001984600802611537565b1980831691505092915050565b600061156e8383611544565b9150826002028217905092915050565b61158782611221565b67ffffffffffffffff8111156115a05761159f610d70565b5b6115aa8254611397565b6115b58282856114f1565b600060209050601f8311600181146115e857600084156115d6578287015190505b6115e08582611562565b865550611648565b601f1984166115f6866113c8565b60005b8281101561161e578489015182556001820191506020850194506020810190506115f9565b8683101561163b5784890151611637601f891682611544565b8355505b6001600288020188555050505b505050505050565b600061165b82611221565b611665818561127f565b9350611675818560208601611158565b61167e81610d5f565b840191505092915050565b600060408201905081810360008301526116a38185611650565b90506116b26020830184610f16565b9392505050565b60006116c5838561122c565b93506116d2838584610e1c565b82840190509392505050565b60006116eb8284866116b9565b91508190509392505050565b7f41646472657373206e6f74207265676973746572656400000000000000000000600082015250565b600061172d60168361127f565b9150611738826116f7565b602082019050919050565b6000602082019050818103600083015261175c81611720565b9050919050565b60008190508160005260206000209050919050565b601f8211156117b95761178a81611763565b611793846113dd565b810160208510156117a2578190505b6117b66117ae856113dd565b8301826114ce565b50505b505050565b6117c78261113c565b67ffffffffffffffff8111156117e0576117df610d70565b5b6117ea8254611397565b6117f5828285611778565b600060209050601f8311600181146118285760008415611816578287015190505b6118208582611562565b865550611888565b601f19841661183686611763565b60005b8281101561185e57848901518255600182019150602085019450602081019050611839565b8683101561187b5784890151611877601f891682611544565b8355505b6001600288020188555050505b505050505050565b60006040820190506118a56000830185610f16565b81810360208301526118b781846111bb565b90509392505050565b7f4e6f7420617574686f72697a6564000000000000000000000000000000000000600082015250565b60006118f6600e8361127f565b9150611901826118c0565b602082019050919050565b60006020820190508181036000830152611925816118e9565b9050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052603260045260246000fd5b6000815461196881611397565b611972818661122c565b9450600182166000811461198d57600181146119a2576119d5565b60ff19831686528115158202860193506119d5565b6119ab856113c8565b60005b838110156119cd578154818901526001820191506020810190506119ae565b838801955050505b50505092915050565b60006119ea828461195b565b915081905092915050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b6000611a2f8261144c565b91507fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff8203611a6157611a606119f5565b5b600182019050919050565b7f557365726e616d65206e6f742072656769737465726564000000000000000000600082015250565b6000611aa260178361127f565b9150611aad82611a6c565b602082019050919050565b60006020820190508181036000830152611ad181611a95565b905091905056fea26469706673582212202495c15ec6f6612f4e9abd1f8e8146bbf57cd9b305ec8fa20992fd05457b61a464736f6c63430008130033";

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
        _addresses.put("5777", "0x65c4CfC10B5838244454c13E42355F61A0468C1C");
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
            byte[] signedPrekey) {
        final Function function = new Function(
                FUNC_STOREPREKEY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(identityKey), 
                new org.web3j.abi.datatypes.DynamicBytes(signedPrekey)), 
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

        public PrekeyBundle(byte[] identityKey, byte[] signedPrekey) {
            super(new org.web3j.abi.datatypes.DynamicBytes(identityKey), 
                    new org.web3j.abi.datatypes.DynamicBytes(signedPrekey));
            this.identityKey = identityKey;
            this.signedPrekey = signedPrekey;
        }

        public PrekeyBundle(DynamicBytes identityKey, DynamicBytes signedPrekey) {
            super(identityKey, signedPrekey);
            this.identityKey = identityKey.getValue();
            this.signedPrekey = signedPrekey.getValue();
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
