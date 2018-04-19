//
//  WeXWalletInfuraGetBalanceModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/1/19.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"


@interface WeXWalletInfuraGetBalanceParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *params;


@property (nonatomic, copy)NSString *token;

@end

@interface  WeXWalletInfuraGetBalanceResponseModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *result;

@end
