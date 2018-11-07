//
//  WeXSunBalanceRecordAdapter.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/7.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXSunBalanceRecordAdapter.h"

@implementation WeXSunBalanceRecordAdapter

- (NSString*)getRequestUrl {
    return @"bemember/ss/changeOrder/query";
}

- (WexBaseUrlType)getBasetUrlType {
    return WexBaseUrlTypeDCCActivity;
}

-(WexNetAdapterRequestType)getNetAdapterRequestType {
    return WexNetAdapterRequestTypePOST;
}

- (Class)getResponseClass {
    return [WeXSunBlanceResModel class];
}

- (BOOL)isNeedBindWeChatToken {
    return true;
}
- (BOOL)isNeedSaveWeChatToken {
    return false;
}

@end
