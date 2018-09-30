//
//  WeXBorrowLoginAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/4/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBorrowLoginAdapter.h"


@implementation WeXBorrowLoginAdapter

- (NSString*)getRequestUrl
{
    return @"login";
}

// 参数是否需要加签
- (BOOL)isSignatureParamters
{
    return YES;
}

- (WexBaseUrlType)getBasetUrlType
{
    return WexBaseUrlTypeDCCActivity;
}

- (Class)getResponseClass
{
    return [WeXBorrowLoginResponseModal class];
}

@end
