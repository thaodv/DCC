//
//  WeXSelectNodeTableViewCell.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/7.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseTableViewCell.h"
#import "WeXNetworkCheckModel.h"

@interface WeXSelectNodeTableViewCell : WeXBaseTableViewCell

- (void)setDelayModel:(WeXNetworkCheckModel *)model isSelected:(BOOL)isSelected;

@end
