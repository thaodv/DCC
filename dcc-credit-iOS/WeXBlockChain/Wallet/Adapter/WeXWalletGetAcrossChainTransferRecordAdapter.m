//
//  WeXWalletGetAcrossChainTransferRecordAdapter.m
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/25.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletGetAcrossChainTransferRecordAdapter.h"

@implementation WeXWalletGetAcrossChainTransferRecordAdapter

- (NSString*)getRequestUrl
{
    return @"secure/forex/queryForexOrderPage";
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
    return [WeXWalletGetAcrossChainTransferRecordResponseModal class];
}

@end
