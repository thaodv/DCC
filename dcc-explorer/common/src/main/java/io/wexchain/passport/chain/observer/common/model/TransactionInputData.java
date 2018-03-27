package io.wexchain.passport.chain.observer.common.model;

/**
 * TransactionInputData
 *
 * @author zhengpeng
 */
public class TransactionInputData {

    private String contractAddress;

    private String inputData;

    private String abi;

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public String getAbi() {
        return abi;
    }

    public void setAbi(String abi) {
        this.abi = abi;
    }
}
