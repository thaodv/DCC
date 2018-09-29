package io.wexchain.dccchainservice.domain;

import java.io.Serializable;

/**
 * @author Created by Wangpeng on 2018/9/21 19:06.
 * usage:
 */
public class BsxMarketBean implements Serializable {
    
    public String logo;
    public String assetCode;
    public String status;
    public String minPerHand;
    public SaleInfoBean saleInfo;
    public String name;
    public String contractAddress;
    
    public static class SaleInfoBean {
        /**
         * presentation :
         * 币生息DCC私链1期是面向DCC用户的一款固定周期、固定DCC
         * 收益的数字资产增值理财产品；具有：极低风险（不包括数字资产本身价格的涨跌），保本保息、到期还本付息的特点。用户在募集期内可以认购币生息DCC私链1期理财产品，理财收益以DCC
         * 私链形式返回，平台为DCC用户提供DCC私链资产理财和到期自动赎回服务，于到期日的23:59:59之前，会自动将本金和收益（收益以DCC
         * 私链形式计息）返还到用户的钱包地址。本理财产品由基于Linux环境搭建的量化交易系统TokenPlus提供支持，作为回馈DCC
         * 用户的福利，无任何风险。用户可在【币生息持仓】中查看币生息理财产品的收益。
         * name : 币生息DCC私1期
         * startTime : 1534211619000
         * closeTime : 1535511010000
         * incomeTime : 1535683810000
         * endTime : 1536976419000
         * period : 28
         * annualRate : 10
         * profitMethod : 到期还本付息
         */
        
        public String presentation;
        public String name;
        public long startTime;
        public long closeTime;
        public long incomeTime;
        public long endTime;
        public String period;
        public String annualRate;
        public String profitMethod;
    }
}
