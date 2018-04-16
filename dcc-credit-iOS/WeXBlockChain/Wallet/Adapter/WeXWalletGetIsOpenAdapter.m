//
//  WeXWalletGetIsOpenAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/2/2.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletGetIsOpenAdapter.h"

@implementation WeXWalletGetIsOpenAdapter

- (NSString*)getRequestUrl
{
    return @"secure/ico/project/get/versionStatusByAppName";
}

- (WexBaseUrlType)getBasetUrlType
{
    return WexBaseUrlTypeDigitalAsset;
}

- (Class)getResponseClass
{
    return [WeXWalletGetIsOpenResponseModal class];
}

@end
