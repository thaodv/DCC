//
//  WeXWalletDigitalGetQuoteAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/23.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletDigitalGetQuoteAdapter.h"

@implementation WeXWalletDigitalGetQuoteAdapter

- (NSString*)getRequestUrl
{
    return @"quote/query/today/variety";
}

- (WexBaseUrlType)getBasetUrlType
{
    return WexBaseUrlTypeDigitalAsset;
}

- (Class)getResponseClass
{
    return [WeXWalletDigitalGetQuoteResponseModal class];
}

@end
