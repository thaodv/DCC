//
//  WeXCoinProfitTopProfitCell.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/13.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseTableViewCell.h"

@class WeXCPSaleInfoResModel;
@class WeXCPActivityListModel;


typedef NS_ENUM(NSInteger,WexCPStatusLabelType) {
    WexCPStatusLabelTypeCreate = 0,     //活动创建
    WexCPStatusLabelTypeStart  = 1,    //活动开始 (募集中)
    WexCPStatusLabelTypeClose  = 2,    //活动关闭 (收益中)
    WexCPStatusLabelTypeEnd    = 3,    //活动结束
    WexCPStatusLabelTypeComplete = 4, //结算完成 (已结束)
};

@interface WeXCoinProfitTopProfitCell : WeXBaseTableViewCell

- (void)setStatusLabelType:(WexCPStatusLabelType)type;

- (void)setSaleInfoModel:(WeXCPSaleInfoResModel *)resModel;

- (void)setMinBuyAmount:(NSString *)minAmount assetCode:(NSString *)assetCode;

- (void)setNewCoinProfitDetailWithProductModel:(WeXCPActivityListModel *)model;

/**
 持仓界面
 @param resModel Model
 @param type WexCPStatusLabelType
 @param totalAmount 投资总金额
 @param assetCode 代币
 */
- (void)setSaleInfoModel:(WeXCPSaleInfoResModel *)resModel
                    type:(WexCPStatusLabelType)type
             totalAmount:(NSString *)totalAmount
               assetCode:(NSString *)assetCode;


/**
 持仓界面

 @param resModel model
 @param status 状态string
 @param amount 金额
 @param assetCode 代币
 */
- (void)setSaleInfoModel:(WeXCPSaleInfoResModel *)resModel
            statusString:(NSString *)status
             totalAmount:(NSString *)amount
               assetCode:(NSString *)assetCode;


@end
