//
//  WeXNewLoanCoinViewController.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/20.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseReuseTableViewController.h"

@class WeXQueryProductByLenderCodeResponseModal_item;

@interface WeXNewLoanCoinViewController : WeXBaseReuseTableViewController
@property (nonatomic, strong) NSArray <WeXQueryProductByLenderCodeResponseModal_item *> *hotCoins;

@end
