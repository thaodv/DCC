//
//  WeXCoinProfitCell.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/13.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseTableViewCell.h"

typedef NS_ENUM(NSInteger,WexCoinProfitCellType) {
    WexCoinProfitCellTypeCandy  = 0, //糖果领取
    WexCoinProfitCellTypeCrdit  = 1, //我的信用
    WexCoinProfitCellTypeLoan   = 2, //信用借币
    WexCoinProfitCellTypeReport = 3, //借贷报告
    WexCoinProfitCellTypeProfit = 4, //币生息
};

@interface WeXCoinProfitCell : WeXBaseTableViewCell

@property (nonatomic,copy) void (^DidClickCell)(WexCoinProfitCellType);

+ (CGFloat)cellHeight;

- (void)setIsAllowAppear:(BOOL)isAppear;


@end

@interface WeXCoinProfitView : UIView

- (void)setIconImage:(NSString *)image
               title:(NSString *)title;

@end
