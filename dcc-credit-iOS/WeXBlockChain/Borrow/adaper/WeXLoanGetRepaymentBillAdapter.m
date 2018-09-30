//
//  WeXLoanGetRepaymentBillAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/9.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXLoanGetRepaymentBillAdapter.h"

@implementation WeXLoanGetRepaymentBillAdapter

- (NSString*)getRequestUrl
{
    return @"/secure/loan/getRepaymentBill";
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
    return [WeXLoanGetRepaymentBillResponseModal class];
}

@end
