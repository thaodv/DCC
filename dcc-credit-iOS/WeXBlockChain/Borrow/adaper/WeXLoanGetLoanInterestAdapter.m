//
//  WeXLoanGetLoanInterestAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/9.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXLoanGetLoanInterestAdapter.h"

@implementation WeXLoanGetLoanInterestAdapter

- (NSString*)getRequestUrl
{
    return @"loan/getLoanInterest";
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
    return [WeXLoanGetLoanInterestResponseModal class];
}

@end
