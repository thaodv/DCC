//
//  WeXAuthenResultAdapter.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/12/5.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXAuthenResultAdapter.h"

@implementation WeXAuthenResultAdapter

- (NSString*)getRequestUrl {
    return @"secure/tn_loan/getAuditResult";
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
    return [WeXLoanAuthenResultResModel class];
}
@end
