//
//  WeXAgentMarketAdapter.m
//  WeXBlockChain
//
//  Created by wh on 2018/7/23.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXAgentMarketAdapter.h"

@implementation WeXAgentMarketAdapter

- (NSString*)getRequestUrl
{
    return @"redis/coinMarketCap";
}

-(WexNetAdapterRequestType)getNetAdapterRequestType
{
    return WexNetAdapterRequestTypeGET;
}

- (WexBaseUrlType)getBasetUrlType
{
    return WexBaseUrlTypeDCCAgentMarket;
}

- (Class)getResponseClass
{
    return [WeXAgentMarketResponseModel class];
}


@end
