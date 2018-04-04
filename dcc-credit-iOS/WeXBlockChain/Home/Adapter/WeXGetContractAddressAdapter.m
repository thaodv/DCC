//
//  WeXGetContractAddressAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/15.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXGetContractAddressAdapter.h"

@implementation WeXGetContractAddressAdapter

- (NSString*)getRequestUrl
{
    return @"getContractAddress";
}

-(WexNetAdapterRequestType)getNetAdapterRequestType
{
    return WexNetAdapterRequestTypeGET;
}

- (Class)getResponseClass
{
    return [WeXGetContractAddressResponseModal class];
}

@end
