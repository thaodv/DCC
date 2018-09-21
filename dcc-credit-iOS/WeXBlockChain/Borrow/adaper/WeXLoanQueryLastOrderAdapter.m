//
//  WeXLoanQueryLastOrderAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/7.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXLoanQueryLastOrderAdapter.h"

@implementation WeXLoanQueryLastOrderAdapter

- (NSString*)getRequestUrl
{
    return @"secure/loan/getLastOrder";
}

-(BOOL)isNeedToken
{
    return YES;
}

-(WexNetAdapterRequestType)getNetAdapterRequestType
{
    return WexNetAdapterRequestTypePOST;
}

- (WexBaseUrlType)getBasetUrlType
{
    return WexBaseUrlTypeDCCActivity;
}

- (Class)getResponseClass
{
    return [WeXLoanQueryLastOrderResponseModal class];
}

@end
