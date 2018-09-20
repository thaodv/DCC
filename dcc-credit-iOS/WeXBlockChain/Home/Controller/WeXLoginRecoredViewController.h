//
//  WeXLoginRecoredViewController.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/20.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseReuseTableViewController.h"
typedef NS_ENUM(NSInteger,WeXLoginRecoredType) {
    WeXLoginRecoredTypeLogin = 0, //登录记录
    WeXLoginRecoredTypeChange= 1, //变更记录
};

@interface WeXLoginRecoredViewController : WeXBaseReuseTableViewController

@property (nonatomic, strong) NSArray *dataArray;
@property (nonatomic, assign) WeXLoginRecoredType type;


@end
