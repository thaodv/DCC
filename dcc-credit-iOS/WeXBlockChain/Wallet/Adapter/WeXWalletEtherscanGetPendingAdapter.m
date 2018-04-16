//
//  WeXWalletEtherscanGetPendingAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/31.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletEtherscanGetPendingAdapter.h"
#import "WeXWalletEtherscanGetPendingModal.h"

@implementation WeXWalletEtherscanGetPendingAdapter

- (NSString*)getRequestUrl
{
    return @"";
}

- (WexBaseUrlType)getBasetUrlType
{
    return WexBaseUrlTypeEtherscan;
}

- (WexNetAdapterRequestType)getNetAdapterRequestType
{
    return WexNetAdapterRequestTypeGET;
}

- (Class)getResponseClass
{
    return [WeXWalletEtherscanGetPendingResponseModal class];
}

@end
