//
//  WeXWalletInfuraGetGasPriceAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/18.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletInfuraGetGasPriceAdapter.h"

@implementation WeXWalletInfuraGetGasPriceAdapter

- (NSString*)getRequestUrl
{
    return @"eth_gasPrice";
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
    return [WeXWalletInfuraGetGasPriceResponseModal class];
}

@end
