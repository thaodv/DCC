//
//  WeXSunBalanceDetailModel.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/7.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXSunBalanceDetailModel.h"

@implementation WeXSunBalanceDetailModel

@end


@implementation WeXSunBalanceDetailParamModel
+ (JSONKeyMapper *)keyMapper {
    return [[JSONKeyMapper alloc] initWithModelToJSONDictionary:@{@"idNum":@"id"}];
}

@end


@implementation WeXSunBalanceDetailResModel

+ (JSONKeyMapper *)keyMapper {
    return [[JSONKeyMapper alloc] initWithModelToJSONDictionary:@{@"idNum":@"id"}];
}

@end
