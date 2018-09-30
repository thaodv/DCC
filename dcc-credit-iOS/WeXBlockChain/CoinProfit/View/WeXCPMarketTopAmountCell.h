//
//  WeXCPMarketTopAmountCell.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/19.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseTableViewCell.h"

@interface WeXCPMarketTopAmountCell : WeXBaseTableViewCell

- (void)setPrincipal:(NSString *)principal
              profit:(NSString *)profit;

- (void)setRightArrowHide:(BOOL)isHidden;

@end
