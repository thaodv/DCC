//
//  WeXCashLoadNewRepaymentAdapter.m
//  WeXBlockChain
//
//  Created by wh on 2018/12/11.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCashLoadNewRepaymentAdapter.h"

@implementation WeXCashLoadNewRepaymentAdapter

- (NSString*)getRequestUrl {
    return @"secure/tn_loan/repay";
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
