//
//  WeXCPPotInfoCell.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/19.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseTableViewCell.h"

typedef NS_ENUM(NSInteger,CPPotInfoCellType) {
    CPPotInfoCellTypeBuying   = 0,  //认购中
    CPPotInfoCellTypeIncoming = 1,  //收益中
    CPPotInfoCellTypeSellOver = 2,  //已售完
    CPPotInfoCellTypeOver     = 3,  //已结束
    
};

@class WeXCPPotListResultModel;
@interface WeXCPPotInfoCell : WeXBaseTableViewCell

- (void)setTitle:(NSString *)title
          profit:(NSString *)profit
          principal:(NSString *)principal
       startTime:(NSString *)startTime
          period:(NSString *)period
         endTime:(NSString *)endTime
     totalAmount:(NSString *)totalAmount
            type:(CPPotInfoCellType)type;

- (void)setPotListModel:(WeXCPPotListResultModel *)model;

@end
