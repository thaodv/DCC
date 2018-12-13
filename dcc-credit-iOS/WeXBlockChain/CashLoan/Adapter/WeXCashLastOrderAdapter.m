//
//  WeXCashLastOrderAdapter.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/12/4.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCashLastOrderAdapter.h"

@implementation WeXCashLastOrderAdapter

- (NSString*)getRequestUrl {
    return @"secure/tn_loan/getLastOrder";
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
