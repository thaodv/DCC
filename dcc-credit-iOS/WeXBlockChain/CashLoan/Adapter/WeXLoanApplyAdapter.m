//
//  WeXLoanApplyAdapter.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/12/5.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXLoanApplyAdapter.h"


@implementation WexLoanCreateOrderAdapter
- (NSString*)getRequestUrl {
    return @"secure/tn_loan/createLoanOrder";
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
    return [WexLoanLastOrderResModel class];
}

@end


@implementation WeXLoanApplyAdapter

- (NSString*)getRequestUrl {
    return @"secure/tn_loan/apply";
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


@implementation WeXLoanOrderAdapter
- (NSString*)getRequestUrl {
    return @"secure/tn_loan/getTNLoanOrder";
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
    return [WexLoanLastOrderResModel class];
}


@end
