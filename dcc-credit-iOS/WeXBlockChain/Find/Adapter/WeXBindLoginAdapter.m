//
//  WeXBindLoginAdapter.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/2.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBindLoginAdapter.h"

@implementation WeXBindLoginAdapter

- (NSString*)getRequestUrl {
    return @"bemember/login";
}

- (WexBaseUrlType)getBasetUrlType {
    return WexBaseUrlTypeDCCActivity;
}

-(WexNetAdapterRequestType)getNetAdapterRequestType {
    return WexNetAdapterRequestTypePOST;
}

- (Class)getResponseClass {
    return [WeXBindLoginResModel class];
}

- (BOOL)isSignatureParamters {
    return true;
}

//需要保存绑定微信相关token
- (BOOL)isNeedSaveWeChatToken {
    return true;
}

@end
