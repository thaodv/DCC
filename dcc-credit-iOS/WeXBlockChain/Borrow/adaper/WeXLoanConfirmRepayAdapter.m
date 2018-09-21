//
//  WeXLoanConfirmRepayAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/9.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXLoanConfirmRepayAdapter.h"

@implementation WeXLoanConfirmRepayAdapter

- (NSString*)getRequestUrl
{
    return @"secure/loan/confirmRepayment";
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
    return [WeXLoanConfirmRepayResponseModal class];
}


@end
