//
//  WeXFindLastPKAdapter.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/14.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXFindLastPKAdapter.h"


@implementation WeXFindLastPKAdapter

- (NSString*)getRequestUrl {
    return @"ss/duel/getLastDuel";
}

- (WexBaseUrlType)getBasetUrlType {
    return WexBaseUrlTypeDCCActivity;
}

-(WexNetAdapterRequestType)getNetAdapterRequestType {
    return WexNetAdapterRequestTypePOST;
}

- (Class)getResponseClass {
    return [WeXLastPKResModel class];
}

- (BOOL)isNeedBindWeChatToken {
    return false;
}
- (BOOL)isNeedSaveWeChatToken {
    return false;
}



@end
