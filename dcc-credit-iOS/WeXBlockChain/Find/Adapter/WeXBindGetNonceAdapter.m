//
//  WeXBindGetNonceAdapter.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/6.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBindGetNonceAdapter.h"

@implementation WeXBindGetNonceAdapter

- (NSString*)getRequestUrl {
    return @"auth/getNonce2";
}

- (WexBaseUrlType)getBasetUrlType {
    return WexBaseUrlTypeDCCActivity;
}

-(WexNetAdapterRequestType)getNetAdapterRequestType {
    return WexNetAdapterRequestTypeGET;
}

- (Class)getResponseClass {
    return [WeXBindGetNonceResModel class];
}


@end
