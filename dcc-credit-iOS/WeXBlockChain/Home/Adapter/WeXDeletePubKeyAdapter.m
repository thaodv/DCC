//
//  WeXDeletePubKeyAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/27.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXDeletePubKeyAdapter.h"

@implementation WeXDeletePubKeyAdapter


- (NSString*)getRequestUrl
{
    return @"ca/1/deletePubKey";
}


- (WexBaseUrlType)getBasetUrlType
{
    return WexBaseUrlTypeAuthen;
}

- (Class)getResponseClass
{
    return [WeXDeletePubKeyResponseModal class];
}


@end
