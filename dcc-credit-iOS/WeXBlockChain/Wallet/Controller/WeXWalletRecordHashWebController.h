//
//  WeXWalletRecordHashWebController.h
//  WeXBlockChain
//
//  Created by wcc on 2018/1/29.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseViewController.h"

typedef NS_ENUM(NSInteger,WeXWalletRecordHashType){
    WeXWalletRecordHashTypePublic,
    WeXWalletRecordHashTypePrivate
};

@interface WeXWalletRecordHashWebController : WeXBaseViewController

@property (nonatomic,copy)NSString *txHash;

@property (nonatomic,assign)WeXWalletRecordHashType type;

@end
