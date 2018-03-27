package io.wexchain.passport.chain.observer.domain;

import com.wexmarket.topia.commons.data.model.CreatedTimeModel;
import com.wexmarket.topia.commons.data.model.IdObject;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Parameter;
import javax.persistence.Table;
import java.math.BigInteger;
import java.util.Date;

/**
 * JuzixBlock
 *
 * @author zhengpeng
 */
@Entity
@Immutable
public class JuzixBlock extends CreatedTimeModel implements IdObject<Long> {

    @Id
    @GenericGenerator(name = "juzixBlockGenerator", strategy = "com.wexmarket.topia.commons.data.keygenerator.LongSequenceGenerator", parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "seq_juzix_block"),
            @org.hibernate.annotations.Parameter(name = "allocationSize", value = "1") })
    @GeneratedValue(generator = "juzixBlockGenerator")
    private Long id;

    @Column(name = "hash")
    private String hash;

    @Column(name = "parent_hash")
    private String parentHash;

    @Column(name = "block_number")
    private Long blockNumber;

    @Column(name = "nonce")
    private String nonce;

    @Column(name = "mix_hash")
    private String mixHash;

    @Column(name = "author")
    private String author;

    @Column(name = "miner")
    private String miner;

    @Column(name = "difficulty")
    private BigInteger difficulty;

    @Column(name = "total_difficulty")
    private BigInteger totalDifficulty;

    @Column(name = "block_size")
    private Long blockSize;

    @Column(name = "gas_limit")
    private BigInteger gasLimit;

    @Column(name = "gas_used")
    private BigInteger gasUsed;

    @Column(name = "block_timestamp")
    private Date blockTimestamp;

    @Column(name = "state_root")
    private String stateRoot;

    @Column(name = "transactions_root")
    private String transactionsRoot;

    @Column(name = "receipts_root")
    private String receiptsRoot;

    @Column(name = "transaction_count")
    private Integer transactionCount;

    @Column(name = "extra_data")
    private String extraData;


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getParentHash() {
        return parentHash;
    }

    public void setParentHash(String parentHash) {
        this.parentHash = parentHash;
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

    public String getMixHash() {
        return mixHash;
    }

    public void setMixHash(String mixHash) {
        this.mixHash = mixHash;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMiner() {
        return miner;
    }

    public void setMiner(String miner) {
        this.miner = miner;
    }

    public BigInteger getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(BigInteger difficulty) {
        this.difficulty = difficulty;
    }

    public BigInteger getTotalDifficulty() {
        return totalDifficulty;
    }

    public void setTotalDifficulty(BigInteger totalDifficulty) {
        this.totalDifficulty = totalDifficulty;
    }

    public Long getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(Long blockSize) {
        this.blockSize = blockSize;
    }

    public BigInteger getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(BigInteger gasLimit) {
        this.gasLimit = gasLimit;
    }

    public BigInteger getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(BigInteger gasUsed) {
        this.gasUsed = gasUsed;
    }

    public Date getBlockTimestamp() {
        return blockTimestamp;
    }

    public void setBlockTimestamp(Date blockTimestamp) {
        this.blockTimestamp = blockTimestamp;
    }

    public String getStateRoot() {
        return stateRoot;
    }

    public void setStateRoot(String stateRoot) {
        this.stateRoot = stateRoot;
    }

    public String getTransactionsRoot() {
        return transactionsRoot;
    }

    public void setTransactionsRoot(String transactionsRoot) {
        this.transactionsRoot = transactionsRoot;
    }

    public String getReceiptsRoot() {
        return receiptsRoot;
    }

    public void setReceiptsRoot(String receiptsRoot) {
        this.receiptsRoot = receiptsRoot;
    }

    public Integer getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Integer transactionCount) {
        this.transactionCount = transactionCount;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }
}
