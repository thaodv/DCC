//
//  WeXGetOcrInfoAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/2/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXGetOcrInfoAdapter.h"

@implementation WeXGetOcrInfoAdapter

- (NSString*)getRequestUrl
{
    return @"cert/id/ocr";
}

- (WexBaseUrlType)getBasetUrlType
{
    return WexBaseUrlTypeLogic;
}


- (Class)getResponseClass
{
    return [WeXGetOcrInfoResponseModal class];
}

@end
