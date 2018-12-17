//
//  WeXFindAwardAdapter.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/14.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXFindAwardAdapter.h"

@implementation WeXFindAwardAdapter

- (NSString*)getRequestUrl {
    return @"bemember/ss/flower/queryFlower";
}

- (WexBaseUrlType)getBasetUrlType {
    return WexBaseUrlTypeDCCActivity;
}

-(WexNetAdapterRequestType)getNetAdapterRequestType {
    return WexNetAdapterRequestTypePOST;
}

- (Class)getResponseClass {
    return [WeXFindAwardResModel class];
}

- (BOOL)isNeedBindWeChatToken {
    return true;
}
- (BOOL)isNeedSaveWeChatToken {
    return false;
}



@end
