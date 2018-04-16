//
//  WeXWalletEthplorerGetRecordAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/30.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletEthplorerGetRecordAdapter.h"

@implementation WeXWalletEthplorerGetRecordAdapter

- (NSString*)getRequestUrl
{
//    NSString *address = @"0x194a0d4139bd1ea6f471dd6b7c3a241479292ac6";
    NSString *requestUrl = [NSString stringWithFormat:@"getAddressHistory/%@",[WexCommonFunc getFromAddress]];
    return requestUrl;
}


- (WexNetAdapterRequestType)getNetAdapterRequestType
{
    return WexNetAdapterRequestTypeGET;
}


- (WexBaseUrlType)getBasetUrlType
{
    return WexBaseUrlTypeEthplorer;
}

- (Class)getResponseClass
{
    return [WeXWalletEthplorerGetRecordResponseModal class];
}

@end
