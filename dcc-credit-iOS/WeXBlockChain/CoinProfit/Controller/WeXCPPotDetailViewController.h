//
//  WeXCPPotDetailViewController.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/14.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseReuseTableViewController.h"

@class WeXCPActivityListModel;
@class WeXCPPotListResultModel;

@interface WeXCPPotDetailViewController : WeXBaseReuseTableViewController

@property (nonatomic, assign) BOOL popToCoinProfitDetailVC;

@property (nonatomic,strong) WeXCPActivityListModel *buyProductModel;
//持仓列表传过来数据
@property (nonatomic, strong) WeXCPPotListResultModel  * potListModel;




@end
