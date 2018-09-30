//
//  WeXQueryProductByLenderCodeAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/2.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXQueryProductByLenderCodeAdapter.h"

@implementation WeXQueryProductByLenderCodeAdapter

- (NSString*)getRequestUrl {
    return @"loan/product/queryByLenderCode";
}


- (WexBaseUrlType)getBasetUrlType
{
    return WexBaseUrlTypeDCCActivity;
}

- (Class)getResponseClass
{
    return [WeXQueryProductByLenderCodeResponseModal class];
}

@end
