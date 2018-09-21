//
//  WeXQueryProductByLenderCodeModal.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/2.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXQueryProductByLenderCodeModal.h"

@implementation WeXQueryProductByLenderCodeParamModal

@end

@implementation WeXQueryProductByLenderCodeResponseModal_currency

@end

@implementation WeXQueryProductByLenderCodeResponseModal_period

@end

@implementation WeXQueryProductByLenderCodeResponseModal_lender

@end

@implementation WeXQueryProductByLenderCodeResponseModal_item

+(JSONKeyMapper*)keyMapper
{
    return [[JSONKeyMapper alloc] initWithModelToJSONDictionary:@{
                                                       @"productId": @"id",
                                                       @"productDescription":@"description"
                                                       }];
}



@end

@implementation WeXQueryProductByLenderCodeResponseModal

@end
