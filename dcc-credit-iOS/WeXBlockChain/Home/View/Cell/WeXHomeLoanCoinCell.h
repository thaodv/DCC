//
//  WeXHomeLoanCoinCell.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/18.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseTableViewCell.h"
@class WeXWalletDigitalGetTokenResponseModal_item;
@class WeXQueryProductByLenderCodeResponseModal_item;

@interface WeXHomeLoanCoinCell : WeXBaseTableViewCell

- (void)setLeftModel:(WeXQueryProductByLenderCodeResponseModal_item *)leftModel
          rightModel:(WeXQueryProductByLenderCodeResponseModal_item *)rightModel;

+ (CGFloat)cellHeight;

@end
