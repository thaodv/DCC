//
//  WeXLoanDownloadContractAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/15.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXLoanDownloadContractAdapter.h"

@implementation WeXLoanDownloadContractAdapter

- (NSString*)getRequestUrl
{
    return @"secure/loan/download/agreement";
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
    return [WeXLoanDownloadContractResponseModal class];
}

@end
