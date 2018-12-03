//
//  WeXSunRecordDetailCell.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/10/30.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseTableViewCell.h"

@class WeXSunBlanceModel;

@interface WeXSunRecordDetailCell : WeXBaseTableViewCell

//- (void)setTitle:(NSString *)title
//            time:(NSString *)time
//           value:(NSString *)value;

- (void)setSunBalanceListModel:(WeXSunBlanceModel *)model;

@end

