//
//  WeXBorrowGetContractAddressAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/4.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBorrowGetContractAddressAdapter.h"

@implementation WeXBorrowGetContractAddressAdapter

- (NSString*)getRequestUrl
{
    return @"dcc/loan/1/getContractAddress";
}

-(WexNetAdapterRequestType)getNetAdapterRequestType
{
    return WexNetAdapterRequestTypeGET;
}

- (WexBaseUrlType)getBasetUrlType
{
    return WexBaseUrlTypeAuthen;
}

- (Class)getResponseClass
{
    return [WeXGetContractAddressResponseModal class];
}

@end
