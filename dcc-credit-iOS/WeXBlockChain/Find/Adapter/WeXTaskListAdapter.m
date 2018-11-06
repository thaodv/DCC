//
//  WeXTaskListAdapter.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/6.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXTaskListAdapter.h"

@implementation WeXTaskListAdapter

- (NSString*)getRequestUrl {
    return @"bemember/ss/task/taskList";
}

- (WexBaseUrlType)getBasetUrlType {
    return WexBaseUrlTypeDCCActivity;
}

-(WexNetAdapterRequestType)getNetAdapterRequestType {
    return WexNetAdapterRequestTypePOST;
}

- (Class)getResponseClass {
    return [WeXTaskListResModel class];
}

- (BOOL)isNeedBindWeChatToken {
    return true;
}

- (BOOL)isNeedSaveWeChatToken {
    return true;
}


@end
