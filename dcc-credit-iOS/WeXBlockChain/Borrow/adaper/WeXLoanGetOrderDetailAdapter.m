//
//  WeXLoanGetOrderDetailAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/9.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXLoanGetOrderDetailAdapter.h"

@implementation WeXLoanGetOrderDetailAdapter

- (NSString*)getRequestUrl
{
    return @"secure/loan/getByChainOrderId";
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
    return [WeXLoanGetOrderDetailResponseModal class];
}


@end