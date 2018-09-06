//
//  WeXBuyInTableViewCell.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/14.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseTableViewCell.h"

typedef NS_ENUM(NSInteger,WeXBuyInTableViewCellType) {
    WeXBuyInTableViewCellTypeBuyIn = 0, //认购
    WeXBuyInTableViewCellTypeSellOver = 1, //已售罄
    WeXBuyInTableViewCellTypeEnd = 2, //已结束
};

@interface WeXBuyInTableViewCell : WeXBaseTableViewCell

@property (nonatomic,copy) void (^BuyInEvent)(void);

- (void)setBuyInCellType:(WeXBuyInTableViewCellType)type;


@end
