//
//  WeXLoanHotCoinsCell.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/21.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseTableViewCell.h"

@class WeXQueryProductByLenderCodeResponseModal_item;

@interface WeXLoanHotCoinsCell : WeXBaseTableViewCell

- (void)setLoanHotCoinsModel:(WeXQueryProductByLenderCodeResponseModal_item *)model;

@end
