//
//  WeXLoanGetOrderIdModal.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/5.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXLoanGetOrderIdModal.h"

@implementation WeXLoanGetOrderIdParamModal

@end

@implementation WeXLoanGetOrderIdResponseModal_item

+(JSONKeyMapper*)keyMapper
{
    return [[JSONKeyMapper alloc] initWithModelToJSONDictionary:@{
                                                                  @"orderId": @"id",
                                                                  }];
}

@end

@implementation WeXLoanGetOrderIdResponseModal

@end

