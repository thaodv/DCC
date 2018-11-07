//
//  WeXTaskSignRequestAdapter.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/7.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXTaskSignRequestAdapter.h"

@implementation WeXTaskSignRequestAdapter

- (NSString*)getRequestUrl {
    return @"bemember/ss/attendence/apply";
}

- (WexBaseUrlType)getBasetUrlType {
    return WexBaseUrlTypeDCCActivity;
}

-(WexNetAdapterRequestType)getNetAdapterRequestType {
    return WexNetAdapterRequestTypePOST;
}

- (Class)getResponseClass {
    return [WeXTaskSignResListModel class];
}

- (BOOL)isNeedBindWeChatToken {
    return true;
}

- (BOOL)isNeedSaveWeChatToken {
    return false;
}


@end
