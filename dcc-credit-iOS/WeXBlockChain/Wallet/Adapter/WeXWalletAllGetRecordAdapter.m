//
//  WeXWalletAllGetRecordAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/3/17.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletAllGetRecordAdapter.h"

@implementation WeXWalletAllGetRecordAdapter

- (NSString*)getRequestUrl
{
    return @"juzix/tokenTransfer";
}

- (WexBaseUrlType)getBasetUrlType
{
    return WexBaseUrlTypeAll;
}


- (Class)getResponseClass
{
    return [WeXWalletAllGetRecordResponseModal class];
}

@end
