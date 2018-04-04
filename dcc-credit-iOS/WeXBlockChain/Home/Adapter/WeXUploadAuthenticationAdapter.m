//
//  WeXUploadAuthenticationAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/2/28.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXUploadAuthenticationAdapter.h"

@implementation WeXUploadAuthenticationAdapter

- (NSString*)getRequestUrl
{
    return @"cert/1/apply";
}

- (WexBaseUrlType)getBasetUrlType
{
    return WexBaseUrlTypeAuthen;
}


- (Class)getResponseClass
{
    return [WeXUploadAuthenticationResponseModal class];
}

@end
