//
//  WeXBankCardAdapter.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/12/5.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBankCardAdapter.h"

@implementation WeXBankCardAdapter

@end

@implementation WexBankListCardAdapter

- (NSString*)getRequestUrl {
    return @"secure/bank/tn_loan/queryBankInfo";
}

- (WexBaseUrlType)getBasetUrlType {
    return WexBaseUrlTypeDCCActivity;
}

-(WexNetAdapterRequestType)getNetAdapterRequestType {
    return WexNetAdapterRequestTypePOST;
}

- (BOOL)isNeedToken {
    return true;
}

- (Class)getResponseClass {
    return [WeXCreditGetBankListResListModal class];
}


@end


@implementation WexBindBankCardAdapter

- (NSString*)getRequestUrl {
    return @"secure/bank/tn_loan/bindingBankCard";
}

- (WexBaseUrlType)getBasetUrlType {
    return WexBaseUrlTypeDCCActivity;
}

-(WexNetAdapterRequestType)getNetAdapterRequestType {
    return WexNetAdapterRequestTypePOST;
}

- (BOOL)isNeedToken {
    return true;
}


@end


@implementation WexGetBindedCardAdapter


- (NSString*)getRequestUrl {
    return @"secure/bank/tn_loan/getBindingBankCard";
}

- (WexBaseUrlType)getBasetUrlType {
    return WexBaseUrlTypeDCCActivity;
}

-(WexNetAdapterRequestType)getNetAdapterRequestType {
    return WexNetAdapterRequestTypePOST;
}

- (BOOL)isNeedToken {
    return true;
}

- (Class)getResponseClass {
    return [WexBankCardItemModel class];
}


@end
