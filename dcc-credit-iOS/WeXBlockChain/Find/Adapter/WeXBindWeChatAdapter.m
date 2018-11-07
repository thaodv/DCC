//
//  WeXBindWeChatAdapter.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/6.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBindWeChatAdapter.h"

@implementation WeXBindWeChatAdapter

- (NSString*)getRequestUrl {
    return @"bemember/bound/wechat";
}

- (WexBaseUrlType)getBasetUrlType {
    return WexBaseUrlTypeDCCActivity;
}

-(WexNetAdapterRequestType)getNetAdapterRequestType {
    return WexNetAdapterRequestTypePOST;
}

- (Class)getResponseClass {
    return [WeXBindWeChatResModel class];
}

- (BOOL)isNeedBindWeChatToken {
    return true;
}

//需要保存微信相关token
- (BOOL)isNeedSaveWeChatToken {
    return false;
}

@end
