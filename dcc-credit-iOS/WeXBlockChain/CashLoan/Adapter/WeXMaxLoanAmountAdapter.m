//
//  WeXMaxLoanAmountAdapter.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/12/5.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXMaxLoanAmountAdapter.h"

@implementation WeXMaxLoanAmountAdapter

- (NSString*)getRequestUrl {
    return @"secure/tn_loan/getMaximumAmount";
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
    return [WeXLoanMaxAmountResModel class];
}

@end
