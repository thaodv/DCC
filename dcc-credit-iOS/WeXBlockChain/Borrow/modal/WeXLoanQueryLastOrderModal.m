//
//  WeXLoanQueryLastOrderModal.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/7.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXLoanQueryLastOrderModal.h"

@implementation WeXLoanQueryLastOrderParamModal

@end

@implementation WeXLoanQueryLastOrderResponseModal

+(JSONKeyMapper*)keyMapper
{
    return [[JSONKeyMapper alloc] initWithModelToJSONDictionary:@{
                                                                  @"orderId": @"id",
                                                                  }];
}


@end
