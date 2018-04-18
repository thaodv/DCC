//
//  WeXWalletDigitalRecordDetailController.h
//  WeXBlockChain
//
//  Created by wcc on 2018/1/24.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseViewController.h"
#import "WeXWalletEtherscanGetRecordModal.h"
#import "WeXWalletDigitalGetTokenModal.h"

@interface WeXWalletDigitalRecordDetailController : WeXBaseViewController

@property (nonatomic,strong)WeXWalletEtherscanGetRecordResponseModal_item *recordModel;

@property (nonatomic,strong)WeXWalletDigitalGetTokenResponseModal_item *tokenModel;

@end
