//
//  WeXAuthenGetOrderIdAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/2/28.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXAuthenGetOrderIdAdapter.h"

@implementation WeXAuthenGetOrderIdAdapter

- (NSString*)getRequestUrl
{
    return @"cert/1/getOrderByTx";
}

-(WexNetAdapterRequestType)getNetAdapterRequestType
{
    return WexNetAdapterRequestTypeGET;
}

- (WexBaseUrlType)getBasetUrlType
{
    return WexBaseUrlTypeAuthen;
}


- (Class)getResponseClass
{
    return [WeXAuthenGetOrderIdResponseModal class];
}

@end
