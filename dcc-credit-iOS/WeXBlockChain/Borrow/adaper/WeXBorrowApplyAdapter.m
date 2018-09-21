//
//  WeXBorrowApplyAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/4.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBorrowApplyAdapter.h"

@implementation WeXBorrowApplyAdapter

- (NSString*)getRequestUrl
{
    return @"dcc/loan/1/apply";
}

- (WexBaseUrlType)getBasetUrlType
{
    return WexBaseUrlTypeAuthen;
}


- (Class)getResponseClass
{
    return [WeXBorrowApplyResponseModal class];
}

@end
