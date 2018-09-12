//
//  WeXWalletGetDccAcrossChainInfoAdapter.m
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletGetDccAcrossChainInfoAdapter.h"

@implementation WeXWalletGetDccAcrossChainInfoAdapter

- (NSString*)getRequestUrl
{
    return @"secure/forex/getForexConfig";
}

-(BOOL)isNeedToken
{
    return YES;
}

-(WexNetAdapterRequestType)getNetAdapterRequestType
{
    return WexNetAdapterRequestTypePOST;
}

- (WexBaseUrlType)getBasetUrlType
{
    return WexBaseUrlTypeDCCActivity;
}

- (Class)getResponseClass
{
    return [WeXWalletGetDccAcrossChainInfoResponseModal class];
}

@end
