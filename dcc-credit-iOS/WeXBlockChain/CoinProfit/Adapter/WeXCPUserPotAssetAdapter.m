
//
//  WeXCPUserPotAssetAdapter.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCPUserPotAssetAdapter.h"

@implementation WeXCPUserPotAssetAdapter

- (NSString*)getRequestUrl {
    return @"bsx/getPositionSum";
}

- (WexBaseUrlType)getBasetUrlType {
    return WexBaseUrlTypeDCCActivity;
}

-(WexNetAdapterRequestType)getNetAdapterRequestType {
    return WexNetAdapterRequestTypeGET;
}

- (Class)getResponseClass {
    return [WeXCPUserPotAssetResModel class];
}

@end
