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
    return @"getPubKey";
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
