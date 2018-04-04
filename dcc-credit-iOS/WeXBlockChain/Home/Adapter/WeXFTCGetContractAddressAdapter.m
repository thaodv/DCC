//
//  WeXFTCGetContractAddressAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/3/21.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXFTCGetContractAddressAdapter.h"

@implementation WeXFTCGetContractAddressAdapter

- (NSString*)getRequestUrl
{
    return @"getContractAddress";
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
    return [WeXGetContractAddressResponseModal class];
}

@end
