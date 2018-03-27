package io.wexchain.passport.chain.observer.domainservice.request;

import com.wexmarket.topia.commons.pagination.SortPageParam;

/**
 * QueryTokenLogRequest
 *
 * @author zhengpeng
 */
public class QueryTokenLogRequest {

    private String contractAddress;

    private String walletAddress;

    private int page = 1;

    private int pageSize = 20;

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
