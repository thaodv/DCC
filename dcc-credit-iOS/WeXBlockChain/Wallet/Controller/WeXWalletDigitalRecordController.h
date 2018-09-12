//
//  WeXWalletDigitalRecordController.h
//  WeXBlockChain
//
//  Created by wcc on 2018/1/24.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseViewController.h"
#import "WeXWalletDigitalGetTokenModal.h"

@interface WeXWalletDigitalRecordController : WeXBaseViewController

@property (nonatomic,strong)WeXWalletDigitalGetTokenResponseModal_item *tokenModel;

//是否是私链
@property (nonatomic,assign)BOOL isPrivateChain;

@end
