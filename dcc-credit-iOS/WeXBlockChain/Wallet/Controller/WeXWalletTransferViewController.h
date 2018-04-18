//
//  WeXWalletTransferViewController.h
//  WeXBlockChain
//
//  Created by wcc on 2018/1/18.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseViewController.h"
#import "WeXWalletDigitalGetTokenModal.h"

@interface WeXWalletTransferViewController : WeXBaseViewController

@property (nonatomic,strong)WeXWalletDigitalGetTokenResponseModal_item *tokenModel;

@property (nonatomic,copy)NSString *feeRate;

@end
