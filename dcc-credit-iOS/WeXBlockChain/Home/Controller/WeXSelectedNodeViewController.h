//
//  WeXSelectedNodeViewController.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/7.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseReuseTableViewController.h"
#import "WeXNetworkCheckModel.h"

@class WeXNetworkCheckModel;

@interface WeXSelectedNodeViewController : WeXBaseReuseTableViewController

@property (nonatomic,copy) void (^DidSelectedNode)(WeXNetworkCheckModel *);


@end
