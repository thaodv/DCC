//
//  WeXWalletEtherscanGetRecordAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/24.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletEtherscanGetRecordAdapter.h"

@implementation WeXWalletEtherscanGetRecordAdapter

- (NSString*)getRequestUrl
{
    return @"";
}

- (WexBaseUrlType)getBasetUrlType
{
    return WexBaseUrlTypeEtherscan;
}

- (Class)getResponseClass
{
    return [WeXWalletEtherscanGetRecordResponseModal class];
}

@end
