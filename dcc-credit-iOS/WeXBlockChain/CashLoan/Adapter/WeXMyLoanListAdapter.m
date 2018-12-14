//
//  WeXMyLoanListAdapter.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/12/7.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXMyLoanListAdapter.h"

@implementation WeXMyLoanListAdapter

- (NSString*)getRequestUrl {
    return @"secure/tn_loan/queryOrderPage";
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
    return [WeXMyLoanListResModel class];
}


@end


// MARK: - 借款订单详情
@implementation WeXMyLoanListDetailAdapter
- (NSString*)getRequestUrl {
    return @"secure/tn_loan/getTNLoanOrderDetail";
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
    return [WeXMyLoanListDetailResModel class];
}

@end
