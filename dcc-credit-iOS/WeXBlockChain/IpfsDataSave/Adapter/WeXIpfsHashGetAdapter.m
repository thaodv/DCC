//
//  WeXIpfsHashGetAdapter.m
//  WeXBlockChain
//
//  Created by wh on 2018/8/14.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXIpfsHashGetAdapter.h"

@implementation WeXIpfsHashGetAdapter

- (NSString*)getRequestUrl
{
    return @"contract/1/getContractAddress/ipfs_metadata";
}

- (BOOL)isNeedToken
{
    return YES;
}

-(WexNetAdapterRequestType)getNetAdapterRequestType
{
    return WexNetAdapterRequestTypeGET;
}

- (WexBaseUrlType)getBasetUrlType
{
    return WexBaseUrlTypeDCCIpfsActivity;
}

- (Class)getResponseClass
{
    return [WeXGetContractAddressResponseModal class];
}

@end
