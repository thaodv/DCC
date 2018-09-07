//
//  WeXDisPlayAdapter.m
//  WeXBlockChain
//
//  Created by wh on 2018/8/20.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXDisPlayAdapter.h"

@implementation WeXDisPlayAdapter

- (NSString*)getRequestUrl
{
    return @"version/query";
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
    return [WeXOpenResResultModel class];
}

@end
