//
//  WeXQuiryRepaymentPlanAdapter.m
//  WeXBlockChain
//
//  Created by wh on 2018/12/11.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXQuiryRepaymentPlanAdapter.h"

@implementation WeXQuiryRepaymentPlanAdapter

- (NSString*)getRequestUrl {
    return @"secure/tn_loan/getTNRepayPlan";
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
    return [WeXMyLoanDetailModel class];
}

@end
