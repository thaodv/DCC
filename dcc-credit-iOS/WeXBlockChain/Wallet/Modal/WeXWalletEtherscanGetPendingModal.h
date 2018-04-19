//
//  WeXWalletEtherscanGetPendingModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/1/31.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface WeXWalletEtherscanGetPendingParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *module;
@property (nonatomic,copy)NSString *action;
@property (nonatomic,copy)NSString *txhash;
@end

@interface  WeXWalletEtherscanGetPendingResponseModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *result;

@end
