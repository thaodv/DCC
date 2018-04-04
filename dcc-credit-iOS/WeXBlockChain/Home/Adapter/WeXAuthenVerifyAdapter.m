//
//  WeXAuthenVerifyAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/2/28.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXAuthenVerifyAdapter.h"

@implementation WeXAuthenVerifyAdapter

- (NSString*)getRequestUrl
{
    return @"cert/id/verify";
}

- (WexBaseUrlType)getBasetUrlType
{
    return WexBaseUrlTypeLogic;
}


- (Class)getResponseClass
{
    return [WeXAuthenVerifyResponseModal class];
}

@end
