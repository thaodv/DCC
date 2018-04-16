//
//  WeXWalletEtherscanGetPendingAdapter.h
//  WeXBlockChain
//
//  Created by wcc on 2018/1/31.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WexBaseNetAdapter.h"
#import "WeXWalletEtherscanGetPendingModal.h"

@interface WeXWalletEtherscanGetPendingAdapter : WexBaseNetAdapter

@property (nonatomic,copy)NSString *txhash;

@end
