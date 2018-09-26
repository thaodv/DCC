//
//  WeXCoinProfitDetailViewController.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/13.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseReuseTableViewController.h"

@interface WeXCoinProfitDetailViewController : WeXBaseReuseTableViewController
//代币
@property (nonatomic, copy) NSString *assetCode;


- (void)wex_refreshContent;

@end
