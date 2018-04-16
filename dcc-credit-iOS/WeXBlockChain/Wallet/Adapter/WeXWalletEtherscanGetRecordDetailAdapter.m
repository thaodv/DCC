//
//  WeXWalletEtherscanGetRecordDetailAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/30.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletEtherscanGetRecordDetailAdapter.h"

@implementation WeXWalletEtherscanGetRecordDetailAdapter

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
    return [WeXWalletEtherscanGetRecordDetailResponseModal class];
}

@end
