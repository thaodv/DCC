//
//  WeXLoanVerifyAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/5.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXLoanVerifyAdapter.h"

@implementation WeXLoanVerifyAdapter

- (NSString*)getRequestUrl
{
    return @"secure/loan/apply";
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
    return [WeXLoanVerifyResponseModal class];
}

@end
