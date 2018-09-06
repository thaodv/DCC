//
//  WeXCPGetContractAddressAdapter.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/14.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCPGetContractAddressAdapter.h"

@implementation WeXCPGetContractAddressAdapter

- (NSString*)getRequestUrl
{
    return @"contract/1/getContractAddress/bsx_01";
}

- (WexBaseUrlType)getBasetUrlType
{
    return WexBaseUrlTypeDCCIpfsActivity;
}

-(WexNetAdapterRequestType)getNetAdapterRequestType
{
    return WexNetAdapterRequestTypeGET;
}

- (Class)getResponseClass
{
    return [WeXCPGetContractAddressResModel class];
}

@end
