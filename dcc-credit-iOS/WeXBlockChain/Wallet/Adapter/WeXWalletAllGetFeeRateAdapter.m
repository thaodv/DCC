//
//  WeXWalletAllGetFeeRateAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/3/21.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletAllGetFeeRateAdapter.h"

@implementation WeXWalletAllGetFeeRateAdapter

- (NSString*)getRequestUrl
{
    return @"getFeeRate";
}

-(WexNetAdapterRequestType)getNetAdapterRequestType
{
    return WexNetAdapterRequestTypeGET;
}

- (WexBaseUrlType)getBasetUrlType
{
    return WexBaseUrlTypeFTC;
}

- (Class)getResponseClass
{
    return [WeXWalletAllGetFeeRateResponseModal class];
}

@end
