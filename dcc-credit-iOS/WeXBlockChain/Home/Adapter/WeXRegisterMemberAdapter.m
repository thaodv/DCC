//
//  WeXRegisterMemberAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/29.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXRegisterMemberAdapter.h"

@implementation WeXRegisterMemberAdapter

- (NSString*)getRequestUrl
{
    return @"member/register";
}

-(BOOL)isSignatureParamters
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
    return [WeXRegisterMemberResponseModal class];
}


@end
