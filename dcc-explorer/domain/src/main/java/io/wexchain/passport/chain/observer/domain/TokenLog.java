package io.wexchain.passport.chain.observer.domain;

import com.wexmarket.topia.commons.data.model.CreatedTimeModel;
import com.wexmarket.topia.commons.data.model.IdObject;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.math.BigInteger;

/**
 * TokenLog
 *
 * @author zhengpeng
 */
@Entity
@Immutable
public class TokenLog extends CreatedTimeModel implements IdObject<Long> {

    @Id
    @SequenceGenerator(name = "tokenLogGenerator", sequenceName = "seq_token_log", allocationSize = 1)
    @GeneratedValue(generator = "tokenLogGenerator")
    private Long id;

    @Column(name = "log_index")
    private String logIndex;

    @Column(name = "transaction_index")
    private String transactionIndex;

    @Column(name = "transaction_hash")
    private String transactionHash;

    @Column(name = "block_hash")
    private String blockHash;

    @Column(name = "block_number")
    private String blockNumber;

    @Column(name = "contract_address")
    private String contractAddress;

    @Column(name = "type")
    private String type;

    @Column(name = "from_address")
    private String fromAddress;

    @Column(name = "to_address")
    private String toAddress;

    @Column(name = "amount")
    private BigInteger amount;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogIndex() {
        return logIndex;
    }

    public void setLogIndex(String logIndex) {
        this.logIndex = logIndex;
    }

    public String getTransactionIndex() {
        return transactionIndex;
    }

    public void setTransactionIndex(String transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public String getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public BigInteger getAmount() {
        return amount;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }
}

