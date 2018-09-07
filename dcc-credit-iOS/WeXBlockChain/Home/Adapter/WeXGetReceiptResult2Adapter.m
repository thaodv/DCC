//
//  WeXGetReceiptResult2Adapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/7.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXGetReceiptResult2Adapter.h"

@implementation WeXGetReceiptResult2Adapter


- (NSString*)getRequestUrl
{
    return @"receipt/1/getReceiptResult";
}

- (WexBaseUrlType)getBasetUrlType
{
    return WexBaseUrlTypeAuthen;
}

-(WexNetAdapterRequestType)getNetAdapterRequestType
{
    return WexNetAdapterRequestTypeGET;
}

- (Class)getResponseClass
{
    return [WeXGetReceiptResult2ResponseModal class];
}

@end
