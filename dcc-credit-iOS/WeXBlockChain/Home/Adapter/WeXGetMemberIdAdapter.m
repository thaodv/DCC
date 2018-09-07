//
//  WeXGetMemberIdAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/29.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXGetMemberIdAdapter.h"

@implementation WeXGetMemberIdAdapter

- (NSString*)getRequestUrl
{
    return @"member/getByIdentity";
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
    return [WeXGetMemberIdResponseModal class];
}

@end
