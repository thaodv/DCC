//
//  WeXLoanGetOrderIdAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/5.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXLoanGetOrderIdAdapter.h"

@implementation WeXLoanGetOrderIdAdapter

- (NSString*)getRequestUrl
{
    return @"dcc/loan/1/getOrderByTx";
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
    return [WeXLoanGetOrderIdResponseModal class];
}


@end
