package io.wexchain.dcc.marketing.domainservice.function.miningevent;

import java.util.List;

public class MiningEventHandlers {

    private List<MiningEventHandler> certHandlerList;

    private List<MiningEventHandler> loanHandlerList;

    private List<MiningEventHandler> loginHandlerList;

    public List<MiningEventHandler> getCertHandlerList() {
        return certHandlerList;
    }

    public void setCertHandlerList(List<MiningEventHandler> certHandlerList) {
        this.certHandlerList = certHandlerList;
    }

    public List<MiningEventHandler> getLoanHandlerList() {
        return loanHandlerList;
    }

    public void setLoanHandlerList(List<MiningEventHandler> loanHandlerList) {
        this.loanHandlerList = loanHandlerList;
    }

    public List<MiningEventHandler> getLoginHandlerList() {
        return loginHandlerList;
    }

    public void setLoginHandlerList(List<MiningEventHandler> loginHandlerList) {
        this.loginHandlerList = loginHandlerList;
    }
}
