//
//  WeXRepayViewController.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/10/15.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseViewController.h"

@class WeXMyLoanDetailModel;

@interface WeXRepayViewController : WeXBaseViewController

@property (nonatomic, strong) WeXMyLoanDetailModel *detailModel;
@property (nonatomic, assign) NSInteger status;



@end

