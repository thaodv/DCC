//
//  WeXBorrowGetNonceAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/4/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBorrowGetNonceAdapter.h"

@implementation WeXBorrowGetNonceAdapter

- (NSString*)getRequestUrl
{
    return @"auth/getNonce";
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
    return [WeXBorrowGetNonceResponseModal class];
}

@end
