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
 * JuzixTransaction
 *
 * @author zhengpeng
 */
@Entity
@Immutable
public class JuzixTransaction extends CreatedTimeModel implements IdObject<Long> {

    @Id
    @GenericGenerator(name = "juzixTransactionGenerator", strategy = "com.wexmarket.topia.commons.data.keygenerator.LongSequenceGenerator", parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "seq_juzix_transaction"),
            @org.hibernate.annotations.Parameter(name = "allocationSize", value = "1") })
    @GeneratedValue(generator = "juzixTransactionGenerator")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "block_id")
    private JuzixBlock block;

    @Column(name = "hash")
    private String hash;

    @Column(name = "block_hash")
    private String blockHash;

    @Column(name = "block_number")
    private Long blockNumber;

    @Column(name = "nonce")
    private String nonce;

    @Column(name = "transaction_index")
    private Integer transactionIndex;

    @Column(name = "from_address")
    private String fromAddress;

    @Column(name = "from_type")
    @Enumerated(EnumType.STRING)
    private AddressType fromType;

    @Column(name = "to_address")
    private String toAddress;

    @Column(name = "to_type")
    @Enumerated(EnumType.STRING)
    private AddressType toType;

    @Column(name = "value")
    private BigInteger value;

    @Column(name = "gas_price")
    private BigInteger gasPrice;

    @Column(name = "gas")
    private BigInteger gas;

    @Column(name = "block_timestamp")
    private Date blockTimestamp;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private JuzixTxStatus status;

    @Transient
    private String inputData;

    @Transient
    private BigInteger dccValue;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JuzixBlock getBlock() {
        return block;
    }

    public void setBlock(JuzixBlock block) {
        this.block = block;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
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

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
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

    public AddressType getFromType() {
        return fromType;
    }

    public void setFromType(AddressType fromType) {
        this.fromType = fromType;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public AddressType getToType() {
        return toType;
    }

    public void setToType(AddressType toType) {
        this.toType = toType;
    }

    public JuzixTxStatus getStatus() {
        return status;
    }

    public void setStatus(JuzixTxStatus status) {
        this.status = status;
    }

    public BigInteger getValue() {
        return value;
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }

    public BigInteger getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(BigInteger gasPrice) {
        this.gasPrice = gasPrice;
    }

    public BigInteger getGas() {
        return gas;
    }

    public void setGas(BigInteger gas) {
        this.gas = gas;
    }

    public Date getBlockTimestamp() {
        return blockTimestamp;
    }

    public void setBlockTimestamp(Date blockTimestamp) {
        this.blockTimestamp = blockTimestamp;
    }

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public BigInteger getDccValue() {
        return dccValue;
    }

    public void setDccValue(BigInteger dccValue) {
        this.dccValue = dccValue;
    }
}
