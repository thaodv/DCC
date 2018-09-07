//
//  WeXGetPubKeyAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/27.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXGetPubKeyAdapter.h"

@implementation WeXGetPubKeyAdapter


- (NSString*)getRequestUrl
{
    return @"ca/1/getPubKey";
}

- (WexBaseUrlType)getBasetUrlType
{
    return WexBaseUrlTypeAuthen;
}

-(WexNetAdapterRequestType)getNetAdapterRequestType
{
    return WexNetAdapterRequestTypeGET;
}

- (Class)getResponseClass
{
    return [WeXGetPubKeyResponseModal class];
}

@end
