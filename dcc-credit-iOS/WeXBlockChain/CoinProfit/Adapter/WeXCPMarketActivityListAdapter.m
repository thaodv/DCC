//
//  WeXCPMarketActivityListAdapter.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/25.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCPMarketActivityListAdapter.h"

@implementation WeXCPMarketActivityListAdapter

- (NSString*)getRequestUrl {
    return @"bsx/getActiveList";
}

- (WexBaseUrlType)getBasetUrlType {
    return WexBaseUrlTypeDCCActivity;
}

-(WexNetAdapterRequestType)getNetAdapterRequestType {
    return WexNetAdapterRequestTypeGET;
}

- (Class)getResponseClass {
    return [WeXCPActivityMainResModel class];
}

@end
