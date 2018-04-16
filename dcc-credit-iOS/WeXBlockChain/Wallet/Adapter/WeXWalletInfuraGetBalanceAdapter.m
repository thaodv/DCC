//
//  WeXWalletInfuraGetBalanceAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/19.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletInfuraGetBalanceAdapter.h"

@implementation WeXWalletInfuraGetBalanceAdapter

- (NSString*)getRequestUrl
{
    return @"eth_getBalance";
}

- (WexBaseUrlType)getBasetUrlType
{
    return WexBaseUrlTypeInfura;
}

-(WexNetAdapterRequestType)getNetAdapterRequestType
{
    return WexNetAdapterRequestTypeGET;
}

- (Class)getResponseClass
{
    return [WeXWalletInfuraGetBalanceResponseModal class];
}

@end
