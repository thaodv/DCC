//
//  WeXTaskGetSunBalanceAdapter.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/7.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXTaskGetSunBalanceAdapter.h"

@implementation WeXTaskGetSunBalanceAdapter

- (NSString*)getRequestUrl {
    return @"bemember/ss/player/balance";
}

- (WexBaseUrlType)getBasetUrlType {
    return WexBaseUrlTypeDCCActivity;
}

-(WexNetAdapterRequestType)getNetAdapterRequestType {
    return WexNetAdapterRequestTypePOST;
}

- (Class)getResponseClass {
    return [WeXTaskGetSunBalanceResModel class];
}

- (BOOL)isNeedBindWeChatToken {
    return true;
}
- (BOOL)isNeedSaveWeChatToken {
    return false;
}


@end
