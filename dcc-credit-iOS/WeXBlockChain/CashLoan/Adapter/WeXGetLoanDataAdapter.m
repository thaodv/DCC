//
//  WeXGetLoanDataAdapter.m
//  WeXBlockChain
//
//  Created by wh on 2018/12/11.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXGetLoanDataAdapter.h"

@implementation WeXGetLoanDataAdapter

- (NSString*)getRequestUrl {
    return @"secure/tn_loan/getLoanCalculationInfo";
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
    return [WeXGetLoadDataResponseModal class];
}

@end
