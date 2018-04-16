//
//  WeXWalletDigitalGetTokenAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/23.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletDigitalGetTokenAdapter.h"

@implementation WeXWalletDigitalGetTokenAdapter

- (NSString*)getRequestUrl
{
    return @"token/query";
}

- (WexBaseUrlType)getBasetUrlType
{
    return WexBaseUrlTypeDigitalAsset;
}

- (Class)getResponseClass
{
    return [WeXWalletDigitalGetTokenResponseModal class];
}

@end
