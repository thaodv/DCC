//
//  WeXGetReceiptResultAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/21.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXGetReceiptResultAdapter.h"

@implementation WeXGetReceiptResultAdapter


- (NSString*)getRequestUrl
{
    return @"receipt/1/hasReceipt";
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
    return [WeXGetReceiptResultResponseModal class];
}

@end
