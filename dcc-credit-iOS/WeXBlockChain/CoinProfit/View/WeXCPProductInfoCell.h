//
//  WeXCPProductInfoCell.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/19.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseTableViewCell.h"

typedef NS_ENUM(NSInteger,ProductInfoCellType) {
    ProductInfoCellTypeBuying   = 0,  //认购中
    ProductInfoCellTypeIncoming = 1,  //收益中
    ProductInfoCellTypeSellOver = 2,  //已售完
    ProductInfoCellTypeOver     = 3,  //已结束
    
};

@class WeXCPActivityListModel;

@interface WeXCPProductInfoCell : WeXBaseTableViewCell

- (void)setTitle:(NSString *)title
          profit:(NSString *)profit
          symbol:(NSString *)symbol
          period:(NSString *)period
       minAmount:(NSString *)minAmount
            type:(ProductInfoCellType)type;

- (void)setMarkeetProductModel:(WeXCPActivityListModel *)model;




@end
