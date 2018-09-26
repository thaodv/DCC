//
//  WeXCPPotListViewController.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/19.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseReuseTableViewController.h"

@class WeXCPUserPotAssetResModel;

@interface WeXCPPotListViewController : WeXBaseReuseTableViewController
@property (nonatomic, strong) WeXCPUserPotAssetResModel  * userPotAssetModel;
@end
