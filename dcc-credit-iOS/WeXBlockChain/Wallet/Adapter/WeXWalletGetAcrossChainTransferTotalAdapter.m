//
//  WeXWalletGetAcrossChainTransferTotalAdapter.m
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletGetAcrossChainTransferTotalAdapter.h"

@implementation WeXWalletGetAcrossChainTransferTotalAdapter

- (NSString*)getRequestUrl
{
    return @"secure/forex/queryForexStatisticsInfo";
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
    return [WeXWalletGetAcrossChainTransferTotalResponseModal class];
}


@end
