//
//  WeXDailyTaskHeadSignCell.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/1.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseTableViewCell.h"

@class WeXTaskSignResModel;
@class WeXTaskGetSunBalanceResModel;


@interface WeXDailyTaskHeadSignCell : WeXBaseTableViewCell

@property (nonatomic,copy) void (^SignEvent)(void);
@property (nonatomic,copy) void (^ClickSunBalance)(void);

- (void)setHaveSignDays:(NSInteger)signNumber
            todayStatus:(NSInteger)stauts;

- (void)setTaskSignResModel:(WeXTaskSignResModel *)resModel
               balanceModel:(WeXTaskGetSunBalanceResModel *)balancemodel
                todayStatus:(NSInteger)stauts;



@end


