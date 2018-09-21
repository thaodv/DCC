//
//  WeXTestLoginAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/4/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXTestLoginAdapter.h"

@implementation WeXTestLoginAdapter

- (NSString*)getRequestUrl
{
    return @"secure/ping";
}

-(BOOL)isNeedToken
{
    return YES;
}

-(WexNetAdapterRequestType)getNetAdapterRequestType
{
    return WexNetAdapterRequestTypeGET;
}

- (WexBaseUrlType)getBasetUrlType
{
    return WexBaseUrlTypeDCCActivity;
}

- (Class)getResponseClass
{
    return [WeXTestLoginResponseModal class];
}

@end
