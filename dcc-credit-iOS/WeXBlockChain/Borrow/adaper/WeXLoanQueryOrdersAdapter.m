//
//  WeXLoanQueryOrdersAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/5.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXLoanQueryOrdersAdapter.h"

@implementation WeXLoanQueryOrdersAdapter

- (NSString*)getRequestUrl
{
    return @"secure/loan/queryOrderPage";
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
    return [WeXLoanQueryOrdersResponseModal class];
}

@end
