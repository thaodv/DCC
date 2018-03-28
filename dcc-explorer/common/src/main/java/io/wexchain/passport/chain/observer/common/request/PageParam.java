package io.wexchain.passport.chain.observer.common.request;

/**
 * PageParam
 *
 * @author zhengpeng
 */
public class PageParam {

    private int page = 1;

    private int pageSize = 20;

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
