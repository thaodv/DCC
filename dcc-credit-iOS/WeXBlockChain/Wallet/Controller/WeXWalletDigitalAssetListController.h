//
//  WeXWalletDigitalAssetListController.h
//  WeXBlockChain
//
//  Created by wcc on 2018/1/17.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseViewController.h"

@interface WeXWalletDigitalAssetListController : WeXBaseViewController<UINavigationControllerDelegate>

@property (strong, nonatomic) UIView *cardView;

@property (nonatomic,strong)NSMutableArray *datasArray;

@property (nonatomic,strong)UIView *tempView;

@property (nonatomic,strong)NSString *allPraceStr;


/**
 用来区分是否是Tab页面
 */
@property (nonatomic, assign) BOOL isTab;


@end
