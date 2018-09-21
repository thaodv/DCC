//
//  WeXLoanRecentOrderCell.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/20.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseTableViewCell.h"

@class WeXLoanGetOrderDetailResponseModal;
@interface WeXLoanRecentOrderCell : WeXBaseTableViewCell
@property (nonatomic,copy) void (^DidClickMoreOrder)(void);


- (void)setOrderNum:(NSString *)orderNum
             symbol:(NSString *)symbol
         loanAmount:(NSString *)loanAmount
        repayAmount:(NSString *)repayAmount
             period:(NSString *)period
             status:(NSString *)status;

- (void)setRecentOrderData:(WeXLoanGetOrderDetailResponseModal *)data;



@end
