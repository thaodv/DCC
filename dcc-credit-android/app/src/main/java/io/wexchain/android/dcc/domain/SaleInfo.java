package io.wexchain.android.dcc.domain;

import java.io.Serializable;

import io.wexchain.dccchainservice.util.DateUtil;

import static io.wexchain.dccchainservice.util.DateUtil.getStringTime;

public class SaleInfo implements Serializable {
    /**
     * presentation :
     * 币生息DCC私链1期是面向DCC用户的一款固定周期、固定DCC
     * 收益的数字资产增值理财产品；具有：极低风险（不包括数字资产本身价格的涨跌），保本保息、到期还本付息的特点。用户在募集期内可以认购币生息DCC私链1期理财产品，理财收益以DCC私链形式返回，平台为DCC
     * 用户提供DCC私链资产理财和到期自动赎回服务，于到期日的23:59:59之前，会自动将本金和收益（收益以DCC私链形式计息）返还到用户的钱包地址。本理财产品由基于Linux
     * 环境搭建的量化交易系统TokenPlus提供支持，作为回馈DCC用户的福利，无任何风险。用户可在【币生息持仓】中查看币生息理财产品的收益。
     * startTime : 1534211619
     * endTime : 1534211619
     * name : 币生息DCC私1期
     * period : 28
     * annualRate : 10
     * profitMethod : 到期还本付息
     */
    
    private String presentation;
    private long startTime;
    private long endTime;
    private String name;
    private int period;
    private int annualRate;
    private String profitMethod;
    private long closeTime;
    private long incomeTime;
    
    
    private int minAmountPerHand;
    
    public long getIncomeTime() {
        return incomeTime;
    }
    
    public void setIncomeTime(long incomeTime) {
        this.incomeTime = incomeTime;
    }
    
    SaleInfo() {
    }
    
    public String minAmountPerHandDCC() {
        return minAmountPerHand + " DCC";
    }
    
    public String periodDay() {
        return period + " 天";
    }
    
    public String annualRateP() {
        return annualRate + "%";
    }
    
    public String showStartTime() {
        return getStringTime(startTime, DateUtil.dateFormatString);
    }
    
    public String showIncomeTime() {
        return getStringTime(incomeTime, DateUtil.dateFormatString);
    }
    
    public String showEndTime() {
        return getStringTime(endTime, DateUtil.dateFormatString);
    }
    
    public String showCloseTime() {
        return "至" + getStringTime(closeTime, DateUtil.dateFormatStringChi) + "，或购完即止";
    }
    
    
    public long getCloseTime() {
        return closeTime;
    }
    
    public void setCloseTime(long closeTime) {
        this.closeTime = closeTime;
    }
    
    public int getMinAmountPerHand() {
        return minAmountPerHand;
    }
    
    public void setMinAmountPerHand(int minAmountPerHand) {
        this.minAmountPerHand = minAmountPerHand;
    }
    
    public String getPresentation() {
        return presentation;
    }
    
    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    
    public long getEndTime() {
        return endTime;
    }
    
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getPeriod() {
        return period;
    }
    
    public void setPeriod(int period) {
        this.period = period;
    }
    
    public int getAnnualRate() {
        return annualRate;
    }
    
    public void setAnnualRate(int annualRate) {
        this.annualRate = annualRate;
    }
    
    public String getProfitMethod() {
        return profitMethod;
    }
    
    public void setProfitMethod(String profitMethod) {
        this.profitMethod = profitMethod;
    }
}
