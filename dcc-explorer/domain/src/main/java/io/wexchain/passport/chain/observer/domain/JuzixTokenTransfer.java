package io.wexchain.passport.chain.observer.domain;

import com.wexmarket.topia.commons.data.model.CreatedTimeModel;
import com.wexmarket.topia.commons.data.model.IdObject;
import io.wexchain.passport.chain.observer.common.constant.AddressType;
import io.wexchain.passport.chain.observer.common.constant.JuzixTxStatus;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

/**
 * JuzixTokenTransfer
 *
 * @author zhengpeng
 */
@Entity
@Immutable
public class JuzixTokenTransfer extends CreatedTimeModel implements IdObject<Long> {

    @Id
    @GenericGenerator(name = "juzixTokenTransferGenerator", strategy = "com.wexmarket.topia.commons.data.keygenerator.LongSequenceGenerator", parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "seq_token_transfer"),
            @org.hibernate.annotations.Parameter(name = "allocationSize", value = "1") })
    @GeneratedValue(generator = "juzixTokenTransferGenerator")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private JuzixTransaction transaction;

    @Column(name = "contract_address")
    private String contractAddress;

    @Column(name = "log_index")
    private Integer logIndex;

    @Column(name = "block_hash")
    private String blockHash;

    @Column(name = "block_number")
    private Long blockNumber;

    @Column(name = "block_timestamp")
    private Date blockTimestamp;

    @Column(name = "transaction_hash")
    private String transactionHash;

    @Column(name = "transaction_index")
    private Integer transactionIndex;

    @Column(name = "from_address")
    private String fromAddress;

    @Column(name = "to_address")
    private String toAddress;

    @Column(name = "value")
    private BigInteger value;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JuzixTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(JuzixTransaction transaction) {
        this.transaction = transaction;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public Integer getLogIndex() {
        return logIndex;
    }

    public void setLogIndex(Integer logIndex) {
        this.logIndex = logIndex;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public Long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public Date getBlockTimestamp() {
        return blockTimestamp;
    }

    public void setBlockTimestamp(Date blockTimestamp) {
        this.blockTimestamp = blockTimestamp;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public Integer getTransactionIndex() {
        return transactionIndex;
    }

    public void setTransactionIndex(Integer transactionIndex) {
        this.transactionIndex = transactionIndex;
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

    public BigInteger getValue() {
        return value;
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }
}
