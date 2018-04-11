package io.wexchain.passport.chain.observer.domain;

import com.wexmarket.topia.commons.data.model.MutableModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * BlockSync
 *
 * @author zhengpeng
 */
@Entity
@Table(name = "t_block_sync")
public class BlockSync extends MutableModel {

    @Id
    private String code;

    @Column(name = "block_number")
    private Long blockNumber;

    @Column(name = "data")
    private String data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
