//
//  WeXLoanConfirmAdapter.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/12/6.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXLoanConfirmAdapter.h"

@implementation WeXLoanConfirmAdapter

- (NSString*)getRequestUrl {
    return @"secure/tn_loan/confirmLoan";
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


@implementation WeXGetConfirmLoanContractAdapter

- (NSString*)getRequestUrl {
    return @"contract/2/tn_loan_service";
}

- (WexBaseUrlType)getBasetUrlType {
    return WexBaseUrlTypeAuthen;
}

-(WexNetAdapterRequestType)getNetAdapterRequestType {
    return WexNetAdapterRequestTypeGET;
}

- (BOOL)isNeedToken {
    return true;
}

- (Class)getResponseClass {
    return [WeXConfirmLoanContractResModel class];
}

@end
