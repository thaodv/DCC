//
//  WeXCPPotListAdapter.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/25.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCPPotListAdapter.h"


@implementation WeXCPPotListAdapter
- (NSString*)getRequestUrl {
    return @"bsx/getPositionList";
}

- (WexBaseUrlType)getBasetUrlType {
    return WexBaseUrlTypeDCCActivity;
}

-(WexNetAdapterRequestType)getNetAdapterRequestType {
    return WexNetAdapterRequestTypeGET;
}

- (Class)getResponseClass {
    return [WeXCPPotListMainModel class];
}

@end
