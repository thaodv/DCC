//
//  WeXGetDefaultLoanerAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/2.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXGetDefaultLoanerAdapter.h"

@implementation WeXGetDefaultLoanerAdapter

- (NSString*)getRequestUrl
{
    return @"getDefault";
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
    return [WeXGetDefaultLoanerResponseModal class];
}

@end
